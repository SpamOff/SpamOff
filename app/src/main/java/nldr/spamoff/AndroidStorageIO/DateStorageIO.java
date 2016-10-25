package nldr.spamoff.AndroidStorageIO;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lior on 21-Oct-16.
 */
public class DateStorageIO {

    private static final String SPAM_OFF_PREFS = "SOPrefs";
    private static final String DATE_STORAGE_NAME = "SOLastScanDate";

    public static long read(Context context){
        SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
        return SOPrefs.getLong(DATE_STORAGE_NAME, 978300000000L);//default 01/01/2001
    }
    public static void write(Context context, long date){
       // if(date > read(context)) {
            SharedPreferences SOPrefs = context.getSharedPreferences(SPAM_OFF_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor SOPrefsEditor = SOPrefs.edit();
            SOPrefsEditor.putLong(DATE_STORAGE_NAME, date);
            SOPrefsEditor.commit();
       // }
    }
}
