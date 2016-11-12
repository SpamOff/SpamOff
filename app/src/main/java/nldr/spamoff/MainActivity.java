package nldr.spamoff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;
import nldr.spamoff.SpamoffFetcher;
import nldr.spamoff.Networking.NetworkManager;
import nldr.spamoff.SMSHandler.SMSReader;
import nldr.spamoff.SMSHandler.SMSToJson;

public class MainActivity extends AppCompatActivity implements AsyncDataHandler.asyncTaskUIMethods {

    private static View rootView;
    private boolean bHasScanned = false;
    private SpamoffFetcher spamoffFetcher = new SpamoffFetcher(this, this, this);

    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity mainActivity = this;
        final Context context = this;
        final int MAX_SLIDE_VALUE = 162;
        final int MIN_SPAM_OPACITY_CHANGER_VALUE = 140;
        final int MIN_SLIDE_VALUE = 38;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!CookiesHandler.getIfTermsApproved(context)) {
            Intent intent = new Intent(context, FloatingTerms.class);
            startActivity(intent);
        }

        final SeekBar slider = (SeekBar) findViewById(R.id.seekBarSpamOff);
        slider.setProgress(MIN_SLIDE_VALUE);

        final ImageView spamView = (ImageView)findViewById(R.id.spam);
        spamView.setAlpha((float)0);

        bHasScanned = CookiesHandler.getIfAlreadyScannedBefore(context);

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > MAX_SLIDE_VALUE) {
                    seekBar.setProgress(MAX_SLIDE_VALUE);
                } if(progress <= MIN_SPAM_OPACITY_CHANGER_VALUE){
                    spamView.setAlpha((float)0);
                }
                if(progress > MIN_SPAM_OPACITY_CHANGER_VALUE) {
                    spamView.setAlpha((float)(progress - MIN_SPAM_OPACITY_CHANGER_VALUE) / (MAX_SLIDE_VALUE - MIN_SPAM_OPACITY_CHANGER_VALUE));
                }
                if (progress < MIN_SLIDE_VALUE) {
                    seekBar.setProgress(MIN_SLIDE_VALUE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > MIN_SLIDE_VALUE && seekBar.getProgress() <= MAX_SLIDE_VALUE) {
                    if (seekBar.getProgress() == MAX_SLIDE_VALUE) {
                        spamoffFetcher.fetchIfPermitted();
                    }

                    seekBar.setProgress(MIN_SLIDE_VALUE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        spamoffFetcher.onRequestPermissionsResult(permissions, grantResults);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateUI(String results) {
        Snackbar.make(rootView, results, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFetchingCancelled(String errorText, Throwable cause) {
        //Snackbar.make(null, errorText, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void startingToCheck(String checkName) {
        //Snackbar.make(null, checkName, Snackbar.LENGTH_SHORT).show();
    }

}