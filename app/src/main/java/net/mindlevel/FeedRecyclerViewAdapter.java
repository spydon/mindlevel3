package net.mindlevel;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.FeedFragment.OnListFragmentInteractionListener;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.util.ImageUtil;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Accomplishment} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.ViewHolder> {

    private final List<Accomplishment> items;
    private final OnListFragmentInteractionListener listener;
    private View view;

    public FeedRecyclerViewAdapter(List<Accomplishment> items, OnListFragmentInteractionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(items.get(position));
        holder.titleView.setText(items.get(position).title);
        ImageLikeView imageView = holder.imageView;
        String url = ImageUtil.getUrl(holder.item.image);
        Glide.with(imageView.getContext())
                .load(url)
                .listener(new ProgressBarController(holder.progressBar))
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView titleView;
        public final ImageLikeView imageView;
        public final ProgressBar progressBar;
        public Accomplishment item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            titleView = (TextView) view.findViewById(R.id.title);
            imageView = (ImageLikeView) view.findViewById(R.id.image);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
            TextView imageText = (TextView) view.findViewById(R.id.image_text);
            imageView.setTextView(imageText);
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
