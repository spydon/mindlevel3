package net.mindlevel;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Mission;

public class MainActivity
        extends AppCompatActivity
        implements MissionsFragment.OnListFragmentInteractionListener,
        FeedFragment.OnListFragmentInteractionListener,
        UserFragment.OnFragmentInteractionListener {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        private FragmentManager fragmentManager = getFragmentManager();

        private void replaceFragment(Fragment fragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.replace(R.id.content_frame, fragment).commit();
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            UserFragment userFragment = new UserFragment();
            MissionsFragment missionsFragment = new MissionsFragment();
            FeedFragment feedFragment = new FeedFragment();

            switch (item.getItemId()) {
                case R.id.navigation_feed:
                    replaceFragment(feedFragment);
                    return true;
                case R.id.navigation_missions:
                    replaceFragment(missionsFragment);
                    return true;
                case R.id.navigation_profile:
                    replaceFragment(userFragment);
                    return true;
            }
            return false;
        }

    };

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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mOnNavigationItemSelectedListener.onNavigationItemSelected(navigation.getMenu().getItem(0));
    }

}
