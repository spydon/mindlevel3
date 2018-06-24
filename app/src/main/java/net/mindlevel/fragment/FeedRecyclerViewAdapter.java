package net.mindlevel.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.impl.ImageLikeView;
import net.mindlevel.impl.ProgressController;
import net.mindlevel.R;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.util.ImageUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Accomplishment} and makes a call to the
 * specified {@link FeedFragment.OnListFragmentInteractionListener}.
 */
class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.ViewHolder> {

    private final Set<Accomplishment> items;
    private final FeedFragment.OnListFragmentInteractionListener listener;
    private View view;

    FeedRecyclerViewAdapter(Set<Accomplishment> items,
                            FeedFragment.OnListFragmentInteractionListener listener) {
        this.items = items;
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Accomplishment accomplishment = (Accomplishment)items.toArray()[position];
        holder.setItem(accomplishment);
        holder.titleView.setText(accomplishment.title);
        ImageLikeView imageView = holder.imageView;
        String url = ImageUtil.getUrl(holder.item.image);
        Glide.with(imageView.getContext())
                .load(url)
                .listener(new ProgressController(holder.progressBar))
                .into(imageView);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onListFragmentInteraction(holder.item);
                }
            }
        };
        holder.view.setOnClickListener(onClickListener);
        imageView.setClickListener(onClickListener, view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        List<Accomplishment> indexed = new ArrayList<>(items);
        return indexed.get(position).hashCode();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        final TextView titleView;
        final ImageLikeView imageView;
        final ProgressBar progressBar;
        public Accomplishment item;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            titleView = view.findViewById(R.id.title);
            imageView = view.findViewById(R.id.image);
            progressBar = view.findViewById(R.id.progress);
            TextView imageText = view.findViewById(R.id.image_text);
            ProgressBar likeProgress = view.findViewById(R.id.progress_like);
            imageView.setTextView(imageText);
            imageView.setProgressLike(likeProgress);
        }

        public void setItem(Accomplishment item) {
            this.item = item;
            imageView.setId(item.id);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleView.getText() + "'";
        }
    }
}
