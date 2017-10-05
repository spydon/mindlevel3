package net.mindlevel.fragment;

// TODO: Change back to non-support lib
//import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.mindlevel.R;
import net.mindlevel.api.AccomplishmentController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.MissionController;
import net.mindlevel.api.UserController;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Mission;
import net.mindlevel.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FeedFragment extends InfoFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int columnCount = 2;
    private View coordinator;
    private OnListFragmentInteractionListener listener;
    private AccomplishmentController accomplishmentController;
    private UserController userController;
    private MissionController missionController;
    private List<Accomplishment> accomplishments;
    private RecyclerView recyclerView;
    private FeedRecyclerViewAdapter adapter;
    private Snackbar searchInfoBar;

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
        this.accomplishmentController = new AccomplishmentController(getContext());
        this.userController = new UserController(getContext());
        this.missionController = new MissionController(getContext());
        this.shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        this.accomplishments = new ArrayList<>();
        this.adapter = new FeedRecyclerViewAdapter(accomplishments, listener);

        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_list, container, false);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        contentView = recyclerView;
        progressView = view.findViewById(R.id.progress);
        errorView = view.findViewById(R.id.error);
        Context context = getContext();

        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        }

        this.coordinator = contentView.getRootView();
        this.searchInfoBar = Snackbar.make(coordinator, "", Snackbar.LENGTH_INDEFINITE);
        String latest = getString(R.string.latest);
        searchInfoBar.setAction(latest, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInfoBar.dismiss();
                populateLatest();
            }
        });

        if (NetworkUtil.connectionCheck(getContext(), coordinator)) {
            populateLatest();
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

    private void populateLatest() {
        showInfo(false, true);
        accomplishmentController.getLatest(getAccomplishmentsCallback);
    }

    public void populateUserAccomplishments(String username) {
        showInfo(false, true);
        userController.getAccomplishments(username, getAccomplishmentsCallback);
        String infoText = getString(R.string.feed_user, username);
        searchInfoBar.setText(infoText);
        searchInfoBar.show();
    }

    public void populateMissionAccomplishments(Mission mission) {
        showInfo(false, true);
        missionController.getAccomplishments(mission.id, getAccomplishmentsCallback);
        String infoText = getString(R.string.feed_mission, mission.title);
        searchInfoBar.setText(infoText);
        searchInfoBar.show();
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

    private ControllerCallback<List<Accomplishment>> getAccomplishmentsCallback = new ControllerCallback<List<Accomplishment>>() {
        @Override
        public void onPostExecute(Boolean isSuccess, List<Accomplishment> response) {
            if(getActivity() != null) {
                if (isSuccess) {
                    accomplishments.clear();

                    if(response.isEmpty()) {
                        showInfo(true, false, getString(R.string.error_not_found));
                    } else {
                        accomplishments.addAll(response);
                        showInfo(false, false);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    showInfo(true, false);
                }
            }
        }
    };
}
