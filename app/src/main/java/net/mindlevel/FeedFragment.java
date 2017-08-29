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
import net.mindlevel.model.Accomplishment;
import net.mindlevel.util.NetworkUtil;

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
    private int shortAnimTime;
    private OnListFragmentInteractionListener listener;
    private AccomplishmentController controller;
    private RecyclerView recyclerView;
    private View view, progressView, errorView;

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
        this.shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
        errorView = view.findViewById(R.id.error);
        Context context = getContext();

        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        }

        if (NetworkUtil.connectionCheck(getContext(), getView())) {
            showInfo(false, true);
            controller.getLatest(getLatestCallback);
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
        void onListFragmentInteraction(Accomplishment accomplishment);
    }

    private void showInfo(final boolean isError, final boolean isProgress) {
        final boolean isNormal = !isError && !isProgress;

        recyclerView.setVisibility(isNormal ? View.VISIBLE : View.GONE);
        recyclerView.animate().setDuration(shortAnimTime).alpha(
                isNormal ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                recyclerView.setVisibility(isNormal ? View.VISIBLE : View.GONE);
            }
        });

        errorView.setVisibility(isError ? View.VISIBLE : View.GONE);
        errorView.animate().setDuration(shortAnimTime).alpha(
                isError ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(isError ? View.VISIBLE : View.GONE);
            }
        });

        progressView.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                isProgress ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(isProgress ? View.VISIBLE : View.GONE);
            }
        });
    }

    private ControllerCallback<List<Accomplishment>> getLatestCallback = new ControllerCallback<List<Accomplishment>>() {
        @Override
        public void onPostExecute(Boolean isSuccess, List<Accomplishment> response) {
            if(getActivity() != null) {
                if (isSuccess) {
                    showInfo(false, false);
                    recyclerView.setAdapter(new FeedRecyclerViewAdapter(response, listener));
                } else {
                    showInfo(true, false);
                }
            }
        }
    };
}
