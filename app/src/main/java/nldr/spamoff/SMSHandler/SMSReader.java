package nldr.spamoff.SMSHandler;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.ShareEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import static nldr.spamoff.R.id.date;


/**
 * Created by lior on 15-Oct-16.
 */
public class SMSReader {

    // TODO : Consider Dual-Sim handling
    static String dateColumnName = "date";
    static String personColumnName = "personnn";
    static String dateFormat = "dd/MM/yyyy hh:mm";

    public static class SmsMissingFieldException extends Exception {
        String fieldName;

        public SmsMissingFieldException(String field) {
            super();
            fieldName = field;
        }
    }

    public static String getArrayAsJson(Object[] arr) {
        String data = "{";

        for (Object curr: arr) {
            data += curr.toString() + ", ";
        }

        data = data.substring(0, data.lastIndexOf(','));
        data += "}";

        return (data);
    }

    public static JSONArray readSms(Context context, Date minDate, boolean keepWithoutFilter) throws JSONException, SmsMissingFieldException {

        String path = "content://sms";
        Cursor cursor = context.getContentResolver().query(Uri.parse(path), null, null, null, null);

        if (!keepWithoutFilter) {

            if (!isClassifiedByAttr(cursor, dateColumnName)) {
                Answers.getInstance().logCustom(
                        new CustomEvent("SmsAttributesMistmach")
                                .putCustomAttribute("date", dateColumnName)
                                .putCustomAttribute("fields", getArrayAsJson(cursor.getColumnNames())));
                throw new SmsMissingFieldException(dateColumnName);
            }

//            if (!isClassifiedByAttr(cursor, personColumnName)) {
//                Answers.getInstance().logCustom(
//                        new CustomEvent("SmsAttributesMistmach")
//                                .putCustomAttribute("person", personColumnName)
//                                .putCustomAttribute("fields", getArrayAsJson(cursor.getColumnNames())));
//                throw new SmsMissingFieldException(personColumnName);
//            }
        }

        return getUnclassifiedData(context, cursor, minDate, keepWithoutFilter);
    }

    private static Boolean isClassifiedByAttr(Cursor cursor, String attribute) {
        boolean bFoundAttribute = false;

        for (String curr :cursor.getColumnNames()) {
            if (curr.equals(attribute)) {
                bFoundAttribute = true;
                break;
            }
        }

        return bFoundAttribute;
    }

    public static ArrayList<String> analizeNumeric(Cursor cursor) {
        ArrayList<String> columnsNames = new ArrayList<String>();

        if (cursor.moveToFirst()) {
            // The loop adds every column that can be parsed to int to the arraylist
            for (int i = 0; i < cursor.getColumnCount() - 1; i++) {
                String data = cursor.getString(i);
                try {
                    int intData;
                    if (data != null)
                         intData = Integer.parseInt(data);
                    columnsNames.add(cursor.getColumnName(i));
                } catch (Exception ex) {
                    Log.e("Error", "Error while parsing column name to int");
                }
            }
        }

        return columnsNames;
    }

    private static JSONArray getUnclassifiedData(Context context, Cursor cursor, Date minDate, Boolean keepWithoutFilter) throws JSONException, SmsMissingFieldException {
        ArrayList<String> numberColumns = new ArrayList<>();
        JSONArray smsData = new JSONArray();

        boolean bIsUpToDate = true;

        int dateIndex;

        if (cursor.moveToFirst()) {

            dateIndex = cursor.getColumnIndex(dateColumnName);

            if (dateIndex != -1) {
                Date smsDate = new Date(cursor.getLong(dateIndex));
                bIsUpToDate = smsDate.after(minDate);
            }

            if (isClassifiedByAttr(cursor, Telephony.TextBasedSmsColumns.PERSON)) {
                numberColumns.add(Telephony.TextBasedSmsColumns.PERSON);
                //numberColumns.add(personColumnName);
            } else {
                numberColumns = analizeNumeric(cursor);
//                for (String curr: cursor.getColumnNames()) {
//                    numberColumns.add(curr);
//                }
                //) = analizeNumeric(cursor);
            }
        }

        if (cursor.moveToFirst()) {
            do {
                boolean bIsInContacts = false;
                JSONObject currSms = new JSONObject();

                for (int i = 0; i < numberColumns.size() && !keepWithoutFilter; i++) {
                    if (contactExists(context, numberColumns.get(i))) {
                        String temp = numberColumns.get(i);
                        numberColumns.clear();
                        numberColumns.add(temp);
                        bIsInContacts = true;
                        break;
                    }
                }

                // Checks some things :
                // Needs to check the date - if yes, checks, if nor - not
                // Needs to check the contact's number - if yes - checks, if nor - not
                //&& bIsUpToDate
                if ((!bIsInContacts) || keepWithoutFilter) {

                    for (int i = 0; i < cursor.getColumnCount() - 1; i++) {
                        currSms.put(cursor.getColumnName(i), cursor.getString(i));
                    }

                    smsData.put(currSms);
                }
            } while (cursor.moveToNext());

            // Checks if a valid attribute name found, else - throws exceptions to notify that there are some attributes and logs it
            if (numberColumns.size() == 1) {
                Answers.getInstance().logCustom(
                        new CustomEvent("SmsAttributesFound")
                                .putCustomAttribute("Phone Model", Build.MANUFACTURER + " - " + Build.MODEL)
                                .putCustomAttribute("Field Name", numberColumns.get(0)));
            } else {
                Answers.getInstance().logCustom(
                        new CustomEvent("SmsAttributesMistmach")
                                .putCustomAttribute("Accessible attributes", getArrayAsJson(numberColumns.toArray())));
                throw new SmsMissingFieldException(personColumnName);
            }

        }

        return smsData;
    }

    private static Cursor readSMS(Context context, String path){
        Cursor cursor = null;
        String WHERE_CONDITION = null;
        String SORT_ORDER = "date DESC";

        cursor = context.getContentResolver().query(
               Uri.parse(path),
               new String[]{"_id", "thread_id", "address", "person", "date", "body"},
               WHERE_CONDITION,
               null,
               SORT_ORDER);

        return cursor;
    }

//    public static String getDate(long milliSeconds, String dateFormat)
//    {
//        // Create a DateFormatter object for displaying date in specified format.
//        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
//
//        // Create a calendar object that will convert the date and time value in milliseconds to date.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(milliSeconds);
//        return formatter.format(calendar.getTime());
//    }

    public static boolean contactExists(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);

        try {
            return cur.moveToFirst();
        } catch (Exception ex) {
            return false;
        } finally {
            if (cur != null)
                cur.close();
        }
    }
}
