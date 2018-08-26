package net.mindlevel.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pchmn.materialchips.ChipView;

import net.mindlevel.R;
import net.mindlevel.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private final List<Category> categories;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;
    private Context context;

    public CategoryRecyclerViewAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.categories = categories;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Category category = categories.get(position);
        holder.item = category;

        holder.chip.setLabel(category.name);
        holder.chip.setOnChipClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Change challenges");
                //CoordinatorUtil somethings context
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public long getItemId(int position) {
        List<Category> indexed = new ArrayList<>(categories);
        return indexed.get(position).hashCode();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View view;
        Category item;
        ChipView chip;

        ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            chip = itemView.findViewById(R.id.category_chip);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}