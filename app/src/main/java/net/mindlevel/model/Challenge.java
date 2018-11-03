package net.mindlevel.model;

import android.content.Context;
import android.text.TextUtils;

import net.mindlevel.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Challenge implements Serializable {
    public final String title, description, image, creator;
    public final int id, levelRestriction, scoreRestriction;
    public final long created;
    public final boolean validated;

    public Challenge(int id,
                     String title,
                     String description,
                     String image,
                     long created,
                     String creator,
                     int levelRestriction,
                     int scoreRestriction,
                     boolean validated) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.created = created;
        this.creator = creator;
        this.levelRestriction = levelRestriction;
        this.scoreRestriction = scoreRestriction;
        this.validated = validated;
    }

    public String toString(Context context) {
        List<String> tokens = new ArrayList<>();
        tokens.add(Integer.toString(id));
        tokens.add(title);
        tokens.add(description);
        tokens.add(image);
        tokens.add(Long.toString(created));
        tokens.add(creator);
        tokens.add(Integer.toString(levelRestriction));
        tokens.add(Integer.toString(scoreRestriction));
        tokens.add(Boolean.toString(validated));
        return TextUtils.join(context.getString(R.string.field_delim), tokens);
    }

    public static Challenge fromString(String marshalled, Context context) {
        String[] fields = marshalled.split(context.getString(R.string.field_delim));
        int id = Integer.valueOf(fields[0]);
        String title = fields[1];
        String description = fields[2];
        String image = fields[3];
        long created = Long.valueOf(fields[4]);
        String creator = fields[5];
        int levelRestriction = Integer.valueOf(fields[6]);
        int scoreRestriction = Integer.valueOf(fields[7]);
        boolean validated = Boolean.valueOf(fields[8]);
        return new Challenge(id, title, description, image, created, creator, levelRestriction, scoreRestriction,
                             validated);
    }

    @Override
    public boolean equals(Object m) {
        return m == this || (m instanceof Challenge && this.hashCode() == m.hashCode());
    }

    @Override
    public int hashCode() {
        return id;
    }
}
