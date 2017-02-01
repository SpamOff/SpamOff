package nldr.spamoff;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;

public class fbsInstanceIdService extends FirebaseInstanceIdService {

    private final String TAG = "fbsInstanceIdService";

    public fbsInstanceIdService() {
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        CookiesHandler.setFirebaseTokenId(this, refreshedToken);

        // TODO : If the user deletes the app, he gets a new token and than we have to send it to the server,
        // TODO : but because we do not save anything about the user, we cant update its token.
        // TODO : If we want to handle this, we have to save any uniq key about the user and than send it with the new token
        //sendRegistrationToServer(refreshedToken);
    }
}
