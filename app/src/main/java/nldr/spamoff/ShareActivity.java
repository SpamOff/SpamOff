package nldr.spamoff;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.internal.LikeContent;
import com.facebook.share.internal.LikeDialog;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareDialog;

public class ShareActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        callbackManager = CallbackManager.Factory.create();

        Button btnShareOnFacebook = (Button)findViewById(R.id.btnFacebookShare);
        btnShareOnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnFacebook();
            }
        });

        LikeView likeView = (LikeView) findViewById(R.id.like_view);
        likeView.setObjectIdAndType(
                "https://www.facebook.com/spamoff.co",
                LikeView.ObjectType.PAGE);

        Button btnLikeOnFacebook = (Button)findViewById(R.id.btnLikeOnFacebook);
        btnLikeOnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeOnFacebook();
            }
        });
    }

    private void shareOnFacebook() {

        ShareDialog shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

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
        LikeDialog likeDialog = new LikeDialog(this);
        LikeContent likeContent = LikeContent.CREATOR.newArray(1)[0];
        likeDialog.show(likeContent);
    }
}
