package net.mindlevel.api;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.ProgressBarController;
import net.mindlevel.R;
import net.mindlevel.UserFragment;
import net.mindlevel.api.endpoint.UserEndpoint;
import net.mindlevel.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserController extends BackendService {

    private static UserEndpoint endpoint;
    private static View view;
    private ImageView imageView;
    private TextView usernameView;
    private TextView scoreView;
    private TextView descriptionView;
    private ProgressBar progressBar;

    public UserController(View view) {
        super();
        this.endpoint = retrofit.create(UserEndpoint.class);
        this.view = view;
        this.imageView = (ImageView) view.findViewById(R.id.image);
        this.usernameView = (TextView) view.findViewById(R.id.username);
        this.scoreView = (TextView) view.findViewById(R.id.score);
        this.descriptionView = (TextView) view.findViewById(R.id.description);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void getUser(String username) {

        Call<User> user = endpoint.getUser(username);

        user.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> userResponse) {
                User user = userResponse.body();

                Glide.with(imageView.getContext())
                        .load(user.imageUrl)
                        .listener(new ProgressBarController(progressBar))
                        .into(imageView);

                usernameView.setText(user.username);
                scoreView.setText(String.valueOf(user.score));
                descriptionView.setText(user.description);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
