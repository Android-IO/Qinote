package com.example.qinote;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PictureViewerActivity extends AppCompatActivity {

    private static final String LOG_TAG = PictureViewerActivity.class.getSimpleName();

    private String mPicPathArrayString;
    private ArrayList<String> mNoteImagePath;
    private int mCurrentImagePositon;
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private int NUM_PAGES;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private PagerAdapter mPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_viewer);

        Intent intent = getIntent();
//        mCurrentNoteUri=intent.getData();
        Bundle extra = intent.getExtras();
        if (extra != null) {
            NUM_PAGES = extra.getInt("ImageCount");
            mCurrentImagePositon = extra.getInt("CurrentPosition");
            mPicPathArrayString = extra.getString("PhotoPath");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            mNoteImagePath = gson.fromJson(mPicPathArrayString, type);
            Log.d(LOG_TAG, "mPicPathArrayString= " + mPicPathArrayString);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the PagerAdapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentImagePositon);

    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picture_viewer, menu);
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
        if (id == android.R.id.home) {
//            NavUtils.navigateUpFromSameTask(PictureViewerActivity.this);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ScreenSlidePageFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_IMAGE_PATH = "image_path";

        public ScreenSlidePageFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
//        public static ScreenSlidePageFragment newInstance(int sectionNumber) {
//            ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
//            Bundle args = new Bundle();
//            final String fileUriPrefix = "file://";
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_picture_viewer, container, false);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.note_picture_viewer);
            final String fileUriPrefix = "file://";
            String mCurrentPicPath = getArguments().getString(ARG_IMAGE_PATH);
            WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            // Get the dimensions of the View
            int targetW;
            int targetH;
            targetW = point.x * 5 / 5;
            targetH = point.y * 4 / 5;
            Transformation transformation = new CropTransformation();
            Picasso.with(getContext()).load(fileUriPrefix + mCurrentPicPath).transform(new CropTransformation()).resize(targetW, targetH).into(imageView);
//            ArrayList<String> imagePathArray=getArguments().getStringArrayList(ARG_IMAGE_PATH);
            return rootView;
        }

        public class CropTransformation implements Transformation {
            @Override
            public Bitmap transform(Bitmap source) {
                WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                // Get the dimensions of the View
                int targetW = point.x * 5 / 5;
                int targetH = point.y * 4 / 5;
                Bitmap result = Bitmap.createScaledBitmap(source, targetW, targetH, true);
                if (result != source) {
                    source.recycle();
                }
                return result;
            }

            @Override
            public String key() {
                return "square()";
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment = new ScreenSlidePageFragment();
            Bundle args = new Bundle();
            ArrayList<String> imagePathArray = mNoteImagePath;
            args.putString(ScreenSlidePageFragment.ARG_IMAGE_PATH, imagePathArray.get(position));
//            args.putStringArrayList(ScreenSlidePageFragment.ARG_IMAGE_PATH,mNoteImagePath);
            fragment.setArguments(args);
            return fragment;
//            return ScreenSlidePageFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "SECTION 1";
//                case 1:
//                    return "SECTION 2";
//                case 2:
//                    return "SECTION 3";
//            }
//            return null;
//        }
    }


}
