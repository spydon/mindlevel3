package net.mindlevel.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pchmn.materialchips.ChipView;

import net.mindlevel.R;
import net.mindlevel.model.Mission;
import net.mindlevel.model.User;
import net.mindlevel.model.UserChip;
import net.mindlevel.util.CoordinatorUtil;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.ProgressController;

import java.util.List;

public class ContributorRecyclerViewAdapter extends RecyclerView.Adapter<ContributorRecyclerViewAdapter.ViewHolder> {

    private final List<User> contributors;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;
    private Context context;

    // data is passed into the constructor
    public ContributorRecyclerViewAdapter(Context context, List<User> contributors) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.contributors = contributors;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.contributor_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User contributor = contributors.get(position);
        holder.chip.setLabel(contributor.username);
        holder.chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordinatorUtil.toUser(context, contributor);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return contributors.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ChipView chip;

        public ViewHolder(View itemView) {
            super(itemView);
            chip = (ChipView) itemView.findViewById(R.id.contributor);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public User getItem(int id) {
        return contributors.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}