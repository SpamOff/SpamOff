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

    private static final String WAITING_FOR_SERVER = "SOWaitingForServer";

    public static boolean getIfWaitingForServer(Context context){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        return SOPrefs.getBoolean(WAITING_FOR_SERVER, false);
    }
    public static void setIfWaitingForServer(Context context, boolean isWaiting){
        // if(date > read(context)) {
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor SOPrefsEditor = SOPrefs.edit();
        SOPrefsEditor.putBoolean(WAITING_FOR_SERVER, isWaiting);
        SOPrefsEditor.commit();
        // }
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

    public static int getLastScanMessagesCount(Context context){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        return SOPrefs.getInt(LAST_SCAN_MESSAGES_COUNT_NAME, 0);
    }
    public static void setLastScanMessagesCount(Context context, int count){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor SOPrefsEditor = SOPrefs.edit();
        SOPrefsEditor.putInt(LAST_SCAN_MESSAGES_COUNT_NAME, count);
        SOPrefsEditor.commit();
    }

    private static final String SPAM_MESSAGES_COUNT = "SOSpamMessagesCount";

    public static int getSpamMessagesCount(Context context){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        return SOPrefs.getInt(SPAM_MESSAGES_COUNT, 0);
    }
    public static void setSpamMessagesCount(Context context, int count){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor SOPrefsEditor = SOPrefs.edit();
        SOPrefsEditor.putInt(SPAM_MESSAGES_COUNT, count);
        SOPrefsEditor.commit();
    }

    private static final String RESULTS_URI = "SOResultsURI";

    public static String getResultsURI(Context context){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        return SOPrefs.getString(RESULTS_URI, "http://www.spamoff.co.il");
    }
    public static void setResultsUri(Context context, String resultsURI){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor SOPrefsEditor = SOPrefs.edit();
        SOPrefsEditor.putString(RESULTS_URI, resultsURI);
        SOPrefsEditor.commit();
    }



    private static final String TERMS_APPROVE_STORAGE_NAME = "SOTermsApproved";

    public static boolean getIfTermsApproved(Context context){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        return SOPrefs.getBoolean(TERMS_APPROVE_STORAGE_NAME, false);

    }
    public static void setIfTermsApproved(Context context, boolean isTermsApprove){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor SOPrefsEditor = SOPrefs.edit();
        SOPrefsEditor.putBoolean(TERMS_APPROVE_STORAGE_NAME, isTermsApprove);
        SOPrefsEditor.commit();
    }

}
