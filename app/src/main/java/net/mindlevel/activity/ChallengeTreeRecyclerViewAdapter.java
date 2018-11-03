package net.mindlevel.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.mindlevel.R;
import net.mindlevel.fragment.ChallengesFragment;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.Level;
import net.mindlevel.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Challenge} and makes a call to the
 * specified {@link ChallengesFragment.OnListFragmentInteractionListener}.
 */
class ChallengeTreeRecyclerViewAdapter extends RecyclerView.Adapter<ChallengeTreeRecyclerViewAdapter.ViewHolder> {

    private final List<List<Challenge>> orderedChallenges;
    private final List<ChallengeTreeRowRecyclerViewAdapter> rowAdapters;
    private final ChallengeTreeActivity parent;
    private final User user;
    private LayoutInflater inflater;

    ChallengeTreeRecyclerViewAdapter(final List<Challenge> challenges,
                                     final User user,
                                     ChallengeTreeActivity parent) {
        this.orderedChallenges = new ArrayList <>();
        this.rowAdapters = new ArrayList <>();
        this.parent = parent;
        this.user = user;
        this.inflater = LayoutInflater.from(parent.getBaseContext());
        setHasStableIds(true);

        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateChallenges(challenges);
            }
        });
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
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.activity_challenge_tree_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        List<Challenge> challenges = orderedChallenges.get(position);
        holder.item = challenges;
        holder.levelView.setText("Level: " + new Level(challenges.get(0).levelRestriction).getVisualLevel());
        if(rowAdapters.size() <= position) {
            RecyclerView recyclerView = holder.view.findViewById(R.id.list);
            ChallengeTreeRowRecyclerViewAdapter adapter =
                    new ChallengeTreeRowRecyclerViewAdapter(challenges, user, parent);
            recyclerView.setLayoutManager(new LinearLayoutManager(parent.getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);
            rowAdapters.add(adapter);
        }
        rowAdapters.get(position).notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderedChallenges.size();
    }

    @Override
    public long getItemId(int position) {
        return orderedChallenges.get(position).hashCode();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public List<Challenge> item;
        public final View view;
        final TextView levelView;
        final RecyclerView rowView;
        final ProgressBar progressBar;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            levelView = view.findViewById(R.id.level);
            rowView = view.findViewById(R.id.list);
            progressBar = view.findViewById(R.id.progress);
        }
    }
}
