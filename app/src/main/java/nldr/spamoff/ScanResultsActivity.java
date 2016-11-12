package nldr.spamoff;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;
import nldr.spamoff.Networking.NetworkManager;
import nldr.spamoff.SMSHandler.SMSReader;
import nldr.spamoff.SMSHandler.SMSToJson;

public class ScanResultsActivity extends AppCompatActivity {


    private SpamoffFetcher spamoffFetcher = new SpamoffFetcher(this, this, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_results);
        final int MAX_SLIDE_VALUE = 162;
        final int MIN_SPAM_OPACITY_CHANGER_VALUE = 140;
        final int MIN_SLIDE_VALUE = 38;


        final Context context = this;
        final TextView dateText = (TextView)findViewById(R.id.date);
        dateText.setText(getLastScanDate());

        final ImageView spamView = (ImageView)findViewById(R.id.spam);
        spamView.setAlpha((float)0);

        CookiesHandler.setResultsUri(context, "http://spamoff.co.il");

        final TextView spamCount = (TextView)findViewById(R.id.spamCount);
        spamCount.setText(""+
                CookiesHandler.getSpamMessagesCount(context));

        final ImageButton watchResultsButton = (ImageButton)findViewById(R.id.watchResultsButton);
        watchResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CookiesHandler.getResultsURI(context)));
                startActivity(browserIntent);
            }
        });

        final SeekBar slider = (SeekBar) findViewById(R.id.seekBarSpamOff);
        slider.setProgress(MIN_SLIDE_VALUE);
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > MAX_SLIDE_VALUE) {
                    seekBar.setProgress(MAX_SLIDE_VALUE);
                }
                if(progress <= MIN_SPAM_OPACITY_CHANGER_VALUE){
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

        ImageButton imgBtnMoreInfo = (ImageButton)findViewById(R.id.imgbtnMoreInfo);
        imgBtnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MoreInfo.class);
                startActivity(intent);
            }
        });
    }


    private String getLastScanDate(){
        Date lastScanDate = new Date(CookiesHandler.getLastScanDate(this));
        String lastScanDateString = lastScanDate.toString();
        String[] splitedArr = lastScanDateString.split(" ");
        return splitedArr[2] + "/" +  convertMonthNameToNumber(splitedArr[1]) + "/" + splitedArr[5];
    }
    private String convertMonthNameToNumber(String name)
    {
        switch (name){
            case "Jan":
                return "1";
            case "Feb":
                return "2";
            case "Mar":
                return "3";
            case "Apr":
                return "4";
            case "May":
                return "5";
            case "Jun":
                return "6";
            case "Jul":
                return "7";
            case "Aug":
                return "8";
            case "Sep":
                return "9";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
            default:
                return "0";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        spamoffFetcher.onRequestPermissionsResult(permissions, grantResults);
    }

}
