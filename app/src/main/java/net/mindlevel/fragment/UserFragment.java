package net.mindlevel.fragment;

// TODO: Change back to non-support lib
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.activity.EditUserActivity;
import net.mindlevel.activity.LoginActivity;
import net.mindlevel.util.CoordinatorUtil;
import net.mindlevel.util.PreferencesUtil;
import net.mindlevel.impl.ProgressController;
import net.mindlevel.R;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.LoginController;
import net.mindlevel.api.UserController;
import net.mindlevel.model.Login;
import net.mindlevel.model.User;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.NetworkUtil;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class UserFragment extends InfoFragment {

    private UserController controller;
    private LoginController loginController;

    private View coordinator;
    private ImageView imageView;
    private TextView usernameView;
    private TextView scoreView;
    private TextView descriptionView;
    private View view, imageProgressBar;
    private FloatingActionButton editButton, signOutButton, selfButton, accomplishmentButton;
    private Context context;
    private User user;

    private final static int UPDATE_USER = 1;

    public UserFragment() {
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_user, container, false);
        this.contentView = view.findViewById(R.id.content);

        this.imageView = (ImageView) view.findViewById(R.id.image);
        this.usernameView = (TextView) view.findViewById(R.id.username);
        this.scoreView = (TextView) view.findViewById(R.id.score);
        this.descriptionView = (TextView) view.findViewById(R.id.description);
        this.progressView = view.findViewById(R.id.progress);
        this.errorView = view.findViewById(R.id.error);
        this.imageProgressBar = view.findViewById(R.id.progress_image);
        this.context = getContext();

        this.controller = new UserController(context);
        this.loginController = new LoginController(context);

        this.shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        editButton = (FloatingActionButton) view.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(context, EditUserActivity.class);
                editIntent.putExtra("user", user);
                startActivityForResult(editIntent, UPDATE_USER);
            }
        });

        signOutButton = (FloatingActionButton) view.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutButton.setActivated(false);
                Login login = PreferencesUtil.getLogin(context);
                loginController.logout(login, signOutCallback);
            }
        });

        selfButton = (FloatingActionButton) view.findViewById(R.id.self_button);
        selfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateWithSelf();
            }
        });

        accomplishmentButton = (FloatingActionButton) view.findViewById(R.id.accomplishments_button);
        accomplishmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CoordinatorUtil.toFeed(context, user.username);
            }
        });

        if (!NetworkUtil.isConnected(context)) {
            buttonVisibility(false, false);
        }
        coordinator = contentView.getRootView();

        populate();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisible) {
        super.setUserVisibleHint(isVisible);
        if (isVisible && isAdded()) {
            populate();
        }
    }

    private void populate() {
        NetworkUtil.connectionCheck(getContext(), coordinator);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("user")) {
                User user = (User) bundle.getSerializable("user");
                populate(user);
            } else if (bundle.containsKey("username")) {
                String username = bundle.getString("username");
                populate(username);
            } else {
                populateWithSelf();
            }
        } else {
            populateWithSelf();
        }
    }

    private void populateWithSelf() {
        showInfo(false, true);
        getArguments().clear();
        String username = PreferencesUtil.getUsername(context);
        populate(username);
    }

    private void populate(String username) {
        showInfo(false, true);
        controller.getUser(username, userCallback);
    }

    private void populate(User user) {
        showInfo(false, false);
        if (user.equals(this.user)) {
            return;
        }

        this.user = user;
        if (isAdded()) {
            Glide.with(imageView.getContext()).clear(imageView);
        }

        ProgressController loading = new ProgressController(imageProgressBar);
        if (!TextUtils.isEmpty(user.image)) {
            String url = ImageUtil.getUrl(user.image);
            Glide.with(imageView.getContext())
                    .load(url)
                    .listener(loading)
                    .into(imageView);
        } else {
            loading.hide();
        }

        usernameView.setText(user.username);
        scoreView.setText(String.valueOf(user.score));
        descriptionView.setText(user.description);

        if (PreferencesUtil.getUsername(context).equals(user.username)) {
            if (NetworkUtil.isConnected(context)) {
                buttonVisibility(true, true);
            } else {
                buttonVisibility(false, true);
            }
        } else {
            buttonVisibility(true, false);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and title
        void onFragmentInteraction(Uri uri);
    }

    public void buttonVisibility(boolean isVisible, boolean isSelf) {
        int visibility = isVisible ? VISIBLE : GONE;
        int selfVisibility = !isSelf ? VISIBLE : GONE;
        int selfModVisibilty = isVisible && isSelf ? VISIBLE : GONE;
        editButton.setVisibility(selfModVisibilty);
        signOutButton.setVisibility(selfModVisibilty);
        accomplishmentButton.setVisibility(visibility);
        selfButton.setVisibility(selfVisibility);
    }

    private ControllerCallback<User> userCallback = new ControllerCallback<User>() {

        @Override
        public void onPostExecute(final Boolean success, final User user) {
            if (success) {
                populate(user);
            } else {
                showInfo(true, false);
            }
        }
    };

    private ControllerCallback<Void> signOutCallback = new ControllerCallback<Void>() {

        @Override
        public void onPostExecute(final Boolean success, final Void nothing) {
            PreferencesUtil.clearSession(context);
            Intent loginIntent = new Intent(getContext(), LoginActivity.class);
            startActivity(loginIntent);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UPDATE_USER) {
            populateWithSelf();
        }
    }
}
