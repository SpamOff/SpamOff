package nldr.spamoff;

import com.crashlytics.android.Crashlytics;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;

/**
 * Created by Roee on 09/01/2017.
 */

public class Logger {
    public static Boolean writeToLog(Exception ex) {
        //ex.printStackTrace();

        if (CookiesHandler.getCrashlyticsStatus() && Crashlytics.getInstance() != null) {
            Crashlytics.logException(ex);
            return true;
        }
        
        return false;
    }
}
