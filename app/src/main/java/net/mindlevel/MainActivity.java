package net.mindlevel;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Mission;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class MainActivity
        extends AppCompatActivity
        implements MissionsFragment.OnListFragmentInteractionListener,
        FeedFragment.OnListFragmentInteractionListener,
        UserFragment.OnFragmentInteractionListener {

    private static final UserFragment userFragment = new UserFragment();
    private static final MissionsFragment missionsFragment = new MissionsFragment();
    private static final FeedFragment feedFragment = new FeedFragment();
    private static final LinkedHashMap<Integer, Fragment> fragments = new LinkedHashMap<>();
    static
    {
        fragments.put(R.id.navigation_feed, feedFragment);
        fragments.put(R.id.navigation_missions, missionsFragment);
        fragments.put(R.id.navigation_profile, userFragment);
    }
    private Fragment currentFragment;
    private BottomNavigationView navigation;
    private ViewPager mViewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = fragments.get(item.getItemId());
            if(selectedFragment != currentFragment) {
                //int fragmentOrderId = Arrays.asList(fragments.keySet().toArray()).indexOf(item.getItemId());
                int fragmentOrderId = Arrays.asList(fragments.values().toArray()).indexOf(selectedFragment);
                // Has to check whether it comes from swiping or bar, if it comes from the bar, do the change
                mViewPager.setCurrentItem(fragmentOrderId, true);
                currentFragment = selectedFragment;
            }
            return true;
        }

    };

    // Used for handling the swiping between fragments
    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //@Override
        public Fragment getItem(int i) {
            // i is incremental from the left
            Fragment selectedFragment = (Fragment) fragments.values().toArray()[i];
            //if(selectedFragment != currentFragment) {
            //    MenuItem item = navigation.getMenu().getItem(i);
            //    mOnNavigationItemSelectedListener.onNavigationItemSelected(item);
            //}
            return selectedFragment;
        }

        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    public void onFragmentInteraction(Uri uri) {
        System.out.println("Uri");
    }

    public void onListFragmentInteraction(Accomplishment accomplishment) {
        System.out.println("Accomplishment");
    }

    public void onListFragmentInteraction(Mission mission) {
        System.out.println("What");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        PagerAdapter pager = new PagerAdapter(getFragmentManager());
        //currentFragment = userFragment;
        // Bottom navigation listener
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // Swipe between fragments
        mViewPager = (ViewPager) findViewById(R.id.content_frame);
        mViewPager.setAdapter(pager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MenuItem item = navigation.getMenu().getItem(position);
                mOnNavigationItemSelectedListener.onNavigationItemSelected(item);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
