package net.mindlevel.activity;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AndroidException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import net.mindlevel.R;
import net.mindlevel.fragment.ChallengesFragment;
import net.mindlevel.impl.ProgressController;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.User;
import net.mindlevel.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Challenge} and makes a call to the
 * specified {@link ChallengesFragment.OnListFragmentInteractionListener}.
 */
class ChallengeTreeRowRecyclerViewAdapter extends RecyclerView.Adapter<ChallengeTreeRowRecyclerViewAdapter.ViewHolder> {

    private final List<Challenge> challenges;
    private final ChallengeTreeActivity parent;
    private final User user;
    private LayoutInflater inflater;

    ChallengeTreeRowRecyclerViewAdapter(List<Challenge> challenges, User user, ChallengeTreeActivity parent) {
        this.challenges = challenges;
        this.parent = parent;
        this.user = user;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_challenge_tree_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Challenge challenge = challenges.get(position);
        holder.item = challenge;
        ImageView imageView = holder.imageView;
        String url = ImageUtil.getUrl(challenge.image);
        Glide.with(imageView.getContext())
                .load(url)
                .listener(new ProgressController(holder.progressBar))
                .into(imageView);


        if (challenge.levelRestriction <= user.level) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (parent != null) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        parent.onListFragmentInteraction(challenge);
                    }
                }
            });
        } else {
            imageView.setAlpha(0.4f);
            imageView.setColorFilter(R.color.disabledBackground);
        }
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    @Override
    public long getItemId(int position) {
        List<Challenge> indexed = new ArrayList<>(challenges);
        return indexed.get(position).hashCode();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public Challenge item;
        public final View view;
        final ImageView imageView;
        final ProgressBar progressBar;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.imageView = view.findViewById(R.id.image);
            this.progressBar = view.findViewById(R.id.progress);
        }

        @Override
        public String toString() {
            return super.toString() + " ChallengeId: " + item.id;
        }
    }
}
