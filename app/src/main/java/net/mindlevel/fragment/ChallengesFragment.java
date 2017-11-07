package net.mindlevel.fragment;

// TODO: Change back to non-support lib
//import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.mindlevel.R;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.ChallengeController;
import net.mindlevel.model.Challenge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChallengesFragment extends InfoFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int columnCount = 1;
    private OnListFragmentInteractionListener listener;
    private ChallengeController controller;
    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;
    private ChallengesRecyclerViewAdapter adapter;
    private Set<Challenge> Challenges;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChallengesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChallengesFragment newInstance(int columnCount) {
        ChallengesFragment fragment = new ChallengesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.controller = new ChallengeController(getContext());
        this.Challenges = new HashSet<>();
        this.adapter = new ChallengesRecyclerViewAdapter(Challenges, listener);

        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_list, container, false);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.list);
        this.contentView = recyclerView;
        this.shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        this.progressView = view.findViewById(R.id.progress);
        this.errorView = view.findViewById(R.id.error);
        this.swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        Context context = getContext();

        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }

        recyclerView.setAdapter(adapter);
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
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void populate() {
        controller.getAll(getAllCallback);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Challenge Challenge);
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
                     if (!Challenges.containsAll(response)) {
                         Challenges.addAll(response);
                         adapter.notifyDataSetChanged();
                     }
                 }
            } else {
                showInfo(true, false);
            }
        }
    };
}
