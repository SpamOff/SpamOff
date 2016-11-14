package nldr.spamoff;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import java.util.ArrayList;
import java.util.List;
import nldr.spamoff.AndroidStorageIO.CookiesHandler;

public class MainActivity
        extends AppCompatActivity
        implements AsyncDataHandler.usingAsyncFetcher {

    private String[] permissionsArray = new String[] {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_SMS
    };

    private myProgressDialog progressDialog = null;
    private boolean isFetching = false;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                        if (!isFetching)
                            fetchIfPermitted();
                        else {
                            Snackbar snc = Snackbar.make(seekBar, "לאט לאט.. תהליך אחר רץ ברקע..", Snackbar.LENGTH_SHORT);
                            snc.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            snc.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                            snc.show();
                        }
                    }

                    seekBar.setProgress(MIN_SLIDE_VALUE);
                }
            }
        });

        Button btnReset = (Button)findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CookiesHandler.setLastScanDate(context, 978300000000L);
                Snackbar snc = Snackbar.make(v, "ידוע שנים...", Snackbar.LENGTH_SHORT);
                snc.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                snc.show();
            }
        });

        progressDialog = new myProgressDialog(this, "", Color.RED);
        progressDialog.setCancelable(false);
    }

    private void fetchIfPermitted() {

        List<String> deniededPermissions = new ArrayList<String>();

        for (String curr : permissionsArray) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, curr) != PackageManager.PERMISSION_GRANTED) {

                deniededPermissions.add(curr);
            }
        }

        if (deniededPermissions.size() > 0) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    deniededPermissions.toArray(new String[deniededPermissions.size()]), 0);
        } else {
            onRequestPermissionsResult(0, null, null);
        }
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
    public void startedFetching() {
        progressDialog.show();
        this.isFetching = true;
    }

    @Override
    public void stoppedFetching() {
        progressDialog.cancel();
        this.isFetching = false;
    }

    @Override
    public void updateProgress(String prg) {

        if (progressDialog.isShowing())
            progressDialog.setTitle(prg);
        // TODO : Optional - update the progress with toasts when the progressLoader has dismissed
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
    public void cancelled() {
        new MaterialDialog.Builder(this)
                .content("לא הצלחנו לבצע את הסריקה, אנא נסה מאוחר יותר")
                .title("ארעה שגיאה בזמן ביצוע הסריקה")
                .titleGravity(GravityEnum.END)
                .buttonsGravity(GravityEnum.END)
                .contentGravity(GravityEnum.END)
                .positiveText("אישור").show();
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
}