package net.mindlevel.api;

import net.mindlevel.api.endpoint.UserEndpoint;
import net.mindlevel.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserController extends BackendService {

    private static UserEndpoint endpoint;

    public UserController() {
        super();
        endpoint = retrofit.create(UserEndpoint.class);
    }

    public void getUser(String username, final ControllerCallback<User> callback) {

        Call<User> userCall = endpoint.getUser(username);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> userResponse) {
                if(userResponse.isSuccessful()) {
                    User user = userResponse.body();
                    callback.onPostExecute(true, user);
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }

}
