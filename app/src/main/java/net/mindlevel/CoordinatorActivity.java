package net.mindlevel;

// TODO: Change back to non-support lib
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.mindlevel.activity.AboutActivity;
import net.mindlevel.activity.AccomplishmentActivity;
import net.mindlevel.activity.LoginActivity;
import net.mindlevel.activity.ChallengeActivity;
import net.mindlevel.activity.TutorialActivity;
import net.mindlevel.api.LoginController;
import net.mindlevel.fragment.FeedFragment;
import net.mindlevel.fragment.HighscoreFragment;
import net.mindlevel.fragment.ChallengesFragment;
import net.mindlevel.fragment.UserFragment;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Login;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.User;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.PreferencesUtil;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;

public class CoordinatorActivity
        extends AppCompatActivity
        implements ChallengesFragment.OnListFragmentInteractionListener,
        FeedFragment.OnListFragmentInteractionListener,
        HighscoreFragment.OnListFragmentInteractionListener,
        UserFragment.OnFragmentInteractionListener {

    private final FeedFragment feedFragment = new FeedFragment();
    private final ChallengesFragment challengesFragment = new ChallengesFragment();
    private final HighscoreFragment highscoreFragment = new HighscoreFragment();
    private final UserFragment userFragment = new UserFragment();
    private final LinkedHashMap<Integer, Fragment> fragments = new LinkedHashMap<>();
    private final Deque<Fragment> fragmentHistory = new ArrayDeque<>();

    private Fragment currentFragment;
    private BottomNavigationView navigation;
    private ViewPager viewPager;

    // TODO: Refactor this
    private boolean isBack = false;

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = fragments.get(item.getItemId());
            if (selectedFragment != currentFragment) {
                if (!isBack) {
                    fragmentHistory.push(currentFragment);
                }
                isBack = false;
                scrollToFragment(selectedFragment);
            }
            return true;
        }

    };

    // Used for handling the swiping between fragments
    private class PagerAdapter extends FragmentStatePagerAdapter {
        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int i) {
            // i is incremental from the left, the selected fragment
            return (Fragment) fragments.values().toArray()[i];
        }

        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator);
        ImageUtil.setBucketAddress(getString(R.string.bucket_address));

        fragments.put(R.id.navigation_feed, feedFragment);
        fragments.put(R.id.navigation_challenges, challengesFragment);
        fragments.put(R.id.navigation_highscore, highscoreFragment);
        fragments.put(R.id.navigation_profile, userFragment);

        currentFragment = feedFragment;

       if (PreferencesUtil.getSessionId(getApplicationContext()).isEmpty()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }

        PreferencesUtil.setTutorialSeen(getApplicationContext(), false);
        if (!PreferencesUtil.getTutorialSeen(getApplicationContext())) {
            Intent tutorialIntent = new Intent(this, TutorialActivity.class);
            startActivity(tutorialIntent);
            PreferencesUtil.setTutorialSeen(getApplicationContext(), true);
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar);

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
            public void onPageScrolled(int position, float offset, int offsetPixels) { /* Do nothing */ }

            @Override
            public void onPageSelected(int position) {
                Menu menu = navigation.getMenu();
                MenuItem selectedItem = menu.getItem(position);
                findViewById(selectedItem.getItemId()).callOnClick();
            }

            @Override
            public void onPageScrollStateChanged(int state) { /* Do nothing */ }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("username")) {
            String username = intent.getStringExtra("username");
            getCleanFragmentBundle(userFragment).putString("username", username);
            scrollToFragment(userFragment);
        } else if (intent.hasExtra("user")) {
            User user = (User) intent.getSerializableExtra("user");
            getCleanFragmentBundle(userFragment).putSerializable("user", user);
            scrollToFragment(userFragment);
        } else if (intent.hasExtra("accomplishments_for_user")) {
            String username = intent.getStringExtra("accomplishments_for_user");
            getCleanFragmentBundle(feedFragment).putString("accomplishments_for_user", username);
            scrollToFragment(feedFragment);
        } else if (intent.hasExtra("accomplishments_for_challenge")) {
            Challenge challenge = (Challenge) intent.getSerializableExtra("accomplishments_for_challenge");
            getCleanFragmentBundle(feedFragment).putSerializable("accomplishments_for_challenge", challenge);
            scrollToFragment(feedFragment);
        }
    }

    private Bundle getCleanFragmentBundle(Fragment fragment) {
        Bundle bundle = fragment.getArguments() != null ? fragment.getArguments() : new Bundle();
        bundle.clear();
        return bundle;
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
            case R.id.tutorial_menu:
                Intent tutorialIntent = new Intent(this, TutorialActivity.class);
                startActivity(tutorialIntent);
                return true;
            case R.id.sign_out_menu:
                PreferencesUtil.clearSession(this);
                Login login = PreferencesUtil.getLogin(this);
                new LoginController(this).logout(login, null);
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        fm.executePendingTransactions();
        int count = fm.getBackStackEntryCount();

        if (count == 0 && !fragmentHistory.isEmpty()) {
            isBack = true;
            scrollToFragment(fragmentHistory.pop());
        } else if (count == 0) {
            super.onBackPressed();
        } else {
            fm.popBackStackImmediate();
        }
    }

    public void onFragmentInteraction(Uri uri) {
        System.out.println("Uri");
    }

    public void onListFragmentInteraction(Accomplishment accomplishment) {
        Intent accomplishmentIntent = new Intent(this, AccomplishmentActivity.class);
        accomplishmentIntent.putExtra("accomplishment", accomplishment);
        startActivity(accomplishmentIntent);
    }

    public void onListFragmentInteraction(Challenge challenge) {
        Intent challengeIntent = new Intent(this, ChallengeActivity.class);
        challengeIntent.putExtra("challenge", challenge);
        startActivity(challengeIntent);
    }

    public void onListFragmentInteraction(User user) {
        getCleanFragmentBundle(userFragment).putSerializable("user", user);
        scrollToFragment(userFragment);
    }

    private void scrollToFragment(Fragment selectedFragment) {
        int fragmentOrderId = Arrays.asList(fragments.values().toArray()).indexOf(selectedFragment);
        viewPager.setCurrentItem(fragmentOrderId, true);
        currentFragment = selectedFragment;
    }
}
