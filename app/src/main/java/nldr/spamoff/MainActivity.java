package nldr.spamoff;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import nldr.spamoff.AndroidStorageIO.CookiesHandler;
import nldr.spamoff.AndroidStorageIO.LastScanIO;
import nldr.spamoff.Networking.MyRequestQueue;
import nldr.spamoff.Networking.NetworkManager;
import nldr.spamoff.SMSHandler.SMSReader;
import nldr.spamoff.SMSHandler.SMSToJson;
import nldr.spamoff.AndroidStorageIO.DateStorageIO;

public class MainActivity extends AppCompatActivity implements AsyncDataHandler.asyncTaskUIMethods {

    private static View rootView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private android.widget.CheckBox chkAccept;

    private boolean bHasScanned = false;
    private String[] permissionsArray =
            new String[] {
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.READ_SMS
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity mainActivity = this;
        final Context context = this;
        final int MAX_SLIDE_VALUE = 152;
        final int MIN_SLIDE_VALUE = 51;
        final int TIME_INTERVAL = 250;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SeekBar slider = (SeekBar)findViewById(R.id.seekBarSpamOff);
        slider.setProgress(MAX_SLIDE_VALUE);

        final Button btnLastScan = (Button)findViewById(R.id.btnLastScan);

        AnimationDrawable animationTop = new AnimationDrawable();
        animationTop.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows1), TIME_INTERVAL);
        animationTop.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows2), TIME_INTERVAL);
        animationTop.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows3), TIME_INTERVAL);
        animationTop.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows4), TIME_INTERVAL);
        animationTop.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows5), TIME_INTERVAL);
        animationTop.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows6), TIME_INTERVAL);
        animationTop.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows7), TIME_INTERVAL);
        animationTop.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows8), TIME_INTERVAL);
        animationTop.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows9), TIME_INTERVAL);
        animationTop.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows10), TIME_INTERVAL);
        animationTop.setOneShot(false);
        final ImageView arrowsTop = (ImageView) findViewById(R.id.arrowsTop);
        arrowsTop.setImageDrawable(animationTop);
        animationTop.start();

        AnimationDrawable animationBot = new AnimationDrawable();
        animationBot.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows1), TIME_INTERVAL);
        animationBot.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows2), TIME_INTERVAL);
        animationBot.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows3), TIME_INTERVAL);
        animationBot.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows4), TIME_INTERVAL);
        animationBot.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows5), TIME_INTERVAL);
        animationBot.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows6), TIME_INTERVAL);
        animationBot.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows7), TIME_INTERVAL);
        animationBot.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows8), TIME_INTERVAL);
        animationBot.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows9), TIME_INTERVAL);
        animationBot.addFrame(ContextCompat.getDrawable(context, R.drawable.arrows10), TIME_INTERVAL);
        animationBot.setOneShot(false);

        final ImageView arrowsBot = (ImageView) findViewById(R.id.arrowsBot);
        arrowsBot.setImageDrawable(animationBot);
        animationBot.start();

        bHasScanned = CookiesHandler.getIfAlreadyScannedBefore(context);
        //bHasScanned = LastScanIO.read(context);

        btnLastScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (bHasScanned) {
                    Intent intent = new Intent(context, lastScanActivity.class);
                    intent.putExtra("date", CookiesHandler.getLastScanDate(context));
                    intent.putExtra("money", 8000);
                    startActivity(intent);
                } else {
                    Snackbar snc = Snackbar.make(v, "לא ביצעת סריקה בעבר", Snackbar.LENGTH_LONG);
                    snc.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    snc.show();
                }
            }
        });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

//        CircleIndicator indicator = (CircleIndicator)findViewById(R.id.indicator);
//        indicator.setViewPager(mViewPager);



        chkAccept = (android.widget.CheckBox)findViewById(R.id.chkAcceptTerms);

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > MAX_SLIDE_VALUE) {
                    seekBar.setProgress(MAX_SLIDE_VALUE);
                }
                if (progress < MIN_SLIDE_VALUE) {
                    seekBar.setProgress(MIN_SLIDE_VALUE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() >= MIN_SLIDE_VALUE && seekBar.getProgress() < MAX_SLIDE_VALUE) {
                    if (seekBar.getProgress() == MIN_SLIDE_VALUE) {
                        stopTheSpamm(seekBar, context);
                    }

                    seekBar.setProgress(MAX_SLIDE_VALUE);
                }
            }
        });

        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setDragView(R.id.sliding_layout);

