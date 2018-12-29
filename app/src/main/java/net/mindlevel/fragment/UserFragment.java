package net.mindlevel.fragment;

// TODO: Change back to non-support lib

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.R;
import net.mindlevel.activity.ChallengeTreeActivity;
import net.mindlevel.activity.EditUserActivity;
import net.mindlevel.activity.LoginActivity;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.LoginController;
import net.mindlevel.api.UserController;
import net.mindlevel.impl.ProgressController;
import net.mindlevel.model.Level;
import net.mindlevel.model.Login;
import net.mindlevel.model.User;
import net.mindlevel.util.CoordinatorUtil;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.NetworkUtil;
import net.mindlevel.util.PreferencesUtil;

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
    private TextView levelView;
    private TextView descriptionView;
    private View view, imageProgressBar;
    private FloatingActionButton editButton, signOutButton, selfButton, accomplishmentButton, challengeTreeButton;
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

        this.imageView = view.findViewById(R.id.image);
        this.usernameView = view.findViewById(R.id.username);
        this.scoreView = view.findViewById(R.id.score_title);
        this.levelView = view.findViewById(R.id.level_title);
        this.descriptionView = view.findViewById(R.id.description);
        this.progressView = view.findViewById(R.id.progress);
        this.errorView = view.findViewById(R.id.error);
        this.imageProgressBar = view.findViewById(R.id.progress_image);
        this.context = getContext();

        this.controller = new UserController(context);
        this.loginController = new LoginController(context);

        this.shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        editButton = view.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(context, EditUserActivity.class);
                editIntent.putExtra("user", user);
                startActivityForResult(editIntent, UPDATE_USER);
            }
        });

        challengeTreeButton = view.findViewById(R.id.challenge_tree_button);
        challengeTreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent challengeTreeIntent = new Intent(context, ChallengeTreeActivity.class);
                challengeTreeIntent.putExtra("user", user);
                startActivity(challengeTreeIntent);
            }
        });

        signOutButton = view.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutButton.setActivated(false);
                Login login = PreferencesUtil.getLogin(context);
                loginController.logout(login, signOutCallback);
            }
        });

        selfButton = view.findViewById(R.id.self_button);
        selfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateWithSelf();
            }
        });

        accomplishmentButton = view.findViewById(R.id.accomplishments_button);
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        populate();
    }

    private void populate() {
        NetworkUtil.connectionCheck(getContext(), coordinator);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("user")) {
                User user = (User) bundle.getSerializable("user");
                // TODO: pass user instead and make sure it doesn't always cache
                populate(user != null ? user.username : "");
            } else if (bundle.containsKey("username")) {
                String username = bundle.getString("username");
                populate(username);
            } else {
                populateWithSelf();
            }
            getArguments().clear();
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

        this.user = user;
        if (isAdded()) {
            Glide.with(imageView.getContext()).clear(imageView);
        }

        ProgressController loading = new ProgressController(imageProgressBar);
        if (!TextUtils.isEmpty(user.image) && isAdded()) {
            String url = ImageUtil.getUrl(user.image);
            // TODO: Tries to add from destroyed activity
            Glide.with(imageView.getContext())
                    .load(url)
                    .listener(loading)
                    .into(imageView);
        } else {
            loading.hide();
        }

        String capitalizedUsername = user.username.toUpperCase().substring(0, 1) + user.username.substring(1);
        Level level = new Level(user.level);
        String scoreText = context.getString(R.string.title_score, String.valueOf(user.score));
        String levelText = context.getString(R.string.title_level, level.getVisualLevel());
        usernameView.setText(capitalizedUsername);
        scoreView.setText(scoreText);
        levelView.setText(levelText);
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
        challengeTreeButton.setVisibility(selfModVisibilty);
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
