package nldr.spamoff.SMSHandler;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Predicate;

import nldr.spamoff.Logger;

/**
 * Created by lior on 15-Oct-16.
 */
public class SMSReader {

    // TODO : Consider Dual-Sim handling
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

    public static JSONArray readSms(Context context, Date minDate, boolean filterBySmsColumns)
            throws JSONException, SmsMissingFieldException {

        String path = "content://sms";
        Cursor cursor = context.getContentResolver().query(Uri.parse(path), null, null, null, null);

        return getUnclassifiedData(context, cursor, minDate, filterBySmsColumns);
    }

    public static ArrayList<String> analizeNumeric(Cursor cursor) {
        ArrayList<String> columnsNames = new ArrayList<String>();

        String curr;

        // The loop adds every column that can be parsed to int to the arraylist
        for (int i = 0; i < cursor.getColumnCount() - 1; i++) {
            try {

                curr = cursor.getString(i);
                if (curr != null)
                    curr = curr.replace("+972", "0");

                // Tries to parse the column to int
                if (!cursor.getColumnName(i).contains("id") &&
                    curr != null &&
                    curr.length() < 13 &&
                    curr.length() > 2) {

                    // If the parse worked and didnt throw an exception, it adds it to the array
                    columnsNames.add(cursor.getColumnName(i));
                }
            } catch (Exception ex) {
                Logger.writeToLog(ex);
            }
        }

        return columnsNames;
    }

    private static JSONArray getUnclassifiedData(Context context, Cursor cursor, Date minDate, Boolean filterBySmsColumns)
            throws JSONException, SmsMissingFieldException {

        ArrayList<String> personColumnName = new ArrayList<>();

        personColumnName.add(Telephony.Sms.PERSON);
        personColumnName.add("person");
        personColumnName.add("address");

        final ArrayList<String> numberColumns = new ArrayList<>();
        JSONArray smsData = new JSONArray();

        int dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE);

        if (cursor.moveToFirst()) {

            if (filterBySmsColumns) {
                // Adds the exists columns from the known array
                for (String currColumnName : personColumnName) {
                    if (cursor.getColumnIndex(currColumnName) != -1)
                        numberColumns.add(currColumnName);
                }
            }

            final ArrayList<String> numericFieldsFoundInSmss = new ArrayList<>();

            do {
                boolean preKnownFieldsExists = false;
                boolean foundFieldsInFoundsList = false;

                // Checks if the columns are relevant to this sms, else - starts the columns scan again with the boolean
                // stops the loop if 1 field found
                for (int i = 0; i < numberColumns.size() && !preKnownFieldsExists; i++) {
                    if (cursor.getColumnIndex(numberColumns.get(i)) != -1)
                        preKnownFieldsExists = true;
                }

                // If none of the known fields fitted, it checks all the other numeric field that already found
                for (int i = 0; i < numericFieldsFoundInSmss.size() && !preKnownFieldsExists && !foundFieldsInFoundsList; i++) {
                    if (cursor.getColumnIndex(numericFieldsFoundInSmss.get(i)) != -1)
                        foundFieldsInFoundsList = true;
                }

                // If there is no column, it will analyze all the numeric fields to find some new columns
                if (!preKnownFieldsExists && !foundFieldsInFoundsList) {
                    numericFieldsFoundInSmss.addAll(analizeNumeric(cursor));
                }

                final ArrayList<Integer> arrToDelete = new ArrayList<>();
//
//                // finds all the duplicates
//                for (String s:numericFieldsFoundInSmss) {
//                    if (numericFieldsFoundInSmss.indexOf(s) != numericFieldsFoundInSmss.lastIndexOf(s) &&
//                            (arrToDelete.contains(numericFieldsFoundInSmss.indexOf(s)) ||
//                                    arrToDelete.contains(numericFieldsFoundInSmss.lastIndexOf(s)))) {
//                        arrToDelete.add(numericFieldsFoundInSmss.indexOf(s));
//                    }
//                }
//
//                // Removes all the duplicates
//                for (Integer a:arrToDelete) {
//                    numericFieldsFoundInSmss.remove(a);
//                }

                boolean bIsInContacts = false;
                boolean bIsNewSms = true;
                JSONObject currSms = new JSONObject();

                if (filterBySmsColumns && dateIndex != -1) {
                    bIsNewSms = new Date(cursor.getLong(dateIndex)).after(minDate);
                }

                // This loop tries to filter the sms columns by all its numeric columns
                for (int i = 0; (filterBySmsColumns && bIsNewSms && (i < numberColumns.size())); i++) {
                    if (contactExists(context, cursor.getString(cursor.getColumnIndex(numberColumns.get(i))))) {
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
                if (!filterBySmsColumns || (!bIsInContacts && bIsNewSms)) {

                    for (int i = 0; i < cursor.getColumnCount() - 1; i++) {
                        currSms.put(cursor.getColumnName(i), cursor.getString(i));
                    }

                    smsData.put(currSms);
                }
            } while (cursor.moveToNext());

            if (filterBySmsColumns) {

                // Checks if a valid attribute name found, else - throws exceptions to notify that there are some attributes and logs it
                if (dateIndex == -1) {
                    Answers.getInstance().logCustom(
                            new CustomEvent("SmsAttributesMistmach")
                                    .putCustomAttribute("Phone Model", Build.MANUFACTURER + " - " + Build.MODEL)
                                    .putCustomAttribute("Date field was not found", "Searched field : " + Telephony.Sms.DATE));
                }

                if (numberColumns.size() > 0) {

                    String names = numberColumns.get(0);

                    for(int i = 1; i < numberColumns.size(); i++) {
                        names += ", " + numberColumns.get(i);
                    }

                    // Validation because Answer service cant log more than 100 chars
                    if (names.length() > 99)
                        names = numberColumns.get(0);

                    Answers.getInstance().logCustom(
                            new CustomEvent("SmsAttributesFound")
                                    .putCustomAttribute("Phone Model", Build.MANUFACTURER + " - " + Build.MODEL)
                                    .putCustomAttribute("Fields Names", names));
                } else {
                    Answers.getInstance().logCustom(
                            new CustomEvent("SmsAttributesMistmach")
                                    .putCustomAttribute("Number of accessible attributes", numberColumns.size()));
                    throw new SmsMissingFieldException("Person column is missing");
                }
            }
        }

        return smsData;
    }

    public static boolean contactExists(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };

        Cursor cur = null;

        try {
            cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            return cur.moveToFirst();
        } catch (Exception ex) {
            Logger.writeToLog(ex);
            return false;
        } finally {
            if (cur != null)
                cur.close();
        }
    }
}
