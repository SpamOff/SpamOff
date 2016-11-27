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
import static nldr.spamoff.AsyncStatus.*;

/**
 * Created by Roee on 15/10/2016.
 */
public class AsyncDataHandler extends AsyncTask<Boolean, String, AsyncStatus> {

    public static AsyncDataHandler communicator;
    private static Context _context;
    private usingAsyncFetcher _callback;

    public interface usingAsyncFetcher {
        void finished();
        void cancelled();
        void updateProgress(String prg);
        void noNewMessages();
        void error(String errorMessage);
        void startedFetching();
        void stoppedFetching();
        void smsFieldsMistmatch();
    }

    public static void performInBackground(Context context, usingAsyncFetcher callback, Boolean keepWithoutFilter) {
        communicator = new AsyncDataHandler(context, callback);
        communicator.execute(keepWithoutFilter);
    }

    public AsyncDataHandler(Context context, usingAsyncFetcher callback) {
        this._context = context;
        this._callback = callback;
    }

    public Context getContext() {
        return this._context;
    }

    public void setContext(Context context) {
        this._context = context;
    }

    public usingAsyncFetcher getCallback() {
        return this._callback;
    }

    public void setCallback(usingAsyncFetcher callback) {
        this._callback = callback;
    }

    protected AsyncStatus doInBackground(Boolean... params) {

        AsyncStatus result = finished;

        publishProgress("בודק את מצב הרשת...");

        if (!hasInternetAccess()) {
            result = noInternet;
        } else {
            Date lastScanDate = new Date(CookiesHandler.getLastScanDate(getContext()));

            JSONArray jsonArray = null;

            try {
                publishProgress("קורא הודעות...");
                Boolean keepWithoutFilter = params[0];
                jsonArray = SMSReader.readSms(getContext(), lastScanDate, keepWithoutFilter);
                //ArrayList<SMSMessage> arr = SMSReader.read(getContext(), lastScanDate);
                publishProgress("מחפש הודעות ספאם...");
                //jsonArray = SMSToJson.parseAllToArray(getContext(), arr);

                CookiesHandler.setLastScanMessagesCount(getContext(), jsonArray.length());
                CookiesHandler.setLastScanDate(getContext(), System.currentTimeMillis());

                if (!CookiesHandler.getIfAlreadyScannedBefore(getContext())){
                    CookiesHandler.setIfAlreadyScannedBefore(getContext(), true);
                }

                if (jsonArray.length() == 0) {
                    result = noNewMessages;
                } else {
                    publishProgress("נמצאו " + jsonArray.length() + " הודעות חשודות, שולח לשרת...");
                    if (!NetworkManager.sendJsonToServer(this.getContext(), jsonArray))
                        result = failedWhileSendingToServer;
                }
            } catch (JSONException e) {
                result = smsReadingError;
            } catch (SMSReader.SmsMissingFieldException e) {
                result = smsFieldsMistmatch;
            }
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        this.getCallback().startedFetching();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(AsyncStatus status) {

        this.getCallback().stoppedFetching();

        switch (status) {
            case noInternet:
                getCallback().error("כדי שנוכל לסרוק ולשלוח את הודעות הספאם שלך דרוש חיבור אינטרנט זמין ומהיר מספיק");
                break;
            case finished:
                getCallback().finished();
                break;
            case smsReadingError:
                getCallback().error("ארעה שגיאה בזמן קריאת ההודעות");
                break;
            case failedWhileSendingToServer:
                getCallback().error("ארעה שגיאה בזמן שליחת הנתונים לשרת");
                break;
            case noNewMessages:
                getCallback().noNewMessages();
                break;
            case smsFieldsMistmatch:
                getCallback().smsFieldsMistmatch();
                break;
            default:
                getCallback().cancelled();
                break;
        }
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
                urlc.setConnectTimeout(1500);
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