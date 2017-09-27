package net.mindlevel.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.util.ProgressController;
import net.mindlevel.R;
import net.mindlevel.model.Mission;
import net.mindlevel.util.ImageUtil;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Mission} and makes a call to the
 * specified {@link MissionsFragment.OnListFragmentInteractionListener}.
 */
public class MissionsRecyclerViewAdapter extends RecyclerView.Adapter<MissionsRecyclerViewAdapter.ViewHolder> {

    private final List<Mission> missions;
    private final MissionsFragment.OnListFragmentInteractionListener listener;

    public MissionsRecyclerViewAdapter(List<Mission> missions, MissionsFragment.OnListFragmentInteractionListener listener) {
        this.missions = missions;
        this.listener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_missions_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = missions.get(position);
        holder.titleView.setText(missions.get(position).title);
        holder.descriptionView.setText(missions.get(position).description);

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
        return missions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Mission item;
        public final View view;
        public final ImageView imageView;
        public final TextView titleView;
        public final TextView descriptionView;
        public final ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.image);
            titleView = (TextView) view.findViewById(R.id.title);
            descriptionView = (TextView) view.findViewById(R.id.description);
            progressBar = (ProgressBar) view.findViewById(R.id.image_progress);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleView.getText() + "'";
        }
    }
}
