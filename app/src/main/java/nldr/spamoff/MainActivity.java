package nldr.spamoff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import info.hoang8f.widget.FButton;
import nldr.spamoff.SMSHandler.SMSReader;
import nldr.spamoff.SMSHandler.SMSToJson;

public class MainActivity extends AppCompatActivity {

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity mainActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btn = (ImageButton)findViewById(R.id.btnSpamOff);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(mainActivity,
                        new String[]{android.Manifest.permission.READ_SMS, android.Manifest.permission.READ_PHONE_STATE},
                        0);

                try {
                    JSONObject jsonObject = SMSToJson.parseAll(context, SMSReader.read(context, new Date(82233213123L)));
                    Log.v("check", jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(MainActivity.this, ScanResultsActivity.class);
                startActivity(intent);
            }
        });

        /*FButton btnMoreInfo = (FButton)findViewById(R.id.btnMoreInfo);

        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OurServiceActivity.class);
                startActivity(intent);
            }
        });*/

        /*SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.set*/

        /*SeekBar sb = (SeekBar)findViewById(R.id.myseek);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > 95) {
                } else {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.info));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (progress > 95) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.logo));
                }

            }
        });

        SeekBar seekBar = (SeekBar) findViewById(R.id.seek);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarProgress = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "SeekBar Touch Stop ", Toast.LENGTH_SHORT).show();
            }

        });*/
    }
}
