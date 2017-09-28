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
import net.mindlevel.util.PreferencesUtil;
import net.mindlevel.util.ProgressController;
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

    private ImageView imageView;
    private TextView usernameView;
    private TextView scoreView;
    private TextView descriptionView;
    private View view, imageProgressBar;
    private FloatingActionButton editButton, signOutButton, selfButton;
    private Context context;
    private User user;
    private int shortAnimTime;

    private final static int UPDATE_USER = 1;

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

        showInfo(false, true);
        populateUserFragment();

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
                String username = PreferencesUtil.getUsername(context);
                String sessionId = PreferencesUtil.getSessionId(context);
                Login login = new Login(username, "", sessionId);
                loginController.logout(login, signOutCallback);
            }
        });

        selfButton = (FloatingActionButton) view.findViewById(R.id.self_button);
        selfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateUserFragment();
            }
        });

        View coordinator = contentView.getRootView();
        NetworkUtil.connectionCheck(getContext(), coordinator);

        return view;
    }

    private void populateUserFragment() {
        String username = PreferencesUtil.getUsername(context);
        populateUserFragment(username);
    }

    public void populateUserFragment(String username) {
        controller.getUser(username, userCallback);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and title
        void onFragmentInteraction(Uri uri);
    }

    public void setUser(User user) {
        this.user = user;
        ProgressController loading = new ProgressController(imageProgressBar);
        showInfo(false, false);
        if(!TextUtils.isEmpty(user.image)) {
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

        if(PreferencesUtil.getUsername(context).equals(user.username)) {
            selfButton.setVisibility(GONE);
            editButton.setVisibility(VISIBLE);
            signOutButton.setVisibility(VISIBLE);
        }
    }

    private ControllerCallback<User> userCallback = new ControllerCallback<User>() {

        @Override
        public void onPostExecute(final Boolean success, final User user) {
            if(success) {
                setUser(user);
            } else {
                showInfo(true, false);
            }
        }
    };

    private ControllerCallback<Void> signOutCallback = new ControllerCallback<Void>() {

        @Override
        public void onPostExecute(final Boolean success, final Void nothing) {
            Intent loginIntent = new Intent(getContext(), LoginActivity.class);
            startActivity(loginIntent);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == UPDATE_USER) {
            populateUserFragment();
        }
    }
}
