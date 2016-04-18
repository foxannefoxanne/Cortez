package eecs581_582.cortez.frontend;

import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentTransaction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.Vector;

import eecs581_582.cortez.R;
import eecs581_582.cortez.backend.Constants;

public class MediaSelectActivity extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static ArrayList<String>
            picLinks,
            audLinks,
            vidLinks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_select);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        ListView picListView = new ListView(this);
        ListView audListView = new ListView(this);
        ListView vidListView = new ListView(this);

        Vector<View> pages = new Vector<View>();
        pages.add(picListView);
        pages.add(audListView);
        pages.add(vidListView);

        MediaSelectPagerAdapter mediaSelectPagerAdapter = new MediaSelectPagerAdapter(this, pages);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mediaSelectPagerAdapter);

        // Get the content links for each of the three separate tabs.
        picLinks = getIntent().getStringArrayListExtra("picLinks");
        audLinks = getIntent().getStringArrayListExtra("audLinks");
        vidLinks = getIntent().getStringArrayListExtra("vidLinks");

        picListView.setAdapter(new MediaSelectItemAdapter(this, generateData(picLinks, 1)));
        audListView.setAdapter(new MediaSelectItemAdapter(this, generateData(audLinks, 2)));
        vidListView.setAdapter(new MediaSelectItemAdapter(this, generateData(vidLinks, 3)));

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    private ArrayList<MediaSelectItem> generateData(ArrayList<String> strings, int num){
        ArrayList<MediaSelectItem> mediaSelectItems = new ArrayList<MediaSelectItem>();

        Constants.MediaType mediaType;
        String s = "";
        switch (num) {
            case 1:
                mediaType = Constants.MediaType.IMAGE;
                break;
            case 2:
                mediaType = Constants.MediaType.AUDIO;
                break;
            case 3:
                mediaType = Constants.MediaType.VIDEO;
                break;
            default:
                mediaType = null;
        }

        for (int i = 1; i < strings.size() + 1; i++) {
            mediaSelectItems.add(new MediaSelectItem(mediaType, s + i, ""+i, strings.get(i-1)));
        }

        return mediaSelectItems;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_media_select, menu);
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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
            switch (sectionNumber) {
                case 1:
                    args.putStringArrayList(ARG_SECTION_NUMBER, picLinks);
                    break;
                case 2:
                    args.putStringArrayList(ARG_SECTION_NUMBER, audLinks);
                    break;
                case 3:
                    args.putStringArrayList(ARG_SECTION_NUMBER, vidLinks);
                    break;
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_media_select, container, false);
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "IMAGE";
                case 1:
                    return "AUDIO";
                case 2:
                    return "VIDEO";
            }
            return null;
        }
    }
}