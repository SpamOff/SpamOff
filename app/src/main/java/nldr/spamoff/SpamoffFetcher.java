package nldr.spamoff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;
import nldr.spamoff.Networking.NetworkManager;
import nldr.spamoff.SMSHandler.SMSReader;
import nldr.spamoff.SMSHandler.SMSToJson;
import nldr.spamoff.ScanFinished;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by Lior on 12/11/2016.
 */
public class SpamoffFetcher {

    Activity activity;
    Context context;
    AppCompatActivity calllingClass;

    private String[] permissionsArray =
            new String[] {
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.READ_SMS
            };

    public SpamoffFetcher(Activity activity, Context context, AppCompatActivity calllingClass){
        this.activity = activity;
        this.context = context;
        this.calllingClass = calllingClass;
    }

    public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {

        boolean bAcceptedAll = true;

        // IT if it null - there are already permissions
        if (permissions != null && grantResults != null) {

            // Goes on each result and checks if it is granted
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED)
                    bAcceptedAll = false;
            }
        }

        if (bAcceptedAll) {
            this.fetchWithPermissions();
        } else {
            new MaterialDialog.Builder(context)
                    .content("כדי שנוכל לבצע את הסריקה יש לאשר את הגישה של האפליקציה לאנשי הקשר וההודעות.")
                    .title("לא נמצאו האישורים המתאימים לביצוע הסריקה")
                    .titleGravity(GravityEnum.END)
                    .buttonsGravity(GravityEnum.END)
                    .contentGravity(GravityEnum.END)
                    .positiveText("אוקי").show();
        }
    }

    public void fetchIfPermitted() {

        List<String> deniededPermissions = new ArrayList<String>();

        for (String curr : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, curr) != PackageManager.PERMISSION_GRANTED) {

                deniededPermissions.add(curr);
            }
        }

        if (deniededPermissions.size() > 0) {
            ActivityCompat.requestPermissions(activity,
                    deniededPermissions.toArray(new String[deniededPermissions.size()]), 0);
        } else {
            calllingClass.onRequestPermissionsResult(0, null, null);
        }
    }

    private void fetchWithPermissions() {

        final ProgressDialog progressDialog = new ProgressDialog(context, "טוען את ההודעות החדשות...");
        progressDialog.show();

        final JSONArray[] finalJsonObject = new JSONArray[] {new JSONArray()};

        Runnable smsCollector = new Runnable() {
            @Override
            public void run() {
                try {
                    Date lastScanDate = new Date(CookiesHandler.getLastScanDate(context));
                    //JSONObject jsonObject = SMSToJson.parseAll(context, SMSReader.read(context, lastScanDate));
                    JSONArray jsonArray = SMSToJson.parseAllToArray(context, SMSReader.read(context, lastScanDate));
                    finalJsonObject[0] = jsonArray;
                    CookiesHandler.setLastScanMessagesCount(context, jsonArray.length());
                    CookiesHandler.setLastScanDate(context, System.currentTimeMillis());
                    CookiesHandler.setIfAlreadyScannedBefore(context, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        Intent intent = new Intent(context, ScanFinished.class);
                        activity.startActivity(intent);
                    }
                });

                if (NetworkManager.sendJsonToServer(context, finalJsonObject[0])) {
                    Log.d("Good", "Sent to the server");
                } else {
                    Log.d("Problem", "Problem");
                }
            }
        };

        activity.runOnUiThread(smsCollector);
        //smsCollector.run();

        // TODO : Runnable for sending the data to the server but checks before how many messages sent last time and if there is no diff it doesnt send (diff - delete and get new one?)
    }


}
