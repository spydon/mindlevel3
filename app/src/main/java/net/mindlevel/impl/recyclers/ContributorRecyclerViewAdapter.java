package net.mindlevel.impl.recyclers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pchmn.materialchips.ChipView;

import net.mindlevel.R;
import net.mindlevel.model.User;
import net.mindlevel.util.CoordinatorUtil;

import java.util.ArrayList;
import java.util.List;

public class ContributorRecyclerViewAdapter extends RecyclerView.Adapter<ContributorRecyclerViewAdapter.ViewHolder> {

    private final List<User> contributors;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;
    private Context context;

    public ContributorRecyclerViewAdapter(Context context, List<User> contributors) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.contributors = contributors;
        setHasStableIds(true);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.contributor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User contributor = contributors.get(position);
        holder.chip.setLabel(contributor.username);
        holder.chip.setOnChipClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordinatorUtil.toUser(context, contributor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contributors.size();
    }

    @Override
    public long getItemId(int position) {
        List<User> indexed = new ArrayList<>(contributors);
        return indexed.get(position).hashCode();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ChipView chip;

        ViewHolder(View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.contributor);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public User getItem(int id) {
        return contributors.get(id);
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}