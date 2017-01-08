package nldr.spamoff;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.FacebookSdk;

import org.apache.http.cookie.Cookie;

import io.fabric.sdk.android.Fabric;
import nldr.spamoff.AndroidStorageIO.CookiesHandler;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        final Context context = this;
        Handler handler = new Handler();

        // Trying to get the intent that called the app to wake up, neccessary to the fcm notifications
        try {
            Intent intent = getIntent();
            if (intent.getExtras().containsKey("arrived-with-news-from-server") &&
                intent.getExtras().containsKey("spam-count") &&
                intent.getExtras().containsKey("spam-link")) {
                    CookiesHandler.setIfWaitingForServer(context, false);
                    CookiesHandler.setIfAlreadyScannedBefore(context, true);
                    CookiesHandler.setSpamMessagesCount(context, Integer.parseInt(getIntent().getExtras().get("spam-count").toString()));
                    CookiesHandler.setResultsUri(context, getIntent().getExtras().get("spam-link").toString());
            }
        } catch (Exception ex) {
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

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());
    }

    @Override
    public void onBackPressed() {
    }
}
