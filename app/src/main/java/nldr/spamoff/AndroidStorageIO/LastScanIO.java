package nldr.spamoff.AndroidStorageIO;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lior on 21-Oct-16.
 */
public class LastScanIO {

    private static final String SPAM_OFF_PREFS = "SOPrefs";
    private static final String LAST_SCAN_STORAGE_NAME = "SOLastScan";

    public static boolean read(Context context){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        return SOPrefs.getBoolean(LAST_SCAN_STORAGE_NAME, false);
    }
    public static void write(Context context, boolean isLastScanned){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor SOPrefsEditor = SOPrefs.edit();
        SOPrefsEditor.putBoolean(LAST_SCAN_STORAGE_NAME, isLastScanned);
        SOPrefsEditor.commit();
    }

}
