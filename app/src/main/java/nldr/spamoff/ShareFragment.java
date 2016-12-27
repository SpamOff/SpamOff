package nldr.spamoff;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class ShareFragment extends Fragment {

    public ShareFragment() {
        // Required empty public constructor
    }


    public static ShareFragment newInstance(String param1, String param2) {
        ShareFragment fragment = new ShareFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share, container, false);

        ImageButton btnShare = (ImageButton)rootView.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sharer.showShareMenu(getActivity().getSupportFragmentManager());
            }
        });

        ImageButton btnMail = (ImageButton) rootView.findViewById(R.id.btnMail);
        btnMail.setOnClickListener(new View.OnClickListener() {
           @Override public void onClick(View v) {
               Intent email = new Intent(Intent.ACTION_SENDTO);
               email.setData(Uri.parse("mailto:sms@spamoff.co"));
               startActivity(email);
           }
        });

        ImageButton btnFaq = (ImageButton) rootView.findViewById(R.id.btnFaq);
        btnFaq.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://spamoff.co.il/faq")));
                }
        });

        ImageButton btnSite = (ImageButton) rootView.findViewById(R.id.btnSite);
        btnSite.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://spamoff.co.il/")));
            }
        });

        return rootView;
    }

}