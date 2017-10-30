package net.mindlevel.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import net.mindlevel.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable, Comparable<User> {
    public final String username, password, description, image;
    public final int score;
    public final long created, lastActive;

    public User(String username, String password, String description) {
        this.username = username;
        this.password = password;
        this.description = description;
        this.image = "";
        this.score = 0;
        this.created = 0L;
        this.lastActive = 0L;
    }

    public User(String username, String password, String description, String image, int score, long created, long
            lastActive) {
        this.username = username;
        this.password = password;
        this.description = description;
        this.image = image;
        this.score = score;
        this.created = created;
        this.lastActive = lastActive;
    }

    public String toString(Context context) {
        List<String> tokens = new ArrayList<>();
        tokens.add(username);
        tokens.add(password);
        tokens.add(description);
        tokens.add(image);
        tokens.add(Integer.toString(score));
        tokens.add(Long.toString(created));
        tokens.add(Long.toString(lastActive));
        return TextUtils.join(context.getString(R.string.field_delim), tokens);
    }

    public static User fromString(String marshalled, Context context) {
        String[] fields = marshalled.split(context.getString(R.string.field_delim));
        String username = fields[0];
        String password = fields[1];
        String description = fields[2];
        String image = fields[3];
        int score = Integer.valueOf(fields[4]);
        long created = Long.valueOf(fields[5]);
        long lastActive = Long.valueOf(fields[6]);
        return new User(username, password, description, image, score, created, lastActive);
    }

    @Override
    public boolean equals(Object u) {
        return u == this || (u instanceof User && this.hashCode() == u.hashCode());
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public int compareTo(@NonNull User otherUser) {
        int offset = otherUser.score - this.score;
        boolean isSame = this.equals(otherUser);
        int order = offset == 0 ? 1 : offset;
        return isSame ? 0 : order;
    }
}
