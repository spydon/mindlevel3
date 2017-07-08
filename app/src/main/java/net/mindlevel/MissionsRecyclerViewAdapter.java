package net.mindlevel;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.mindlevel.MissionsFragment.OnListFragmentInteractionListener;
import net.mindlevel.model.Mission;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Mission} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MissionsRecyclerViewAdapter extends RecyclerView.Adapter<MissionsRecyclerViewAdapter.ViewHolder> {

    private final List<Mission> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MissionsRecyclerViewAdapter(List<Mission> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_missions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).title);
        holder.mDescriptionView.setText(mValues.get(position).description);

        ImageView imageView = holder.mImageView;
        Glide.with(imageView.getContext())
                .load(holder.mItem.imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        holder.mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        //holder.mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                })
                .into(imageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Mission mItem;
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTitleView;
        public final TextView mDescriptionView;
        public final ProgressBar mProgressBar;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.image);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
