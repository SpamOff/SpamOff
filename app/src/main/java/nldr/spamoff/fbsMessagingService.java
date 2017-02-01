package nldr.spamoff;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;

import static android.app.Notification.PRIORITY_MAX;

public class fbsMessagingService extends FirebaseMessagingService {

    static final public String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";

    static final public String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";

    private final String TAG = "fbsMessagingService";
    private final int mId = 165;

    public fbsMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);

        Intent intent = new Intent(COPA_RESULT);
        intent.putExtra(COPA_MESSAGE, "true");
        broadcaster.sendBroadcast(intent);

        // Checks if the user is really waiting for this notification or its a mistake
        // Removed it to allow the server send another notifications also
        //if(CookiesHandler.getIfWaitingForServer(this)) {
            this.showNotification(
                    Integer.parseInt(remoteMessage.getData().get(getResources().getString(R.string.firebase_messages_count_identifier))),
                    remoteMessage.getData().get(getResources().getString(R.string.firebase_link_to_view_answer_in_site_identifier)));
        //}
    }

    private void showNotification(int numberOfSpamMessages, String linkToSite) {

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ScanResultsActivity.class);
        CookiesHandler.setResultsUri(this, linkToSite);
        CookiesHandler.setSpamMessagesCount(this, numberOfSpamMessages);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ScanResultsActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("SpamOff")
                .setContentText("עלינו על הספאמרים , מגיע לך כסף!")
                .setSmallIcon(R.drawable.logo)
                .setPriority(PRIORITY_MAX)
                .setSound(defaultSoundUri)
                .setContentIntent(resultPendingIntent)
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(123, notification);
    }
}
