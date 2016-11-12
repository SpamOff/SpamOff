package nldr.spamoff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

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

public class MainActivity extends AppCompatActivity implements AsyncDataHandler.asyncTaskUIMethods {

    private static View rootView;
    private boolean bHasScanned = false;
    private String[] permissionsArray =
            new String[] {
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.READ_SMS
            };

    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity mainActivity = this;
        final Context context = this;
        final int MAX_SLIDE_VALUE = 162;
        final int MIN_SLIDE_VALUE = 38;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!CookiesHandler.getIfTermsApproved(context)) {
            Intent intent = new Intent(context, FloatingTerms.class);
            startActivity(intent);
        }

        final SeekBar slider = (SeekBar) findViewById(R.id.seekBarSpamOff);
        slider.setProgress(MIN_SLIDE_VALUE);

        bHasScanned = CookiesHandler.getIfAlreadyScannedBefore(context);

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > MAX_SLIDE_VALUE) {
                    seekBar.setProgress(MAX_SLIDE_VALUE);
                }
                if (progress < MIN_SLIDE_VALUE) {
                    seekBar.setProgress(MIN_SLIDE_VALUE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > MIN_SLIDE_VALUE && seekBar.getProgress() <= MAX_SLIDE_VALUE) {
                    if (seekBar.getProgress() == MAX_SLIDE_VALUE) {
                        fetchIfPermitted();
                    }

                    seekBar.setProgress(MIN_SLIDE_VALUE);
                }
            }
        });
    }

    private void fetchIfPermitted() {

        List<String> deniededPermissions = new ArrayList<String>();

        for (String curr : permissionsArray) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, curr) != PackageManager.PERMISSION_GRANTED) {

                deniededPermissions.add(curr);
            }
        }

        if (deniededPermissions.size() > 0) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    deniededPermissions.toArray(new String[deniededPermissions.size()]), 0);
        } else {
            onRequestPermissionsResult(0, null, null);
        }
    }

    private void fetchWithPermissions() {

        final ProgressDialog progressDialog = new ProgressDialog(this, "טוען את ההודעות החדשות...");
        progressDialog.show();

        final Context context = this;
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        Intent intent = new Intent(context, ScanFinished.class);
                        startActivity(intent);
                    }
                });

                if (NetworkManager.sendJsonToServer(context, finalJsonObject[0])) {
                    Log.d("Good", "Sent to the server");
                } else {
                    Log.d("Problem", "Problem");
                }
            }
        };

        runOnUiThread(smsCollector);
        //smsCollector.run();

        // TODO : Runnable for sending the data to the server but checks before how many messages sent last time and if there is no diff it doesnt send (diff - delete and get new one?)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @returns true if all the permissions granted and false if not
     */
    private boolean checkAllPermissions() {
        for (String curr : permissionsArray) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, curr) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
            new MaterialDialog.Builder(this)
                    .content("כדי שנוכל לבצע את הסריקה יש לאשר את הגישה של האפליקציה לאנשי הקשר וההודעות.")
                    .title("לא נמצאו האישורים המתאימים לביצוע הסריקה")
                    .titleGravity(GravityEnum.END)
                    .buttonsGravity(GravityEnum.END)
                    .contentGravity(GravityEnum.END)
                    .positiveText("אוקי").show();
        }
    }

    @Override
    public void updateUI(String results) {
        Snackbar.make(rootView, results, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFetchingCancelled(String errorText, Throwable cause) {
        //Snackbar.make(null, errorText, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void startingToCheck(String checkName) {
        //Snackbar.make(null, checkName, Snackbar.LENGTH_SHORT).show();
    }

}