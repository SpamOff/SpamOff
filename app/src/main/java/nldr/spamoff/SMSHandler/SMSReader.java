package nldr.spamoff.SMSHandler;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;


/**
 * Created by lior on 15-Oct-16.
 */
public class SMSReader {

    public static ArrayList<SMSMessage> read(Context context, Date date){
        Cursor chosenCursor;
        ArrayList<SMSMessage> united = new ArrayList<>();
        Cursor phoneSMSCursor = readFromPhone(context);
        phoneSMSCursor.moveToFirst();
        Cursor simSMSCursor = readFromSim(context);
        String[] phoneTemplate = new String[] { "_id", "thread_id", "address", "person", "date", "body" };
        String[] simTemplate = new String[] { "_id", "thread_id", "address", "person", "date", "body" };
        SMSMessage temp;


        try{
            if(phoneSMSCursor.getCount() == 0) {
                if(simSMSCursor.getCount() > 0) {
                    moveCursorToArrayList(context, simSMSCursor, united, date);
                }
                return united;

            }
            else if(simSMSCursor.getCount() == 0){
                moveCursorToArrayList(context, phoneSMSCursor, united, date);
                return united;
            }
        }
        catch(Exception e){

    }


         CursorJoiner joiner = new CursorJoiner(phoneSMSCursor, phoneTemplate, simSMSCursor, simTemplate);
         Date SMSDate;

         for (CursorJoiner.Result result : joiner) {
             switch (result) {
                 case RIGHT:
                     chosenCursor = phoneSMSCursor;
                     break;
                 case LEFT:
                     chosenCursor = simSMSCursor;
                     break;
                 default:
                     chosenCursor = phoneSMSCursor;
                     break;
             }
             SMSDate = new Date(chosenCursor.getLong(4));
             if(SMSDate.after(date) && contactExists(context, chosenCursor.getString(2))) {
                 temp = new SMSMessage();
                 temp.setAddress(chosenCursor.getString(2));
                 temp.setBody(chosenCursor.getString(5));
                 temp.setTimeStamp(getDate(chosenCursor.getLong(4), "dd/MM/yyyy hh:mm"));
                 united.add(temp);
             }
         }
        return united;
    }

    private static void moveCursorToArrayList(Context context, Cursor cursor, ArrayList<SMSMessage> list, Date date) {
        cursor.moveToFirst();
        SMSMessage temp;
        Date SMSDate;


        while(!cursor.isAfterLast()) {
            SMSDate = new Date(cursor.getLong(4));
            if(SMSDate.after(date) && !contactExists(context, cursor.getString(2))) {
                temp = new SMSMessage();
                temp.setAddress(cursor.getString(2));
                temp.setBody(cursor.getString(5));
                temp.setTimeStamp(getDate(cursor.getLong(4), "dd/MM/yyyy hh:mm"));
                list.add(temp);
            }
            cursor.moveToNext();
        }
        cursor.close();
    }

    private static Cursor readFromSim(Context context){
        if(isDualSim()){
            //TODO:

        }
        return readSMS(context, "content://sms/icc");
    }
    private static Cursor readFromPhone(Context context){
       return readSMS(context, "content://sms/inbox");
    }
    private static boolean isDualSim(){
        return false;
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

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    public static boolean contactExists(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }
}
