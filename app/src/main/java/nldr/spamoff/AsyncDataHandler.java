package nldr.spamoff;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by Roee on 15/10/2016.
 */
public class AsyncDataHandler {

    private static Context context;
    private final String LOG_TAG = this.getClass().getSimpleName();

    public AsyncDataHandler(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Checks if the device has an internet connection
     * Does not refers to wifi/3g
     * @returns a boolean
     */
    public static boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) AsyncDataHandler.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activityNetwork = cm.getActiveNetworkInfo();

        //boolean isWifi = activityNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        return ((activityNetwork != null) &&
                (activityNetwork.isConnected()));
    }

    /**
     * Checks if the device has access to the internt, after the internet-is-on checking
     * @return
     */
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

    /**
     * Returns the weather array
     * @return
     */
    public String[] getUpdatedWeather(classWithUIupdates inter) {
        return (performUpdateInAnotherTask(inter));
    }

    /**
     * Function that does the update, I took this out to this func to do a nice try catch system on all of this
     */
    private String[] performUpdateInAnotherTask(classWithUIupdates inter) {

        final FetchWeatherTask weatherTask = new FetchWeatherTask(inter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        weatherTask.execute("");
/*

        try {
            retValue = weatherTask.execute(location).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
*/

        return null;
    }

    private Boolean sendDataToServer(String smsData) throws NetworkErrorException {

        // Checks if the device has internet
        if (!AsyncDataHandler.hasInternetAccess()) {
            throw new NetworkErrorException("No Internet access or connection!");
        }

        return (true);
    }

    public class FetchWeatherTask extends AsyncTask<String, String[], String[]> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        // This is a callable functino to perform updates in the ui after the asynctask dones. like an event in the fragment
        private Callable<Void> funcFromUItoFetch;

        private classWithUIupdates myRefreshInter;

        public FetchWeatherTask(classWithUIupdates inter) {
            myRefreshInter = inter;
        }
        public FetchWeatherTask(Callable<Void> func) {
            funcFromUItoFetch = func;
        }

        @Override
        protected String[] doInBackground(String... params) {

            String[] weatherForecastArray = null;

            if (params.length == 0) {
            }

            String weatherForecast = null;

                // Should throw NetworkErrorException if there is no internet access
                /*weatherForecast = getWeatherForecastDataFromServer(params[0]);

                weatherForecastArray = getWeatherDataFromJson(weatherForecast, 7);*/


            return (weatherForecastArray);
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {

                myRefreshInter.updateUI(result);
            }
        }
    }

}
