package net.mindlevel;

// TODO: Change back to non-support lib
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.mindlevel.api.AccomplishmentController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.dummy.DummyContent;
import net.mindlevel.model.Accomplishment;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FeedFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int columnCount = 2;
    private OnListFragmentInteractionListener listener;
    private AccomplishmentController controller;
    private RecyclerView recyclerView;
    private View view, progressView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FeedFragment newInstance(int columnCount) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.controller = new AccomplishmentController(getContext());

        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        progressView = view.findViewById(R.id.progress);
        Context context = getContext();
        showProgress(true);

        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        }
        recyclerView.setAdapter(new FeedRecyclerViewAdapter(DummyContent.ACCOMPLISHMENTS, listener));

        controller.getLatest(getLatestCallback);
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
        void onListFragmentInteraction(Accomplishment accomplishment);
    }

        private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        recyclerView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private ControllerCallback<List<Accomplishment>> getLatestCallback = new ControllerCallback<List<Accomplishment>>() {
        @Override
        public void onPostExecute(Boolean isSuccess, List<Accomplishment> response) {
            showProgress(false);
            if(isSuccess) {
                recyclerView.setAdapter(new FeedRecyclerViewAdapter(response, listener));
            } else {
                // TODO: Show error
            }
        }
    };
}