//        Dialog dialog = new Dialog(this);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.activity_intro);
//        dialog.show();
    }

    private void stopTheSpamm(View v, final Context context){

        if (chkAccept.isChecked()) {
            showInfromativeDialog(v, context);
        } else {
            Snackbar snc = Snackbar.make(v, "אנא אשר שקראת את הכתוב למעלה", Snackbar.LENGTH_LONG);
            snc.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            snc.setAction("אשר", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chkAccept.setChecked(true);
                    showInfromativeDialog(v, context);
                }
            });
            snc.show();
        }
    }

    private void showInfromativeDialog(View v, final Context context) {

        new MaterialDialog.Builder(context)
            .content(R.string.explenationModal)
            .title("מה הולך לקרות?")
            .titleGravity(GravityEnum.END)
            .buttonsGravity(GravityEnum.END)
            .contentGravity(GravityEnum.END)
            .positiveText("המשך")
            .negativeText("לא תודה")
            .onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    CookiesHandler.setLastScanDate(context, 978300000000L);
                }
            })
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {
                    getNeededPermissions();
                }
            }).show();
    }

    private void getNeededPermissions() {

        List<String> deniededPermissions = new ArrayList<String>();

        for (String curr : permissionsArray) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, curr) != PackageManager.PERMISSION_GRANTED) {

                deniededPermissions.add(curr);
            }
        }

        if (deniededPermissions.size() > 0) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    deniededPermissions.toArray(new String[deniededPermissions.size()]), 0);
        } else {
            onRequestPermissionsResult(0, null, null);
        }
    }

    private void fetchWithPermissions() {

        final ProgressDialog progressDialog = new ProgressDialog(this, "טוען את ההודעות החדשות...");
        progressDialog.show();

        final Context context = this;
        final JSONArray[] finalJsonObject = new JSONArray[] {new JSONArray()};

        Runnable smsCollector = new Runnable() {
            @Override
            public void run() {
                try {
                    Date lastScanDate = new Date(CookiesHandler.getLastScanDate(context));
                    //JSONObject jsonObject = SMSToJson.parseAll(context, SMSReader.read(context, lastScanDate));
                    JSONArray jsonArray = SMSToJson.parseAllToArray(context, SMSReader.read(context, lastScanDate));
                    finalJsonObject[0] = jsonArray;
                    CookiesHandler.setLastScanMessagesCount(context, jsonArray.length());
                    CookiesHandler.setLastScanDate(context, System.currentTimeMillis());
                    CookiesHandler.setIfAlreadyScannedBefore(context, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        Intent intent = new Intent(context, ScanFinished.class);
                        startActivity(intent);
                    }
                });

                if (NetworkManager.sendJsonToServer(context, finalJsonObject[0])) {
                    Log.d("Good", "Sent to the server");
                } else {
                    Log.d("Problem", "Problem");
                }
            }
        };

        runOnUiThread(smsCollector);
        //smsCollector.run();

        // TODO : Runnable for sending the data to the server but checks before how many messages sent last time and if there is no diff it doesnt send (diff - delete and get new one?)
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

    /**
     * @returns true if all the permissions granted and false if not
     */
    private boolean checkAllPermissions() {
        for (String curr : permissionsArray) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, curr) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean bAcceptedAll = true;

        // IT if it null - there are already permissions
        if (permissions != null && grantResults != null) {

            // Goes on each result and checks if it is granted
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED)
                    bAcceptedAll = false;
            }
        }

        if (bAcceptedAll) {
            this.fetchWithPermissions();
        } else {
            new MaterialDialog.Builder(this)
                    .content("כדי שנוכל לבצע את הסריקה יש לאשר את הגישה של האפליקציה לאנשי הקשר וההודעות.")
                    .title("לא נמצאו האישורים המתאימים לביצוע הסריקה")
                    .titleGravity(GravityEnum.END)
                    .buttonsGravity(GravityEnum.END)
                    .contentGravity(GravityEnum.END)
                    .positiveText("אוקי").show();
        }
    }

    @Override
    public void updateUI(String results) {
        Snackbar.make(rootView, results, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFetchingCancelled(String errorText, Throwable cause) {
        //Snackbar.make(null, errorText, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void startingToCheck(String checkName) {
        //Snackbar.make(null, checkName, Snackbar.LENGTH_SHORT).show();
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
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
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