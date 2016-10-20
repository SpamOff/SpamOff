package nldr.spamoff;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Callback;


/**
 * Created by Roee on 15/10/2016.
 */
public class AsyncDataHandler extends AsyncTask<String, String, String> {

    private asyncTaskUIMethods Callback;
    private static Context context;

    /**
     * This code section prevents other classes to create new FetchWeatherTask every time they want to execute it..
     */
    public static AsyncDataHandler communicator;

    public static void runTaskInBackground(asyncTaskUIMethods callback,
                                           Context context,
                                           String jsonToSend,
                                           String serverAddress) {
        communicator = new AsyncDataHandler(callback, context);
        communicator.sendDataToServer(false, jsonToSend, serverAddress);
    }

    public static String runTaskAndWait(asyncTaskUIMethods callback,
                                        Context context,
                                        String jsonToSend,
                                        String serverAddress) {
        communicator = new AsyncDataHandler(callback, context);
        return (communicator.sendDataToServer(true, jsonToSend, serverAddress));
    }

    /**
     * This interface is to help the asynctask call UI functions when its done or being cancelled
     */
    public interface asyncTaskUIMethods {
        void updateUI(String results);

        void onFetchingCancelled(String errorText, Throwable cause);

        void startingToCheck(String checkName);
    }

    /**
     * Inits the async task with all the needed things
     * @param callback - The callback of the calling class
     * @param context - The context of the calling class
     */
    public AsyncDataHandler(asyncTaskUIMethods callback, Context context) {
        this.context = context;
        this.Callback = callback;
    }

    /**
     * @returns the calling callback of the asynctask
     */
    public asyncTaskUIMethods getCallback() {
        return this.Callback;
    }

    /**
     * Sets a new callback to the asynctask
     * @param callback - The new callback
     */
    public void setCallback(asyncTaskUIMethods callback) {
        this.Callback = callback;
    }

    /**
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * Sets a new context to the asynctask
     * @param context - The new context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    private void callCancel(String cause, Throwable throwable) {
        // Checks if the user wanted to update the ui
        if (this.getCallback() != null)
            this.getCallback().onFetchingCancelled(cause, throwable);
    }

    /**
     * Returns the weather array
     * @returns an array with all the weekly weather info
     */
    public String sendDataToServer(Boolean waitForReturnValue, String jsonToSend, String serverAddress) {

        //this.getCallback().startingToCheck("Internet Connection varification");
        String returnValue = null;

            // Checks if the user wants to wait for the return value (async/nor)
            if (waitForReturnValue) {
                try {
                    returnValue = this.execute(jsonToSend, serverAddress).get();
                } catch (Exception ex) {
                    callCancel("Error while fetching the weather task", ex);
                }
            } else {
                this.execute(jsonToSend, serverAddress);
            }


        return (returnValue);
        //performUpdateInAnotherTask();
    }

    protected String doInBackground(String... params) {

        String smsJson = params[0];
        //String url = params[1];
        String url = "http://spamoff.co.il/uploadClient/uploadTxts";
        final String res = "";
        final String[] response = {null};

        // Should throw NetworkErrorException if there is no internet access
//            publishProgress(new String[]{"Retriving data from the server"});
//            Thread.sleep(300);




        final Boolean[] bHasInternet = {false};

        bHasInternet[0] = hasInternetAccess();

        // Checks if the device has internet
        if (!bHasInternet[0])
            callCancel("No internet connection", null);
        else {
            if (!isCancelled()) {
                if (params[0] == null) {
                    return "Nothing was sent because the sms json is empty";
                }

                final MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8");

                OkHttpClient client = new OkHttpClient();
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS);

                RequestBody body = RequestBody.create(JSON, "");
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                Response r;
                Call call = client.newCall(request);

                try {
                     r = call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response resp) throws IOException {
                        response[0] = resp.body().string();
                    }
                });
            }

            if (response[0] == null) {
                callCancel("Oops.. Couldn't connect to the server.", null);
                return "An error occurred";
            }
        }

        return (response[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            // Checks if the user wanted to update the ui
            if (this.getCallback() != null)
                this.getCallback().updateUI(result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        // Checks if the user wanted to update the ui
        if (this.getCallback() != null)
            this.getCallback().onFetchingCancelled("", null);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        // Checks if the user wanted to update the ui
        if (this.getCallback() != null)
            this.getCallback().startingToCheck(values[0]);
    }

    public static boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) AsyncDataHandler.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activityNetwork = cm.getActiveNetworkInfo();

        //boolean isWifi = activityNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        return ((activityNetwork != null) &&
                (activityNetwork.isConnected()));
    }

    public static boolean hasInternetAccess() {

        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc =
                        (HttpURLConnection)(new URL("http://clients3.google.com/generate_204")
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
