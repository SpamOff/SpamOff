package nldr.spamoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import java.util.Random;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;

public class ScanFinished extends AppCompatActivity {

    private static final String ACTIVITY_STATUS = "state";
    final int MAX_SLIDE_VALUE = 162;
    BroadcastReceiver receiver;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_finished);
        final Context context = this;

        CookiesHandler.setIfWaitingForServer(this, true);

        Intent afterScanInformationIntent = new Intent(context, AfterScanInformationActivity.class);
        startActivity(afterScanInformationIntent);

        Button btnMoreInfo = (Button)findViewById(R.id.btnMoreInfo);
        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MoreInfo.class);
                startActivity(intent);

            }
        });

        final SeekBar slider = (SeekBar) findViewById(R.id.seekBarSpamOff);
        slider.setProgress(MAX_SLIDE_VALUE);

        slider.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Random rand = new Random();
                CookiesHandler.setIfWaitingForServer(context, false);
                CookiesHandler.setIfAlreadyScannedBefore(context, true);
                CookiesHandler.setSpamMessagesCount(context, rand.nextInt(100));
                CookiesHandler.setIfTermsApproved(context, false);

                Intent intent = new Intent(context, ScanResultsActivity.class);
                startActivity(intent);
                finish();
                seekBar.setProgress(MAX_SLIDE_VALUE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra(fbsMessagingService.UPDATE_MESSAGE) != null) {
                    openResultActivity();
                }
            }
        };
    }

    private void openResultActivity() {
        CookiesHandler.setIfWaitingForServer(this, false);
        CookiesHandler.setIfAlreadyScannedBefore(this, true);
        CookiesHandler.setIfTermsApproved(this, false);

        Intent intent = new Intent(this, ScanResultsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(fbsMessagingService.UPDATE_INTENT)
        );
        // Store our shared preference
        SharedPreferences sp = getSharedPreferences(ACTIVITY_STATUS, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.commit();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        // Store our shared preference
        SharedPreferences sp = getSharedPreferences("OURINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", false);
        ed.commit();
        super.onStop();
    }
}

