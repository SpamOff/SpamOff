package nldr.spamoff;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.widgets.SnackBar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.Date;

import info.hoang8f.widget.FButton;
import nldr.spamoff.SMSHandler.SMSReader;
import nldr.spamoff.SMSHandler.SMSToJson;

import java.util.Timer;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {



    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity mainActivity = this;
        final Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btn = (ImageButton)findViewById(R.id.btnSpamOff);


        /*FButton btnMoreInfo = (FButton)findViewById(R.id.btnMoreInfo);

        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OurServiceActivity.class);
                startActivity(intent);
            }
        });*/
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final android.widget.CheckBox chkAccept =
                (android.widget.CheckBox)findViewById(R.id.chkAcceptTerms);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (chkAccept.isChecked()) {
                    new MaterialDialog.Builder(context)
                            .positiveText("OK")
                            .negativeText("NOT OK")
                            .content(R.string.explenationModal)
                            .title("מה הולך לקרות?")
                            .titleGravity(GravityEnum.END)
                            .buttonsGravity(GravityEnum.END)
                            .contentGravity(GravityEnum.END)
                            .positiveText("המשך")
                            .negativeText("לא תודה")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    Snackbar.make(v, "lol", Snackbar.LENGTH_SHORT).show();

                                    ////lior's code

                                    checkPermission(mainActivity, android.Manifest.permission.READ_CONTACTS);
                                    checkPermission(mainActivity, android.Manifest.permission.READ_SMS);

                                            try {
                                              /*  new MaterialDialog.Builder(context)
                                                        .title("שנייה לפני שמתחילים")
                                                        .content("זה אולי ישמע כאילו אנחנו מתחילים איתך.. אבל... מה מספר הפלאפון שלך?")
                                                        .inputType(InputType.TYPE_CLASS_NUMBER)
                                                        .inputRangeRes(10, 10, R.color.material_red_500)
                                                        .input("מספר הפלאפון", "05", new MaterialDialog.InputCallback() {
                                                            @Override
                                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                                Snackbar.make(v, "looal", Snackbar.LENGTH_SHORT).show();
                                                            }
                                                        }).show();*/
                                                JSONObject jsonObject = SMSToJson.parseAll(context, SMSReader.read(context, new Date(82233213123L)));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                            }).show();
                } else {
                    Snackbar snc = Snackbar.make(v, "אנא אשר שקראת את הכתוב למעלה", Snackbar.LENGTH_LONG);
                    snc.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    snc.setAction("אשר", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            chkAccept.setChecked(true);
                        }
                    });
                    snc.show();


                }
            }
        });

      /*  btnStop.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
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
        });*/






        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(null, "LOLLLLL", Snackbar.LENGTH_SHORT).show();
                ((SlidingUpPanelLayout) v).onDragEvent(null); //setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        slidingUpPanelLayout.setDragView(R.id.sliding_layout);
    }

    private void slideUp() {
        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        slidingUpPanelLayout.setDragView(findViewById(R.id.btnStop));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkPermission(Activity activity, String permission)
    {
        if (ContextCompat.checkSelfPermission(activity,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    ourServiceFragment tab1 = new ourServiceFragment();
                    return tab1;
                case 1:
                    whoWeAreFragment tab2 = new whoWeAreFragment();
                    return tab2;
                case 2:
                    whyFragment tab3 = new whyFragment();
                    return tab3;
                case 3:
                    howLongFragment tab4 = new howLongFragment();
                    return tab4;
                case 4:
                    howMuchFragment tab5 = new howMuchFragment();
                    return tab5;
                default:
                    return null;
            }

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "";
                case 1:
                    return " ";
                case 2:
                    return "3";
                case 3:
                    return " ";
                case 4:
                    return " ";
            }
            return null;
        }
    }
}