package net.mindlevel.fragment;

// TODO: Change back to non-support lib

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
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.UserController;
import net.mindlevel.model.User;
import net.mindlevel.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

//import android.app.Fragment;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HighscoreFragment extends InfoFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int columnCount = 1;
    private OnListFragmentInteractionListener listener;
    private UserController controller;
    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;
    private HighscoreRecyclerViewAdapter adapter;
    private List<User> highscores;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HighscoreFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HighscoreFragment newInstance(int columnCount) {
        HighscoreFragment fragment = new HighscoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.controller = new UserController(getContext());
        this.shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        this.highscores = new ArrayList<>();
        this.adapter = new HighscoreRecyclerViewAdapter(highscores, listener);

        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_highscore_list, container, false);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        contentView = recyclerView;
        progressView = view.findViewById(R.id.progress);
        errorView = view.findViewById(R.id.error);
        this.swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        Context context = getContext();
        View coordinator = contentView.getRootView();

        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                controller.getHighscore(getHighscoreCallback);
            }
        });

        if (NetworkUtil.connectionCheck(getContext(), coordinator)) {
            showInfo(false, true);
            controller.getHighscore(getHighscoreCallback);
        } else {
            showInfo(true, false);
        }

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(User user);
    }

    private ControllerCallback<List<User>> getHighscoreCallback = new ControllerCallback<List<User>>() {
        @Override
        public void onPostExecute(Boolean isSuccess, List<User> response) {
            swipe.setRefreshing(false);
            if(isSuccess) {
                showInfo(false, false);
                highscores.clear();
                highscores.addAll(response);
                adapter.notifyDataSetChanged();
            } else {
                showInfo(true, false);
            }
        }
    };
}
