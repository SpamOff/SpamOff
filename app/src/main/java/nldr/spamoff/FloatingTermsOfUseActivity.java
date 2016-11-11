package nldr.spamoff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;

public class FloatingTermsOfUseActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_terms_of_use);

        this.setFinishOnTouchOutside(false);

        ImageButton skipButton = (ImageButton)findViewById(R.id.skipButton);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CookiesHandler.setIfTermsApproved(context, true);
                finish();

            }

        });
    }
}
