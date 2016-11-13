package nldr.spamoff;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;
import nldr.spamoff.Networking.NetworkManager;
import nldr.spamoff.SMSHandler.SMSMessage;
import nldr.spamoff.SMSHandler.SMSReader;
import nldr.spamoff.SMSHandler.SMSToJson;

/**
 * Created by Roee on 15/10/2016.
 */
public class AsyncDataHandler extends AsyncTask<String, String, Boolean> {

    public static AsyncDataHandler communicator;

    private static Context _context;

    private usingInternetChecker _callback;

    public interface usingInternetChecker {
        void noInternet();
        void progressDone(Boolean bHasInternet);
        void updateProgress(String prg);
    }

    public static void checkInternet(Context context, usingInternetChecker callback) {
        communicator = new AsyncDataHandler(context, callback);
        communicator.fetch();
    }

    public AsyncDataHandler(Context context, usingInternetChecker callback) {
        this._context = context;
        this._callback = callback;
    }

    public Context getContext() {
        return this._context;
    }

    public void setContext(Context context) {
        this._context = context;
    }

    public usingInternetChecker getCallback() {
        return this._callback;
    }

    public void setCallback(usingInternetChecker callback) {
        this._callback = callback;
    }

    private void fetch() {

        this.execute();
    }

//    protected Boolean doInBackground(Boolean... params) {
//        return (hasInternetAccess());
//    }

    boolean bHasInternet = true;

    protected Boolean doInBackground(String... params) {

        publishProgress("בודק את מצב הרשת...");
        bHasInternet = hasInternetAccess();
        boolean bStatus = false;

        if (bHasInternet) {
            Date lastScanDate = new Date(CookiesHandler.getLastScanDate(getContext()));

            JSONArray jsonArray = null;

            try {
                publishProgress("קורא הודעות...");
                ArrayList<SMSMessage> arr = SMSReader.read(getContext(), lastScanDate);
                publishProgress("מחפש הודעות ספאם...");
                jsonArray = SMSToJson.parseAllToArray(getContext(), arr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            CookiesHandler.setLastScanMessagesCount(getContext(), jsonArray.length());
            CookiesHandler.setLastScanDate(getContext(), System.currentTimeMillis());
            CookiesHandler.setIfAlreadyScannedBefore(getContext(), true);

            publishProgress("נמצאו " + jsonArray.length() + " הודעות חשודות, שולח לשרת...");

            bStatus = NetworkManager.sendJsonToServer(this.getContext(), jsonArray);

        }

        return bStatus;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {

        if (!bHasInternet)
            getCallback().noInternet();
        else
            getCallback().progressDone(aBoolean);

        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        getCallback().updateProgress(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public static boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) AsyncDataHandler._context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activityNetwork = cm.getActiveNetworkInfo();

        //boolean isWifi = activityNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        return ((activityNetwork != null) &&
                (activityNetwork.isConnected()));
    }

    public static boolean hasInternetAccess() {

        if (isNetworkAvailable()) {

            try {
                HttpURLConnection urlc =
                        (HttpURLConnection) (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(100);
                urlc.connect();

                return ((urlc.getResponseCode() == 204) && (urlc.getContentLength() == 0));
            } catch (IOException e) {
                Log.e("Internet_Checking_Tak", "Error checking internet connection", e);
            }
        } else {
            Log.d("Internet_Checking_Tak", "No network available!");
        }

        return false;
    }
}
