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

        final Context context = this;
        Handler handler = new Handler();

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
