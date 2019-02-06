package net.mindlevel.impl.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.fragment.HighscoreFragment;
import net.mindlevel.impl.ProgressController;
import net.mindlevel.R;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.Level;
import net.mindlevel.model.User;
import net.mindlevel.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Challenge} and makes a call to the
 * specified {@link HighscoreFragment.OnListFragmentInteractionListener}.
 */
public class HighscoreRecyclerViewAdapter extends RecyclerView.Adapter<HighscoreRecyclerViewAdapter.ViewHolder> {

    private final Set<User> users;
    private final HighscoreFragment.OnListFragmentInteractionListener listener;

    public HighscoreRecyclerViewAdapter(Set<User> users, HighscoreFragment.OnListFragmentInteractionListener listener) {
        this.users = users;
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_highscore_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        User user = (User)users.toArray()[position];
        holder.item = user;
        holder.positionView.setText(String.valueOf(position+1));
        holder.usernameView.setText(user.username);
        holder.scoreView.setText(String.valueOf(user.score));
        holder.levelView.setText(new Level(user.level).getVisualLevel());

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

    @Override
    public long getItemId(int position) {
        List<User> indexed = new ArrayList<>(users);
        return indexed.get(position).hashCode();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public User item;
        public final View view;
        final ImageView imageView;
        final TextView usernameView;
        final TextView scoreView;
        final TextView levelView;
        final TextView positionView;
        final ProgressBar progressBar;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            imageView = view.findViewById(R.id.image);
            usernameView = view.findViewById(R.id.username);
            scoreView = view.findViewById(R.id.score);
            levelView = view.findViewById(R.id.level);
            positionView = view.findViewById(R.id.position);
            progressBar = view.findViewById(R.id.image_progress);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + usernameView.getText() + "'";
        }
    }
}
