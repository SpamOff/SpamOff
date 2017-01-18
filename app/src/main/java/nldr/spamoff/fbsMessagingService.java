package nldr.spamoff;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;

public class fbsMessagingService extends FirebaseMessagingService {

    private final String TAG = "fbsMessagingService";
    private final int mId = 165;

    public fbsMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        this.showNotification(
                Integer.parseInt(remoteMessage.getData().get(R.string.firebase_messages_count_identifier)),
                remoteMessage.getData().get(R.string.firebase_link_to_view_answer_in_site_identifier));
    }

    private void showNotification(int numberOfSpamMessages, String linkToSite) {

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ScanResultsActivity.class);
        resultIntent.putExtra(getResources().getString(R.string.firebase_messages_count_identifier), numberOfSpamMessages);
        resultIntent.putExtra(getResources().getString(R.string.firebase_link_to_view_answer_in_site_identifier), linkToSite);
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

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setSound(defaultSoundUri)
                        .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());

        // Pops up the results scan sctivity when the reciving message from the server and the app is running
//        Intent intent = new Intent(this, ScanResultsActivity.class);
//        CookiesHandler.setSpamMessagesCount(this, numberOfSpamMessages);
//        CookiesHandler.setIfWaitingForServer(this, false);
//        CookiesHandler.setResultsUri(this, linkToSite);
//        startActivity(intent);
    }
}
