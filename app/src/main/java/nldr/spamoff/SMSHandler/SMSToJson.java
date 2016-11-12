package nldr.spamoff.SMSHandler;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lior on 18-Oct-16.
 */
public class SMSToJson {

    public static JSONObject parseAll(Context context, ArrayList<SMSMessage> smsMesssages) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        for (SMSMessage smsMessage : smsMesssages) {
            messagesArray.put(parse(smsMessage));
        }

        jsonObject.put("messages", messagesArray);
        return jsonObject;
    }

    public static JSONArray parseAllToArray(Context context, ArrayList<SMSMessage> smsMesssages) throws JSONException {
        JSONArray messagesArray = new JSONArray();

        for (SMSMessage smsMessage : smsMesssages) {
            messagesArray.put(parse(smsMessage));
        }

        return messagesArray;
    }

    private static JSONObject parse(SMSMessage smsMessage) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", smsMessage.getAddress());
        jsonObject.put("date", smsMessage.getTimeStamp());
        jsonObject.put("body", smsMessage.getBody());
        return jsonObject;
    }
}
