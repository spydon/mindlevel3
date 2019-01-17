package net.mindlevel.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.R;
import net.mindlevel.impl.ProgressController;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.User;
import net.mindlevel.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Challenge} and makes a call to the
 * specified {@link ChallengeTreeFragment.OnListFragmentInteractionListener}.
 */
class ChallengeTreeRowRecyclerViewAdapter extends RecyclerView.Adapter<ChallengeTreeRowRecyclerViewAdapter.ViewHolder> {

    private final List<Challenge> challenges;
    private final ChallengeTreeFragment.OnListFragmentInteractionListener listener;
    private LayoutInflater inflater;

    ChallengeTreeRowRecyclerViewAdapter(List<Challenge> challenges,
                                        ChallengeTreeFragment.OnListFragmentInteractionListener listener) {
        this.challenges = challenges;
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_challenge_tree_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Challenge challenge = challenges.get(position);
        holder.item = challenge;
        ImageView imageView = holder.imageView;
        ImageView lockView = holder.lockView;
        ImageView checkmarkView = holder.checkmarkView;
        TextView countView = holder.countView;
        String url = ImageUtil.getUrl(challenge.image);
        Context context = imageView.getContext();
        Glide.with(context)
                .load(url)
                .listener(new ProgressController(holder.progressBar))
                .into(imageView);

        if (!challenge.hasAccess) {
            imageView.setAlpha(0.4f);
            imageView.setColorFilter(R.color.disabledBackground);
            lockView.setVisibility(VISIBLE);
        } else if (challenge.finishCount > 0) {
            ((View)imageView.getParent()).setBackgroundColor(context.getResources().getColor(R.color.finishedBackground));
            imageView.setAlpha(0.4f);
            checkmarkView.setAlpha(0.6f);
            checkmarkView.setVisibility(VISIBLE);
            countView.setVisibility(VISIBLE);
            countView.setText("️🏁" + challenge.finishCount);
            lockView.setVisibility(GONE);
        } else {
            // Has access to the challenge but has not yet completed it
            imageView.clearColorFilter();
            imageView.setAlpha(1.0f);
            checkmarkView.setVisibility(GONE);
            lockView.setVisibility(GONE);
            countView.setVisibility(GONE);
        }

        if (challenge.hasAccess) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        listener.onListFragmentInteraction(challenge);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    @Override
    public long getItemId(int position) {
        List<Challenge> indexed = new ArrayList<>(challenges);
        return indexed.get(position).id;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public Challenge item;
        public final View view;
        final ImageView imageView;
        final ImageView lockView;
        final ImageView checkmarkView;
        final TextView countView;
        final ProgressBar progressBar;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.imageView = view.findViewById(R.id.image);
            this.lockView = view.findViewById(R.id.lock);
            this.checkmarkView = view.findViewById(R.id.checkmark);
            this.countView = view.findViewById(R.id.count);
            this.progressBar = view.findViewById(R.id.progress);
        }

        @Override
        public String toString() {
            return super.toString() + " ChallengeId: " + item.id;
        }
    }
}
