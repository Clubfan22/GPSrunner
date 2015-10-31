package me.diskstation.ammon.gpsrunner.ui;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.ArrayList;

import me.diskstation.ammon.gpsrunner.R;
import me.diskstation.ammon.gpsrunner.db.LocationDBHelper;
import me.diskstation.ammon.gpsrunner.db.LocationDBReader;
import me.diskstation.ammon.gpsrunner.db.Run;
import me.diskstation.ammon.gpsrunner.db.Segment;
import me.diskstation.ammon.gpsrunner.db.Waypoint;

public class DetailsTabActivity extends AppCompatActivity {

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
    private TabLayout mTabLayout;
    private AppBarLayout appBarLayout;

    private LocationDBHelper dbHelp;
    private LocationDBReader dbRead;

    private DetailsMapFragment mapFragment;
    private LineDetailsFragment lineFragment;
    private RunDetailsFragment detailsFragment;
    protected long[] runIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runIds = getIntent().getLongArrayExtra("run_ids");
        setContentView(R.layout.activity_details_tab);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                turnOffTabLayoutScrolling();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        dbHelp = new LocationDBHelper(this);
        dbRead = new LocationDBReader(dbHelp);
    }

    protected ArrayList<Waypoint> getWaypoints(long id){
        return dbRead.getWaypoints(id);
    }

    protected ArrayList<Segment> getSegments(long id){
        return dbRead.getSegments(id);
    }

    public ArrayList<Run> getRuns(long[] runIds) {
        ArrayList<Run> runs = new ArrayList<>();
        for (int i = 0; i < runIds.length; i++){
            runs.add(dbRead.getRun(runIds[i]));
        }
        return runs;
    }

    protected void turnOffTabLayoutScrolling() {
        //turn off scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mTabLayout.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(null);
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    protected void turnOnTabLayoutScrolling() {
        //turn on scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mTabLayout.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        mTabLayout.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_details_tab, menu);
        return true;
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
            switch (position){
                case 0:
                    if (mapFragment == null) {
                        mapFragment = DetailsMapFragment.newInstance(runIds);
                    }
                    return mapFragment;

                case 1:
                    if (lineFragment == null){
                        lineFragment = LineDetailsFragment.newInstance(runIds);
                    }
                    return lineFragment;
                case 2:
                    if (detailsFragment == null){
                        detailsFragment = RunDetailsFragment.newInstance(runIds);
                    }
                    return detailsFragment;
            }
            return null;
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
                    return getString(R.string.Map);
                case 1:
                    return getString(R.string.Charts);
                case 2:
                    return getString(R.string.Details);
            }
            return null;
        }
    }
}
