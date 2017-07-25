package net.mindlevel;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.UserFragment.OnFragmentInteractionListener;
import net.mindlevel.model.User;

/**
 * {@link RecyclerView.Adapter} that can display a {@link User} and makes a call to the
 * specified {@link OnFragmentInteractionListener}.
 */
public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    private final User mUser;
    private final OnFragmentInteractionListener mListener;

    public UserRecyclerViewAdapter(User user, OnFragmentInteractionListener listener) {
        mUser = user;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mUser = mUser;
        holder.mUsernameView.setText(mUser.username);
        holder.mDescriptionView.setText(mUser.description);

        ImageView imageView = holder.mImageView;
        Glide.with(imageView.getContext())
                .load(holder.mUser.imageUrl)
                .listener(new ProgressBarController(holder.mProgressBar))
                .into(imageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onFragmentInteraction(holder.mUser);
                    // TODO: Handle onClick
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public User mUser;
        public final View mView;
        public final ImageView mImageView;
        public final TextView mUsernameView;
        public final TextView mDescriptionView;
        public final ProgressBar mProgressBar;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.image);
            mUsernameView = (TextView) view.findViewById(R.id.title);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUsernameView.getText() + "'";
        }
    }
}
