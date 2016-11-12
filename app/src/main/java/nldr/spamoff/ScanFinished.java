package nldr.spamoff;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ScanFinished extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_finished);

        final Context context = this;

        ImageButton imgBtnMoreInfo = (ImageButton)findViewById(R.id.imgbtnMoreInfo);
        imgBtnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MoreInfo.class);
                startActivity(intent);
            }
        });
    }
}
