package net.mindlevel;

// TODO: Change back to non-support lib
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentManager;
//import android.app.Fragment;
//import android.support.v13.app.FragmentStatePagerAdapter;
//import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Mission;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.NetworkUtil;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class MainActivity
        extends AppCompatActivity
        implements MissionsFragment.OnListFragmentInteractionListener,
        FeedFragment.OnListFragmentInteractionListener,
        UserFragment.OnFragmentInteractionListener {

    private final UserFragment userFragment = new UserFragment();
    private final MissionsFragment missionsFragment = new MissionsFragment();
    private final FeedFragment feedFragment = new FeedFragment();
    private final LinkedHashMap<Integer, Fragment> fragments = new LinkedHashMap<>();

    private Fragment currentFragment;
    private BottomNavigationView navigation;
    private ViewPager viewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = fragments.get(item.getItemId());
            if(selectedFragment != currentFragment) {
                int fragmentOrderId = Arrays.asList(fragments.values().toArray()).indexOf(selectedFragment);
                viewPager.setCurrentItem(fragmentOrderId, true);
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
        // TODO: Check how to start non programmatically, R.id.Mission... etc
        Intent accomplishmentIntent = new Intent(this, AccomplishmentActivity.class);
        accomplishmentIntent.putExtra("accomplishment", accomplishment);
        startActivity(accomplishmentIntent);
    }

    public void onListFragmentInteraction(Mission mission) {
        // TODO: Check how to start non programmatically, R.id.Mission... etc
        Intent missionIntent = new Intent(this, MissionActivity.class);
        missionIntent.putExtra("mission", mission);
        startActivity(missionIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageUtil.setBucketAddress(getString(R.string.bucket_address));

        fragments.put(R.id.navigation_feed, feedFragment);
        fragments.put(R.id.navigation_missions, missionsFragment);
        fragments.put(R.id.navigation_profile, userFragment);

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("sessionId", "").isEmpty() || !NetworkUtil.isConnected(this)) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            //startActivity(loginIntent); //TODO: Enable again
        }

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        // TODO: Remove support lib once not on S5
        //PagerAdapter pager = new PagerAdapter(getFragmentManager());
        PagerAdapter pager = new PagerAdapter(getSupportFragmentManager());
        // Bottom navigation listener
        navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        // Swipe between fragments
        viewPager = (ViewPager) findViewById(R.id.content_frame);
        viewPager.setAdapter(pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Do nothing
            }

            @Override
            public void onPageSelected(int position) {
                // TODO: There must be something built-in for this.
                // Currently unchecks all menu items and then checks the correct one
                Menu menu = navigation.getMenu();
                for(int i = 0; i < menu.size(); i++) {
                    menu.getItem(i).setChecked(false);
                }
                MenuItem selectedItem = menu.getItem(position);
                selectedItem.setChecked(true);
                navigationItemSelectedListener.onNavigationItemSelected(selectedItem);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Do nothing
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.about_menu:
	            Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
