package net.mindlevel.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.mindlevel.R;
import net.mindlevel.activity.ChallengeSuggestionActivity;
import net.mindlevel.api.ChallengeController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.UserController;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.User;
import net.mindlevel.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class ChallengeTreeFragment extends InfoFragment {

    private List<Challenge> challenges;
    private SwipeRefreshLayout swipe;
    private ChallengeTreeRecyclerViewAdapter adapter;
    private ChallengeController controller;
    private UserController userController;
    private OnListFragmentInteractionListener listener;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        challenges = new ArrayList <>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        final Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_challenge_tree, container, false);

        this.infoView = view.findViewById(R.id.info_center);
        this.contentView = view.findViewById(R.id.list);
        this.progressView = view.findViewById(R.id.progress);
        this.errorView = view.findViewById(R.id.error);
        this.swipe = view.findViewById(R.id.swipe_refresh_layout);
        this.controller = new ChallengeController(getContext());
        this.userController = new UserController(getContext());
        this.adapter = new ChallengeTreeRecyclerViewAdapter(challenges, listener);

        ((RecyclerView) contentView).setLayoutManager(new LinearLayoutManager(context));
        ((RecyclerView) contentView).setAdapter(adapter);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populate();
            }
        });

        FloatingActionButton suggestionButton = view.findViewById(R.id.challenge_suggestion_button);
        suggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent suggestionIntent = new Intent(context, ChallengeSuggestionActivity.class);
                startActivity(suggestionIntent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        showInfo(false, true);
        populate();
    }

    @Override
    public void setUserVisibleHint(boolean isVisible) {
        if (isVisible && challenges.isEmpty()) {
            populate();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            listener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement all InteractionListeners");
        }
    }
    /**
     * These interfaces must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Challenge challenge);
    }

    private void populate() {
        userController.getUser(PreferencesUtil.getUsername(getContext()), userCallback);
    }

    private ControllerCallback<User> userCallback = new ControllerCallback<User>() {
        @Override
        public void onPostExecute(final Boolean success, final User user) {
            if (success) {
                adapter.setUser(user);
                controller.getAllRestricted(getAllCallback);
            } else {
                showInfo(true, false);
            }
        }
    };

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
