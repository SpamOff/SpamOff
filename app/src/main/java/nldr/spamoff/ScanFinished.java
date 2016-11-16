package nldr.spamoff;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Date;
import java.util.Random;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;

public class ScanFinished extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Exit application", true);
        startActivity(intent);
        finish();
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



        ImageButton imageButton = (ImageButton)findViewById(R.id.imageView10);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Random rand = new Random();
                CookiesHandler.setIfWaitingForServer(context, false);
                CookiesHandler.setSpamMessagesCount(context, rand.nextInt(100));

                Intent intent = new Intent(context, ScanResultsActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
}
