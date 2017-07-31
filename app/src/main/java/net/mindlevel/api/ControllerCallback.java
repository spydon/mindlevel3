package net.mindlevel.api;

public interface ControllerCallback<T> {
    void onPostExecute(Boolean isSuccess, T response);
}
