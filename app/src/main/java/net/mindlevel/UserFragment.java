package net.mindlevel;

// TODO: Change back to non-support lib
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.LoginController;
import net.mindlevel.api.UserController;
import net.mindlevel.model.Login;
import net.mindlevel.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class UserFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private UserController controller;
    private LoginController loginController;

    private ImageView imageView;
    private TextView usernameView;
    private TextView scoreView;
    private TextView descriptionView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_user, container, false);

        this.imageView = (ImageView) view.findViewById(R.id.image);
        this.usernameView = (TextView) view.findViewById(R.id.username);
        this.scoreView = (TextView) view.findViewById(R.id.score);
        this.descriptionView = (TextView) view.findViewById(R.id.description);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);

        final Context context = getContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        this.controller = new UserController(context);
        this.loginController = new LoginController(context);
        controller.getUser(username, userCallback);

        Button signOutButton = (Button) view.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");
                String sessionId = sharedPreferences.getString("sessionId", "");
                Login login = new Login(username, "", sessionId);
                loginController.logout(login, signOutCallback);
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    private void userVisibility(boolean visible) {
        imageView.setVisibility(View.GONE);
        usernameView.setVisibility(View.GONE);
        scoreView.setVisibility(View.GONE);
        descriptionView.setVisibility(View.GONE);
    }

    private ControllerCallback<User> userCallback = new ControllerCallback<User>() {

        @Override
        public void onPostExecute(final Boolean success, final User user) {
            ProgressBarController loading = new ProgressBarController(progressBar);
            if (success) {
                Glide.with(imageView.getContext())
                        .load(user.image)
                        .listener(loading)
                        .into(imageView);

                usernameView.setText(user.username);
                scoreView.setText(String.valueOf(user.score));
                descriptionView.setText(user.description);
            } else {
                loading.hide();
                descriptionView.setText("Something went wrong.");
            }
        }
    };

    private ControllerCallback<Void> signOutCallback = new ControllerCallback<Void>() {

        @Override
        public void onPostExecute(final Boolean success, final Void nothing) {
            ProgressBarController loading = new ProgressBarController(progressBar);
            userVisibility(false);
            if (success) {
                Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                startActivity(loginIntent);
            } else {
                userVisibility(true);
                loading.hide();
                descriptionView.setText("Something went wrong.");
            }
        }
    };
}
