package net.mindlevel.impl.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pchmn.materialchips.ChipView;

import net.mindlevel.R;
import net.mindlevel.model.Comment;
import net.mindlevel.util.CoordinatorUtil;
import net.mindlevel.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {

    private final List<Comment> comments;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;
    private Context context;
    private String username;

    public CommentRecyclerViewAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.comments = comments;
        this.username = PreferencesUtil.getUsername(context);
        setHasStableIds(true);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.comment_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Comment comment = comments.get(position);

        if (comment.username.equals(this.username)) {
            holder.chip.setChipBackgroundColor(context.getResources().getColor(R.color.commentSelf));
            holder.container.setGravity(Gravity.END);
            holder.container.setPadding(30, 0, 0, 0);
        } else {
            holder.chip.setChipBackgroundColor(context.getResources().getColor(R.color.commentOther));
            holder.container.setGravity(Gravity.START);
            holder.container.setPadding(0, 0, 0, 0);
        }

        holder.comment.setText(comment.comment);
        holder.chip.setLabel(comment.username);
        holder.chip.setOnChipClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordinatorUtil.toUser(context, comment.username);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public long getItemId(int position) {
        List<Comment> indexed = new ArrayList<>(comments);
        return indexed.get(position).hashCode();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ChipView chip;
        TextView comment;
        LinearLayout container;

        ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.comment_container);
            chip = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Comment getItem(int id) {
        return comments.get(id);
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}