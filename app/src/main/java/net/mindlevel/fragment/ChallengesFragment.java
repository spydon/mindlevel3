package net.mindlevel.fragment;

// TODO: Change back to non-support lib
//import android.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.mindlevel.R;
import net.mindlevel.api.ChallengeController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.model.Category;
import net.mindlevel.model.Challenge;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChallengesFragment extends InfoFragment {

    private int columnCount = 1;
    private OnListFragmentInteractionListener listener;
    private ChallengeController controller;
    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView, categoryRecyclerView;
    private ChallengesRecyclerViewAdapter adapter;
    private CategoryRecyclerViewAdapter categoryAdapter;
    private Set<Challenge> challenges;
    private List<Category> categories;

    public ChallengesFragment() {
        this.challenges = new LinkedHashSet<>();
        this.categories = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.controller = new ChallengeController(getContext());
        this.adapter = new ChallengesRecyclerViewAdapter(challenges, listener);
        this.categoryAdapter = new CategoryRecyclerViewAdapter(getActivity(), categories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_list, container, false);
        this.recyclerView = view.findViewById(R.id.list);
        this.categoryRecyclerView = view.findViewById(R.id.category_list);
        this.contentView = recyclerView;
        this.shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        this.progressView = view.findViewById(R.id.progress);
        this.errorView = view.findViewById(R.id.error);
        this.swipe = view.findViewById(R.id.swipe_refresh_layout);
        Context context = getContext();

        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }

        recyclerView.setAdapter(adapter);
        categoryRecyclerView.setAdapter(categoryAdapter);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populate();
            }
        });

        showInfo(false, true);
        populate();
        return view;
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

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void populate() {
        controller.getCategories(getCategoriesCallback);
        controller.getAll(getAllCallback);
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

    private ControllerCallback<List<Category>> getCategoriesCallback = new ControllerCallback<List<Category>>() {
        @Override
        public void onPostExecute(Boolean isSuccess, List<Category> response) {
            if (isSuccess) {
                 if (response.isEmpty()) {
                     // TODO: Hide category section
                     //showInfo(true, false, getString(R.string.error_not_found));
                 } else {
                     if (!categories.containsAll(response)) {
                         categories.clear();
                         categories.addAll(response);
                         categoryAdapter.notifyDataSetChanged();
                     }
                 }
            } else {
                // TODO: Hide category section
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
                     if (!challenges.containsAll(response)) {
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
