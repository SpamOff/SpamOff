package nldr.spamoff.AndroidStorageIO;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Roee on 27/10/2016.
 */
public class CookiesHandler {

    private static final String SPAM_OFF_PREFS = "SOPrefs";
    private static final String LAST_SCAN_STORAGE_NAME = "SOLastScan";

    public static boolean getIfAlreadyScannedBefore(Context context){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        return SOPrefs.getBoolean(LAST_SCAN_STORAGE_NAME, false);
    }
    public static void setIfAlreadyScannedBefore(Context context, boolean isLastScanned){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor SOPrefsEditor = SOPrefs.edit();
        SOPrefsEditor.putBoolean(LAST_SCAN_STORAGE_NAME, isLastScanned);
        SOPrefsEditor.commit();
    }

    private static final String DATE_STORAGE_NAME = "SOLastScanDate";

    public static long getLastScanDate(Context context){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        return SOPrefs.getLong(DATE_STORAGE_NAME, 978300000000L);//default 01/01/2001
    }
    public static void setLastScanDate(Context context, long date){
        // if(date > read(context)) {
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor SOPrefsEditor = SOPrefs.edit();
        SOPrefsEditor.putLong(DATE_STORAGE_NAME, date);
        SOPrefsEditor.commit();
        // }
    }

    private static final String LAST_SCAN_MESSAGES_COUNT_NAME = "SOLastScanMessagesCount";

    public static long getLastScanMessagesCount(Context context){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        return SOPrefs.getLong(LAST_SCAN_MESSAGES_COUNT_NAME, 978300000000L);//default 01/01/2001
    }
    public static void setLastScanMessagesCount(Context context, int count){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor SOPrefsEditor = SOPrefs.edit();
        SOPrefsEditor.putLong(LAST_SCAN_MESSAGES_COUNT_NAME, count);
        SOPrefsEditor.commit();
    }
}
