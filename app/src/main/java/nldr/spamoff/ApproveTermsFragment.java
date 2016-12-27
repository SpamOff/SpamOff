package nldr.spamoff;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.IMediaControllerCallback;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import nldr.spamoff.AndroidStorageIO.CookiesHandler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ApproveTermsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ApproveTermsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApproveTermsFragment extends Fragment {

    private String[] permissionsArray =
            new String[]{
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.READ_SMS
            };

    public ApproveTermsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ApproveTermsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ApproveTermsFragment newInstance(String param1, String param2) {
        ApproveTermsFragment fragment = new ApproveTermsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_approve_terms, container, false);
        final Context context = inflater.getContext();

        TextView textView = (TextView)view.findViewById(R.id.txtTerms);

//        Linkify.addLinks(txtView, Pattern.compile("תנאי השימוש"), "http://www.spamoff.co.il/uploads/2016/07/Terms-of-use.pdf");

//        SpannableString ss = new SpannableString("This is a cool text cool text cool text tcoooccococ");
////        SpannableString ss = new SpannableString("מאשר את הסכמתי לתנאי השימוש ומדיניות הפרטיות");
//        ClickableSpan terms = new ClickableSpan() {
//            @Override
//            public void onClick(View textView) {
//                Toast.makeText(textView.getContext(), "LOL", Toast.LENGTH_LONG).show();
//            }
//        };
//        ClickableSpan privacy = new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                Toast.makeText(textView.getContext(), "LOL1", Toast.LENGTH_LONG).show();
//            }
//        };
//        ss.setSpan(terms, 15, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(privacy, 28, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        textView.setText(ss);
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//        textView.setHighlightColor(Color.TRANSPARENT);

        ImageButton approveButton = (ImageButton) view.findViewById(R.id.approveButton);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CookiesHandler.setIfTermsApproved(context, true);
                getNeededPermissions();
                getActivity().finish();
            }
        });

        return view;
    }

    private void getNeededPermissions() {

        List<String> deniededPermissions = new ArrayList<String>();

        for (String curr : permissionsArray) {
            if (ContextCompat.checkSelfPermission(getActivity(), curr) != PackageManager.PERMISSION_GRANTED) {

                deniededPermissions.add(curr);
            }
        }

        if (deniededPermissions.size() > 0) {
            ActivityCompat.requestPermissions(getActivity(),
                    deniededPermissions.toArray(new String[deniededPermissions.size()]), 0);
        } else {
            onRequestPermissionsResult(0, null, null);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
}
