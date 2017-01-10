package nldr.spamoff;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

/**
 * Created by Roee on 09/01/2017.
 */

public class Logger {
    public static Boolean writeToLog(Exception ex) {
        if (Crashlytics.getInstance() != null) {
            Crashlytics.logException(ex);
            return true;
        }
//        if (Answers.getInstance() != null) {
//            Answers.getInstance().logCustom(new CustomEvent("Exception").putCustomAttribute("message", ex.getMessage()));
        else return false;
    }
}
