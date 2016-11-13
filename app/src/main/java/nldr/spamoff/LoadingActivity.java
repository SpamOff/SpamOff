package nldr.spamoff;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

                startActivity(new Intent(context, MainActivity.class));
                // TODO : Remove
                //startActivity(intent);
                finish();
            }
        };

        handler.postDelayed(runnable, 2200);
    }
}
