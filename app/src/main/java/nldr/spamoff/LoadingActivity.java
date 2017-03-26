package nldr.spamoff;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.FacebookSdk;

import io.fabric.sdk.android.Fabric;
import nldr.spamoff.AndroidStorageIO.CookiesHandler;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        try {
            FacebookSdk.sdkInitialize(this.getApplicationContext());
            CookiesHandler.setIfFacebookInitialized(true);
        } catch (Exception ex) {
            Logger.writeToLog(ex);
        }

        try {
            Fabric.with(this, new Crashlytics());
            CookiesHandler.setIfCrashlyticsInitialized(true);
        } catch (Exception ex) {
            Logger.writeToLog(ex);
        }

        try {
            Fabric.with(this, new Answers());
            CookiesHandler.setIfAnswersInitialized(true);
        } catch (Exception ex) {
            Logger.writeToLog(ex);
        }

        final Context context = this;
        Handler handler = new Handler();

        // Trying to get the intent that called the app to wake up, neccessary to the fcm notifications
        try {
            Intent intent = getIntent();

            if (CookiesHandler.getIfTermsApproved(context) &&
                CookiesHandler.getIfWaitingForServer(context) &&
                    intent.getExtras() != null &&
                    intent.getExtras().containsKey("arrived-with-news-from-server") &&
                    intent.getExtras().containsKey(getResources().getString(R.string.firebase_messages_count_identifier)) &&
                    intent.getExtras().containsKey(getResources().getString(R.string.firebase_link_to_view_answer_in_site_identifier))) {
                CookiesHandler.setIfWaitingForServer(context, false);
                CookiesHandler.setIfAlreadyScannedBefore(context, true);
                CookiesHandler.setSpamMessagesCount(context,
                        Integer.parseInt(getIntent().getExtras().get(getResources().getString(R.string.firebase_messages_count_identifier)).toString()));
                CookiesHandler.setResultsUri(context,
                        getIntent().getExtras().get(getResources().getString(R.string.firebase_link_to_view_answer_in_site_identifier)).toString());
            }
        } catch (Exception ex) {
            Logger.writeToLog(ex);
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if (CookiesHandler.getIfWaitingForServer(context)) {
                    intent = new Intent(context, ScanFinished.class);
                } else if (CookiesHandler.getIfAlreadyScannedBefore(context)) {
                    intent = new Intent(context, ScanResultsActivity.class);
                } else {
                    intent = new Intent(context, MainActivity.class);
                }

                startActivity(intent);
                finish();
            }
        };

        handler.postDelayed(runnable, 1400);
    }

    @Override
    public void onBackPressed() {
    }
}
