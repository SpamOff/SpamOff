package nldr.spamoff;

import android.app.Activity;
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
import android.view.View;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import nldr.spamoff.AndroidStorageIO.LastScanIO;
import nldr.spamoff.SMSHandler.SMSReader;
import nldr.spamoff.SMSHandler.SMSToJson;
import nldr.spamoff.AndroidStorageIO.DateStorageIO;

public class MainActivity extends AppCompatActivity implements AsyncDataHandler.asyncTaskUIMethods {

    final AsyncDataHandler.asyncTaskUIMethods lala = this;
    static View rootView;
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
        final int MAX_SLIDE_VALUE = 152;
        final int MIN_SLIDE_VALUE = 51;
        final int TIME_INTERVAL = 500;
        final boolean isLastScanned;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SeekBar slider = (SeekBar)findViewById(R.id.seekBar);
        slider.setProgress(MAX_SLIDE_VALUE);
        final ImageButton btnLastScan = (ImageButton)findViewById(R.id.lastScan);
       // ImageButton btnSpamOff = (ImageButton)findViewById(R.id.btnSpamOff);

        AnimationDrawable animationTop = new AnimationDrawable();
        animationTop.addFrame(getDrawable(R.drawable.arrows1), TIME_INTERVAL);
        animationTop.addFrame(getDrawable(R.drawable.arrows2), TIME_INTERVAL);
        animationTop.addFrame(getDrawable(R.drawable.arrows3), TIME_INTERVAL);
        animationTop.addFrame(getDrawable(R.drawable.arrows4), TIME_INTERVAL);
        animationTop.addFrame(getDrawable(R.drawable.arrows5), TIME_INTERVAL);
        animationTop.addFrame(getDrawable(R.drawable.arrows6), TIME_INTERVAL);
        animationTop.addFrame(getDrawable(R.drawable.arrows7), TIME_INTERVAL);
        animationTop.addFrame(getDrawable(R.drawable.arrows8), TIME_INTERVAL);
        animationTop.addFrame(getDrawable(R.drawable.arrows9), TIME_INTERVAL);
        animationTop.addFrame(getDrawable(R.drawable.arrows10), TIME_INTERVAL);

        animationTop.setOneShot(false);

        final ImageView arrowsTop = (ImageView) findViewById(R.id.arrowsTop);


        arrowsTop.setImageDrawable(animationTop);

        animationTop.start();

        AnimationDrawable animationBot = new AnimationDrawable();
        animationBot.addFrame(getDrawable(R.drawable.arrows1), TIME_INTERVAL);
        animationBot.addFrame(getDrawable(R.drawable.arrows2), TIME_INTERVAL);
        animationBot.addFrame(getDrawable(R.drawable.arrows3), TIME_INTERVAL);
        animationBot.addFrame(getDrawable(R.drawable.arrows4), TIME_INTERVAL);
        animationBot.addFrame(getDrawable(R.drawable.arrows5), TIME_INTERVAL);
        animationBot.addFrame(getDrawable(R.drawable.arrows6), TIME_INTERVAL);
        animationBot.addFrame(getDrawable(R.drawable.arrows7), TIME_INTERVAL);
        animationBot.addFrame(getDrawable(R.drawable.arrows8), TIME_INTERVAL);
        animationBot.addFrame(getDrawable(R.drawable.arrows9), TIME_INTERVAL);
        animationBot.addFrame(getDrawable(R.drawable.arrows10), TIME_INTERVAL);

        animationBot.setOneShot(false);

        final ImageView arrowsBot = (ImageView) findViewById(R.id.arrowsBot);


        arrowsBot.setImageDrawable(animationBot);

        animationBot.start();
        

     
        isLastScanned = LastScanIO.read(context);

        if(isLastScanned) {
            setLastScanButtonOnClickListener(context, btnLastScan);

        } else {
            btnLastScan.getDrawable().setColorFilter(0xBBFFFFFF, PorterDuff.Mode.SRC_ATOP);
            btnLastScan.invalidate();
            btnLastScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Snackbar snc = Snackbar.make(v, "לא ביצעת סריקה בעבר", Snackbar.LENGTH_LONG);
                    snc.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    snc.show();
                }
            });
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final android.widget.CheckBox chkAccept =
                (android.widget.CheckBox)findViewById(R.id.chkAcceptTerms);

        chkAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    askPermission(mainActivity, android.Manifest.permission.READ_CONTACTS);
                    askPermission(mainActivity, android.Manifest.permission.READ_SMS);
                }
            }
        });


        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress > MAX_SLIDE_VALUE){
                    seekBar.setProgress(MAX_SLIDE_VALUE);
                }
                if(progress < MIN_SLIDE_VALUE){
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
                        onMinExceeded(seekBar, context, isLastScanned, chkAccept, btnLastScan);
                    }
                }
                    seekBar.setProgress(MAX_SLIDE_VALUE);
            }
        });

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

    private void onMinExceeded(final View v, final Context context, final boolean isLastScanned, final android.widget.CheckBox chkAccept, final ImageButton btnLastScan){
        if (chkAccept.isChecked()) {
            if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
                Snackbar snc = Snackbar.make(v, "לא נוכל להמשיך בלי הרשאות לקריאת ההודעות ואנשי הקשר אנא החלק שוב לאחר שאישרת", Snackbar.LENGTH_LONG);
                snc.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                snc.show();
                askPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS);
                askPermission(MainActivity.this, android.Manifest.permission.READ_SMS);
                return;
            }

            showInfromativeDialog(v, context, isLastScanned, btnLastScan);

        } else {
            Snackbar snc = Snackbar.make(v, "אנא אשר שקראת את הכתוב למעלה", Snackbar.LENGTH_LONG);
            snc.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            snc.setAction("אשר", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chkAccept.setChecked(true);
                    showInfromativeDialog(v, context, isLastScanned, btnLastScan);
                }
            });
            snc.show();


        }
    }

    private void showInfromativeDialog(final View v, final Context context, final boolean isLastScanned, final ImageButton btnLastScan){

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
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DateStorageIO.write(context, 978300000000L);
                        setLastScanButtonOnClickListener(context, btnLastScan);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        try {

                            final long lastScanDate = DateStorageIO.read(context);
                            runAsync(context, lastScanDate);

                            DateStorageIO.write(context, System.currentTimeMillis());

                            if (!isLastScanned) {
                                LastScanIO.write(context, true);
                                btnLastScan.getDrawable().clearColorFilter();
                                btnLastScan.invalidate();

                                setLastScanButtonOnClickListener(context, btnLastScan);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).show();



    }

    private void runAsync(final Context context, long lastScanDate){
        try {
            JSONObject jsonObject = SMSToJson.parseAll(context, SMSReader.read(context, new Date(lastScanDate)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // AsyncDataHandler.runTaskInBackground(null, context, jsonObject.toString(), "");
    }

    private void setLastScanButtonOnClickListener(final Context context, ImageButton btnLastScan){
        btnLastScan.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton) v;
                        //overlay is black with transparency of 0x77 (119)
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

        btnLastScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(context, lastScanActivity.class);
                intent.putExtra("date", DateStorageIO.read(context));
                intent.putExtra("money", 8000);
                startActivity(intent);
            }
        });

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

    public void askPermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
*/
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
  //          }
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

    public class PositionHandler{
        int position = 1;
        int getPosition(){return position;}
        void setPosition(){
            if(position == 9){
                position = 1;
            }
            else{
                position++;
            }
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