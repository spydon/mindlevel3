package net.mindlevel.fragment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.mindlevel.R;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.Level;
import net.mindlevel.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Challenge} and makes a call to the
 * specified {@link ChallengeTreeFragment.OnListFragmentInteractionListener}.
 */
class ChallengeTreeRecyclerViewAdapter extends RecyclerView.Adapter<ChallengeTreeRecyclerViewAdapter.ViewHolder> {

    private final List<List<Challenge>> orderedChallenges;
    private final ChallengeTreeFragment.OnListFragmentInteractionListener listener;
    private User user;

    ChallengeTreeRecyclerViewAdapter(final List<Challenge> challenges,
                                     ChallengeTreeFragment.OnListFragmentInteractionListener listener) {
        this.orderedChallenges = new ArrayList <>();
        this.listener = listener;
        setHasStableIds(true);

        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateChallenges(challenges);
            }
        });
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    private void updateChallenges(List<Challenge> challenges) {
        HashMap<Integer, List<Challenge>> mappedChallenges = new HashMap<>();
        for (Challenge c : challenges) {
            int level = c.levelRestriction;
            // TODO: Change to getOrDefault once API level > 24
            if(mappedChallenges.containsKey(level)) {
                mappedChallenges.get(level).add(c);
            } else {
                List<Challenge> levelList = new ArrayList <>();
                levelList.add(c);
                mappedChallenges.put(level, levelList);
            }
        }

        int index = 0;
        orderedChallenges.clear(); // TODO: Only update the ones that have been updated
        while (!mappedChallenges.keySet().isEmpty()) {
            if(mappedChallenges.containsKey(index)) {
                List<Challenge> currentLevelChallenges = mappedChallenges.get(index);
                orderedChallenges.add(currentLevelChallenges);
                mappedChallenges.remove(index);
            }
            index++;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_challenge_tree_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        List<Challenge> currentChallenges = holder.challenges;
        List<Challenge> updatedChallenges = orderedChallenges.get(position);
        Level level = new Level(updatedChallenges.get(0).levelRestriction);
        Context context = holder.view.getContext();
        String levelText = context.getString(R.string.title_level, level.getVisualLevel());
        holder.levelView.setText(levelText);
        if (!currentChallenges.containsAll(updatedChallenges) || !updatedChallenges.containsAll(currentChallenges)) {
            currentChallenges.clear();
            currentChallenges.addAll(updatedChallenges);
            holder.adapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return orderedChallenges.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
       return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        final List<Challenge> challenges;
        final TextView levelView;
        final RecyclerView rowView;
        final ProgressBar progressBar;
        final ChallengeTreeRowRecyclerViewAdapter adapter;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.challenges = new ArrayList <>();
            levelView = view.findViewById(R.id.level_title);
            rowView = view.findViewById(R.id.list);
            progressBar = view.findViewById(R.id.progress);
            adapter = new ChallengeTreeRowRecyclerViewAdapter(challenges, listener);
            LinearLayoutManager layoutManager =
                    new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
            rowView.setLayoutManager(layoutManager);
            rowView.setAdapter(adapter);
         }
    }
}
