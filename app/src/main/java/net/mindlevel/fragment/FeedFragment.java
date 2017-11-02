package net.mindlevel.fragment;

// TODO: Change back to non-support lib
//import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.mindlevel.R;
import net.mindlevel.api.AccomplishmentController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.ChallengeController;
import net.mindlevel.api.UserController;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Challenge;
import net.mindlevel.impl.Glassbar;
import net.mindlevel.util.NetworkUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A fragment representing a list of accomplishments.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FeedFragment extends InfoFragment {

    private int columnCount = 2;
    private View coordinator;
    private OnListFragmentInteractionListener listener;
    private AccomplishmentController accomplishmentController;
    private UserController userController;
    private ChallengeController ChallengeController;
    private Set<Accomplishment> accomplishments;
    private RecyclerView recyclerView;
    private FeedRecyclerViewAdapter adapter;
    private Snackbar searchInfoBar;
    private View paginationProgress;
    private SwipeRefreshLayout swipe;
    private int page = 0;
    private boolean isFirstRun;

    public FeedFragment() {
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.accomplishmentController = new AccomplishmentController(getContext());
        this.userController = new UserController(getContext());
        this.ChallengeController = new ChallengeController(getContext());
        this.shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        this.accomplishments = new HashSet<>();
        this.adapter = new FeedRecyclerViewAdapter(accomplishments, listener);
        this.isFirstRun = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_list, container, false);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        this.contentView = recyclerView;
        this.progressView = view.findViewById(R.id.progress);
        this.errorView = view.findViewById(R.id.error);
        this.paginationProgress = view.findViewById(R.id.progress_pagination);
        this.swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        Context context = getContext();

        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        }

        this.coordinator = contentView.getRootView();
        this.searchInfoBar = Glassbar.make(coordinator, "", Snackbar.LENGTH_INDEFINITE);
        searchInfoBar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar));
        String latest = getString(R.string.latest);
        searchInfoBar.setAction(latest, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInfoBar.dismiss();
                populateLatest();
            }
        });

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.isShown()) {
                    return;
                }

                if (!recyclerView.canScrollVertically(-1)) {
                    // Already handled by SwipeRefreshLayout
                } else if (!recyclerView.canScrollVertically(1)) {
                    populatePage(page++);
                } else if (dy < 0) {
                    System.out.println("dy");
                } else if (dy > 0) {
                    System.out.println("dy2");
                }
            }
        });

        if (isFirstRun) {
            isFirstRun = false;
            populate();
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisible) {
        super.setUserVisibleHint(isVisible);
        if (isVisible && isAdded()) {
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
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void populate() {
        if (NetworkUtil.connectionCheck(getContext(), coordinator)) {
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                if (bundle.containsKey("accomplishments_for_user")) {
                    String username = bundle.getString("accomplishments_for_user");
                    populateUserAccomplishments(username);
                } else if (bundle.containsKey("accomplishments_for_challenge")) {
                    Challenge challenge = (Challenge) bundle.getSerializable("accomplishments_for_challenge");
                    populateChallengeAccomplishments(challenge);
                } else {
                    populateLatest();
                }
            } else {
                populateLatest();
            }
        } else {
            showInfo(true, false);
        }
    }

    private void refresh() {
        populate();
    }

    private void populateLatest() {
        showInfo(false, true);
        getArguments().clear();
        accomplishmentController.getLatest(getAccomplishmentsCallback);
    }

    private void populatePage(int page) {
        showPaginationProgress(true);
        String range = page*AccomplishmentController.PAGE_SIZE + "-" + ((page+1)*AccomplishmentController.PAGE_SIZE);
        accomplishmentController.getLatest(range, getPaginationCallback);
    }

    private void populateUserAccomplishments(String username) {
        showInfo(false, true);
        userController.getAccomplishments(username, getAccomplishmentsCallback);
        String infoText = getString(R.string.feed_user, username);
        searchInfoBar.setText(infoText);
        searchInfoBar.show();
    }

    private void populateChallengeAccomplishments(Challenge Challenge) {
        showInfo(false, true);
        ChallengeController.getAccomplishments(Challenge.id, getAccomplishmentsCallback);
        String infoText = getString(R.string.feed_challenge, Challenge.title);
        searchInfoBar.setText(infoText);
        searchInfoBar.show();
    }

    private void showPaginationProgress(boolean isPagination) {
        paginationProgress.setVisibility(isPagination ? VISIBLE : GONE);
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
            swipe.setRefreshing(false);
            if (getActivity() != null) {
                if (isSuccess) {
                    if (response.isEmpty()) {
                        showInfo(true, false, getString(R.string.error_not_found));
                    } else {
                        showInfo(false, false);
                        if (!accomplishments.containsAll(response)) {
                            accomplishments.addAll(response);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    showInfo(true, false);
                }
            }
        }
    };

    private ControllerCallback<List<Accomplishment>> getPaginationCallback = new
            ControllerCallback<List<Accomplishment>>() {
        @Override
        public void onPostExecute(Boolean isSuccess, List<Accomplishment> response) {
            showPaginationProgress(false);
            if (getActivity() != null) {
                if (isSuccess) {
                    if (response.isEmpty()) {
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
