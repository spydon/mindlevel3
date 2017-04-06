package net.mindlevel;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.mindlevel.dummy.DummyContent;

public class MainActivity
        extends AppCompatActivity
        implements MissionsFragment.OnListFragmentInteractionListener,
        FeedFragment.OnListFragmentInteractionListener,
        UserFragment.OnFragmentInteractionListener {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        private FragmentManager fragmentManager = getFragmentManager();

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            UserFragment userFragment = new UserFragment();
            MissionsFragment missionsFragment = new MissionsFragment();
            FeedFragment feedFragment = new FeedFragment();

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_feed:
                    transaction.replace(R.id.content_frame, feedFragment).commit();
                    return true;
                case R.id.navigation_missions:
                    transaction.replace(R.id.content_frame, missionsFragment).commit();
                    return true;
                case R.id.navigation_profile:
                    transaction.replace(R.id.content_frame, userFragment).commit();
                    return true;
            }
            return false;
        }

    };

    public void onFragmentInteraction(Uri uri) {
        System.out.println("Uri");
    }

    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        System.out.println("What");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
