package net.mindlevel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import net.mindlevel.R;
import net.mindlevel.api.ChallengeController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChallengeTreeActivity extends InfoActivity {

    private List<Challenge> challenges;
    private SwipeRefreshLayout swipe;
    private ChallengeTreeRecyclerViewAdapter adapter;
    private ChallengeController controller;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_challenge_tree);
        challenges = new ArrayList <>();
        User user = (User) getIntent().getSerializableExtra("user");

        this.contentView = findViewById(R.id.list);
        this.progressView = findViewById(R.id.progress);
        this.errorView = findViewById(R.id.error);
        this.swipe = findViewById(R.id.swipe_refresh_layout);
        this.adapter = new ChallengeTreeRecyclerViewAdapter(challenges, user, this);
        this.controller = new ChallengeController(this);

        ((RecyclerView) contentView).setLayoutManager(new LinearLayoutManager(getBaseContext()));
        ((RecyclerView) contentView).setAdapter(adapter);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populate();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        showInfo(false, true);
        populate();
    }

    public void onListFragmentInteraction(Challenge challenge) {
        Intent challengeIntent = new Intent(this, ChallengeActivity.class);
        challengeIntent.putExtra("challenge", challenge);
        startActivity(challengeIntent);
    }

    private void populate() {
        controller.getAllRestricted(getAllCallback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ControllerCallback<List<Challenge>> getAllCallback = new ControllerCallback<List<Challenge>>() {
        @Override
        public void onPostExecute(Boolean isSuccess, List<Challenge> response) {
            swipe.setRefreshing(false);
            if (isSuccess) {
                 if (response.isEmpty()) {
                     showInfo(true, false, getString(R.string.error_not_found));
                 } else {
                     showInfo(false, false);
                     if (!challenges.containsAll(response) || !response.containsAll(challenges)) {
                         challenges.clear();
                         challenges.addAll(response);
                         adapter.notifyDataSetChanged();
                     }
                 }
            } else {
                showInfo(true, false);
            }
        }
    };
}
