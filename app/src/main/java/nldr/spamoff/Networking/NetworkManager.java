package nldr.spamoff.Networking;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.telecom.StatusHints;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


/**
 * Created by Roee on 10/11/2016.
 */
public class NetworkManager {

    public static String serverUrl = "https://spamofftestserver.herokuapp.com/lol";
    // "http://spamoff.co.il/uploadClient/uploadTxts"

    public static boolean sendJsonToServer(Context context, JSONArray jsonToSend) {

        final boolean[] bWasSentSuccessfully = new boolean[] {true};

        // Get a RequestQueue
        RequestQueue queue = MyRequestQueue.getInstance(context.getApplicationContext()).getRequestQueue();

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.POST, serverUrl, jsonToSend,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response from server : ", "lol");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.getMessage());
                        bWasSentSuccessfully[0] = false;
                    }
                }) {

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));

                    return Response.success(new JSONArray(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));

                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };

        queue.add(jsObjRequest);

        return bWasSentSuccessfully[0];


    }
}
