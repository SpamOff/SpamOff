package nldr.spamoff;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nldr.spamoff.AndroidStorageIO.CookiesHandler;

public class ScanResultsActivity
        extends AppCompatActivity
        implements AsyncDataHandler.usingAsyncFetcher {

    private Context context;

    private String[] permissionsArray = new String[] {
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.READ_SMS
    };

    private myProgressDialog progressDialog = null;
    private boolean isFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = this;

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

        // TODO : WTF?
        CookiesHandler.setResultsUri(context, "http://spamoff.co.il");

        final TextView spamCount = (TextView)findViewById(R.id.spamCount);
        spamCount.setText("" +
                CookiesHandler.getSpamMessagesCount(context));

        final ImageButton watchResultsButton = (ImageButton)findViewById(R.id.watchResultsButton);
        watchResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CookiesHandler.getResultsURI(context)));
                startActivity(browserIntent);*/
                CookiesHandler.setIfAlreadyScannedBefore(context, true);
                CookiesHandler.setIfWaitingForServer(context, false);
                CookiesHandler.setIfTermsApproved(context, true);
                CookiesHandler.setLastScanDate(context, 978300000000L);

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();

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
                if (progress <= MIN_SPAM_OPACITY_CHANGER_VALUE) {
                    spamView.setAlpha((float) 0);
                }
                if (progress > MIN_SPAM_OPACITY_CHANGER_VALUE) {
                    spamView.setAlpha((float) (progress - MIN_SPAM_OPACITY_CHANGER_VALUE) / (MAX_SLIDE_VALUE - MIN_SPAM_OPACITY_CHANGER_VALUE));
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
                        if (seekBar.getProgress() == MAX_SLIDE_VALUE) {
                            if (!isFetching)
                                fetchIfPermitted();
                            else {
                                Snackbar snc = Snackbar.make(seekBar, "לאט לאט.. תהליך אחר רץ ברקע..", Snackbar.LENGTH_SHORT);
                                snc.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                                snc.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                snc.show();
                            }
                        }
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


        progressDialog = new myProgressDialog(this, "", Color.RED);
        progressDialog.setCancelable(false);
    }

    private String getLastScanDate() {
        Date lastScanDate = new Date(CookiesHandler.getLastScanDate(this));
        String lastScanDateString = lastScanDate.toString();
        String[] splitedArr = lastScanDateString.split(" ");
        return splitedArr[2] + "/" +  convertMonthNameToNumber(splitedArr[1]) + "/" + splitedArr[5];
    }

    private String convertMonthNameToNumber(String name) {
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

    public void fetchIfPermitted() {

        List<String> deniededPermissions = new ArrayList<String>();

        for (String curr : permissionsArray) {

            if (ContextCompat.checkSelfPermission(this, curr) != PackageManager.PERMISSION_GRANTED) {

                deniededPermissions.add(curr);
            }
        }

        if (deniededPermissions.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    deniededPermissions.toArray(new String[deniededPermissions.size()]), 0);
        } else {
            onRequestPermissionsResult(0, null, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean bAcceptedAll = true;

        // IT if it null - there are already permissions
        if (permissions != null && grantResults != null) {

            // Goes on each result and checks if it is granted
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED)
                    bAcceptedAll = false;
            }
        }

        if (bAcceptedAll) {
            AsyncDataHandler.performInBackground(this, this);
        } else {
            new MaterialDialog.Builder(this)
                    .content("כדי שנוכל לבצע את הסריקה יש לאשר את הגישה של האפליקציה לאנשי הקשר וההודעות.")
                    .title("לא נמצאו האישורים המתאימים לביצוע הסריקה")
                    .titleGravity(GravityEnum.END)
                    .buttonsGravity(GravityEnum.END)
                    .contentGravity(GravityEnum.END)
                    .positiveText("אישור").show();
        }
    }

    @Override
    public void updateProgress(String prg) {
        progressDialog.setTitle(prg);
    }

    @Override
    public void noNewMessages() {
        new MaterialDialog.Builder(this)
                .content("לא נמצאו הודעות חדשות מאז הסריקה האחרונה")
                .title("הסריקה נגמרה")
                .titleGravity(GravityEnum.END)
                .buttonsGravity(GravityEnum.END)
                .contentGravity(GravityEnum.END)
                .positiveText("אישור").show();
    }

    @Override
    public void finished() {
        Intent intent = new Intent(this, ScanFinished.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void error(String errorMessage) {
        new MaterialDialog.Builder(this)
                .content(errorMessage)
                .title("ארעה שגיאה בזמן ביצוע הסריקה")
                .titleGravity(GravityEnum.END)
                .buttonsGravity(GravityEnum.END)
                .contentGravity(GravityEnum.END)
                .positiveText("אישור").show();
    }

    @Override
    public void startedFetching() {
        this.progressDialog.show();
        this.isFetching = true;
    }

    @Override
    public void stoppedFetching() {
        this.progressDialog.cancel();
        this.isFetching = false;
    }

    @Override
    public void cancelled() {
        new MaterialDialog.Builder(this)
                .content("כדי שנוכל לסרוק ולשלוח את הודעות הספאם שלך דרוש חיבור אינטרנט זמין ומהיר מספיק")
                .title("ארעה שגיאה בזמן ביצוע הסריקה")
                .titleGravity(GravityEnum.END)
                .buttonsGravity(GravityEnum.END)
                .contentGravity(GravityEnum.END)
                .positiveText("אישור").show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Exit application", true);
        startActivity(intent);
        finish();
    }
}
