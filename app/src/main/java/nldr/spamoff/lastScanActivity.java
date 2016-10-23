package nldr.spamoff;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

public class lastScanActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_scan);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        int maxMoney = getIntent().getExtras().getInt("money");
        String maxMoneyText = "אתה יכול להרוויח עד "+maxMoney + " ש\"ח";

        TextView maxMoneyTextView = (TextView)findViewById(R.id.lastMoney);
        maxMoneyTextView.setText(maxMoneyText);

        Date lastScanDate = new Date(getIntent().getExtras().getLong("date"));
        String lastDateText = "הסריקה האחרונה היתה ב-" +lastScanDate.toString();

        TextView lastDateTextView = (TextView)findViewById(R.id.lastDate);
        lastDateTextView.setText(lastDateText);


        ImageButton btnDescription = (ImageButton)findViewById(R.id.description);
        btnDescription.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton) v;
                        view.getDrawable().setColorFilter(0x55000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }

                return false;
            }
            });
        btnDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.spamoff.co.il"); // missing 'http://' will cause crashed
                Intent spamOffWeb = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(spamOffWeb);
            }
        });
    }
}
