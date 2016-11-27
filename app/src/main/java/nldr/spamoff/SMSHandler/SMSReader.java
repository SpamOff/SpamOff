package nldr.spamoff.SMSHandler;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.ShareEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    static String personColumnName = "personn";
    static String dateFormat = "dd/MM/yyyy hh:mm";

    public static class SmsMissingFieldException extends Exception {
        String fieldName;

        public SmsMissingFieldException(String field) {
            super();
            fieldName = field;
        }
    }

    public static String getArrayAsJson(String[] arr) {
        String data = "{";

        for (String curr: arr) {
            data += curr + ", ";
        }

        data = data.substring(0, data.lastIndexOf(','));
        data += "}";

        return (data);
    }

    public static JSONArray readSms(Context context, Date minDate, boolean keepWithoutFilter) throws JSONException, SmsMissingFieldException {

        String path = "content://sms";
        Cursor cursor = context.getContentResolver().query(Uri.parse(path), null, null, null, null);

        if (!keepWithoutFilter) {

            if (isClassifiedByAttr(cursor, dateColumnName)) {
                Answers.getInstance().logCustom(
                        new CustomEvent("SmsAttributesMistmach")
                                .putCustomAttribute("date", dateColumnName)
                                .putCustomAttribute("fields", getArrayAsJson(cursor.getColumnNames())));
                throw new SmsMissingFieldException(dateColumnName);
            }

            if (isClassifiedByAttr(cursor, personColumnName)) {
                Answers.getInstance().logCustom(
                        new CustomEvent("SmsAttributesMistmach")
                                .putCustomAttribute("person", personColumnName)
                                .putCustomAttribute("fields", getArrayAsJson(cursor.getColumnNames())));
                throw new SmsMissingFieldException(personColumnName);
            }
        }

        return getUnclassifiedData(context, cursor, minDate, keepWithoutFilter);
    }

//    public static ArrayList<SMSMessage> read(Context context, Date date) {
//        Cursor chosenCursor;
//        ArrayList<SMSMessage> united = new ArrayList<>();
//        Cursor phoneSMSCursor = readFromPhone(context);
//        phoneSMSCursor.moveToFirst();
//        Cursor simSMSCursor = readFromSim(context);
//        String[] phoneTemplate = new String[] { "_id", "address", "person", "date", "body" };
//        String[] simTemplate = new String[] { "_id", "address", "person", "date", "body" };
//        SMSMessage temp;
//        String[] phoneSMSCursorFields = phoneSMSCursor.getColumnNames();
//        String[] simSMSCursorFields = simSMSCursor.getColumnNames();
//
//        Crashlytics.log("PHONE : " + phoneSMSCursorFields.toString()  + " SIM : " + simSMSCursorFields.toString());
//
//        try {
//            if (phoneSMSCursor.getCount() == 0) {
//                if (simSMSCursor.getCount() > 0) {
//                    moveCursorToArrayList(context, simSMSCursor, united, date);
//                }
//
//                return united;
//            }
//            else if(simSMSCursor.getCount() == 0){
//                moveCursorToArrayList(context, phoneSMSCursor, united, date);
//                return united;
//            }
//        }
//        catch(Exception e) {
//
//        }
//
//        try {
//
//            CursorJoiner joiner = new CursorJoiner(phoneSMSCursor, phoneTemplate, simSMSCursor, simTemplate);
//            Date SMSDate;
//
//            for (CursorJoiner.Result result : joiner) {
//                switch (result) {
//                    case RIGHT:
//                        chosenCursor = phoneSMSCursor;
//                        break;
//                    case LEFT:
//                        chosenCursor = simSMSCursor;
//                        break;
//                    default:
//                        chosenCursor = phoneSMSCursor;
//                        break;
//                }
//
//                SMSDate = new Date(chosenCursor.getLong(4));
//
//                if (SMSDate.after(date) && contactExists(context, chosenCursor.getString(2))) {
//                    united.add(new SMSMessage(chosenCursor.getString(2),
//                            chosenCursor.getString(5),
//                            getDate(chosenCursor.getLong(4), "dd/MM/yyyy hh:mm")));
//                }
//            }
//        } catch (Exception ex) {
//            Crashlytics.log("PHONE : " + phoneSMSCursorFields.toString()  + " SIM : " + simSMSCursorFields.toString());
//        }
//
//        return united;
//        return null;
//    }

//    private static void moveCursorToArrayList(Context context, Cursor cursor, ArrayList<SMSMessage> list, Date date) {
//        cursor.moveToFirst();
//        SMSMessage temp;
//        Date SMSDate;
//
//        while (!cursor.isAfterLast()) {
//
//            SMSDate = new Date(cursor.getLong(4));
//
//            if (SMSDate.after(date) && !contactExists(context, cursor.getString(2))) {
//                list.add(new SMSMessage(cursor.getString(2), cursor.getString(5), getDate(cursor.getLong(4), "dd/MM/yyyy hh:mm")));
//            }
//
//            cursor.moveToNext();
//        }
//
//        cursor.close();
//    }

//    private static JSONArray readFromSim(Context context, Date date) throws JSONException {
//        return getUnclassifiedData(context, "content://sms/icc", date);
//    }
//
//    private static JSONArray readFromPhone(Context context, Date date) throws JSONException {
//       return getUnclassifiedData(context, "content://sms/inbox", date);
//    }

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

    private static JSONArray getUnclassifiedData(Context context, Cursor cursor, Date minDate, Boolean keepWithoutFilter) throws JSONException {
        //Cursor cursor = context.getContentResolver().query(Uri.parse(path), null, null, null, "date DESC");

        JSONArray smsData = new JSONArray();

        if (cursor.moveToFirst()) {
            do {
                JSONObject currSms = new JSONObject();
                String contactNumber;
                boolean bIsUpToDate = true;
                boolean bIsInContacts = true;

                if (isClassifiedByAttr(cursor, dateColumnName)) {
                    Date smsDate = new Date(cursor.getLong(cursor.getColumnIndex(dateColumnName)));
                    bIsUpToDate = smsDate.after(minDate);
                }

                if (isClassifiedByAttr(cursor, personColumnName)) {
                    contactNumber = cursor.getString(cursor.getColumnIndex(personColumnName));
                    bIsInContacts = contactExists(context, contactNumber);
                }

                // Checks some things :
                // Needs to check the date - if yes, checks, if nor - not
                // Needs to check the contact's number - if yes - checks, if nor - not
                if ((!bIsInContacts && bIsUpToDate) || keepWithoutFilter) {

                    for (int i = 0; i < cursor.getColumnCount() - 1; i++) {
                        currSms.put(cursor.getColumnName(i), cursor.getString(i));
                    }

                    smsData.put(currSms);
                }
            } while (cursor.moveToNext());
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
