package net.mindlevel.fragment;

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
import net.mindlevel.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Challenge} and makes a call to the
 * specified {@link ChallengesFragment.OnListFragmentInteractionListener}.
 */
class ChallengesRecyclerViewAdapter extends RecyclerView.Adapter<ChallengesRecyclerViewAdapter.ViewHolder> {

    private final Set<Challenge> challenges;
    private final ChallengesFragment.OnListFragmentInteractionListener listener;

    ChallengesRecyclerViewAdapter(Set<Challenge> challenges, ChallengesFragment.OnListFragmentInteractionListener
            listener) {
        this.challenges = challenges;
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_challenge_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Challenge challenge = (Challenge) challenges.toArray()[position];
        holder.item = challenge;
        holder.titleView.setText(challenge.title);
        holder.descriptionView.setText(challenge.description);

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
        final TextView titleView;
        final TextView descriptionView;
        final ProgressBar progressBar;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            imageView = view.findViewById(R.id.image);
            titleView = view.findViewById(R.id.title);
            descriptionView = view.findViewById(R.id.description);
            progressBar = view.findViewById(R.id.image_progress);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleView.getText() + "'";
        }
    }
}
