package nldr.spamoff;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.*;
import com.facebook.share.internal.LikeContent;
import com.facebook.share.internal.LikeDialog;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareDialog;

public class share extends AppCompatActivity {

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final Context context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        callbackManager = CallbackManager.Factory.create();

        ((LinearLayout)findViewById(R.id.facebook_share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnFacebook();
            }
        });

        ((LinearLayout)findViewById(R.id.facebook_like)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeOnFacebook();
            }
        });

        ((LinearLayout)findViewById(R.id.facebook_rate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateOnFacebook();
            }
        });

        ((LinearLayout)findViewById(R.id.rate_the_app)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "בקרוב...", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void shareOnFacebook() {

        ShareDialog shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<com.facebook.share.Sharer.Result>() {
            @Override
            public void onSuccess(com.facebook.share.Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("אפליקיית SpamOff")
                    .setContentDescription(
                            "גם אני הורדתי את האפליקציה והתחלתי להרוויח כסף על ההודעות ספאם שלי!")
                    //.setImageUrl()
                    .setContentUrl(Uri.parse("spamoff.co.il"))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    private void likeOnFacebook() {
//        LikeView likeView = (LikeView)v.findViewById(R.id.like_view);
//        likeView.setObjectIdAndType(
//                "https://www.facebook.com/spamoff.co",
//                LikeView.ObjectType.PAGE);


//        LikeDialog likeDialog = new LikeDialog(this);
//        LikeContent likeContent = LikeContent.CREATOR.newArray(1)[0];
//        likeDialog.show(likeContent);

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/spamoff.co")));
    }

    private void rateOnFacebook() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pg/spamoff.co/reviews/")));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home)
            finish();

        return true;
    }
}
