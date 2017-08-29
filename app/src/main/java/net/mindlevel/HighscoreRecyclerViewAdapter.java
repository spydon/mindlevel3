package net.mindlevel;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.HighscoreFragment.OnListFragmentInteractionListener;
import net.mindlevel.model.Mission;
import net.mindlevel.model.User;
import net.mindlevel.util.ImageUtil;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Mission} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class HighscoreRecyclerViewAdapter extends RecyclerView.Adapter<HighscoreRecyclerViewAdapter.ViewHolder> {

    private final List<User> users;
    private final OnListFragmentInteractionListener listener;

    public HighscoreRecyclerViewAdapter(List<User> users, OnListFragmentInteractionListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_highscore, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = users.get(position);
        holder.positionView.setText(String.valueOf(position+1));
        holder.usernameView.setText(users.get(position).username);
        holder.scoreView.setText(String.valueOf(users.get(position).score));

        ImageView imageView = holder.imageView;
        String url = ImageUtil.getUrl(holder.item.image);
        Glide.with(imageView.getContext())
                .load(url)
                .listener(new ProgressController(holder.progressBar))
                .into(imageView);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onListFragmentInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public User item;
        public final View view;
        public final ImageView imageView;
        public final TextView usernameView;
        public final TextView scoreView;
        public final TextView positionView;
        public final ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.image);
            usernameView = (TextView) view.findViewById(R.id.username);
            scoreView = (TextView) view.findViewById(R.id.score);
            positionView = (TextView) view.findViewById(R.id.position);
            progressBar = (ProgressBar) view.findViewById(R.id.image_progress);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + usernameView.getText() + "'";
        }
    }
}
