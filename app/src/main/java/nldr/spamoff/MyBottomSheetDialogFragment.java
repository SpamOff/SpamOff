package nldr.spamoff;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.internal.LikeContent;
import com.facebook.share.internal.LikeDialog;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by MG on 17-07-2016.
 */
public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    String mString;
    private CallbackManager callbackManager;

    static MyBottomSheetDialogFragment newInstance(String string) {
        MyBottomSheetDialogFragment f = new MyBottomSheetDialogFragment();
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_modal, container, false);

        Button btnShareOnFacebook = (Button)v.findViewById(R.id.btnFacebookShare);
        btnShareOnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnFacebook();
            }
        });

        LikeView likeView = (LikeView)v.findViewById(R.id.like_view);
        likeView.setObjectIdAndType(
                "https://www.facebook.com/spamoff.co",
                LikeView.ObjectType.PAGE);

        Button btnLikeOnFacebook = (Button)v.findViewById(R.id.btnLikeOnFacebook);
        btnLikeOnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeOnFacebook();
            }
        });

        return v;
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