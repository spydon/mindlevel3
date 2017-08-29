package net.mindlevel.model;

import android.content.Context;
import android.text.TextUtils;

import net.mindlevel.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mission implements Serializable {
    public final String title, description, image, creator;
    public final int id;
    public final long created;
    public final boolean validated;

    public Mission(int id,
                   String title,
                   String description,
                   String image,
                   long created,
                   String creator,
                   boolean validated) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.created = created;
        this.creator = creator;
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
        tokens.add(Boolean.toString(validated));
        return TextUtils.join(context.getString(R.string.mission_field_delim), tokens);
    }

    public static Mission fromString(String marshalled, Context context) {
        String[] fields = marshalled.split(context.getString(R.string.mission_field_delim));
        int id = Integer.valueOf(fields[0]);
        String title = fields[1];
        String description = fields[2];
        String image = fields[3];
        long created = Long.valueOf(fields[4]);
        String creator = fields[5];
        boolean validated = Boolean.valueOf(fields[6]);
        return new Mission(id, title, description, image, created, creator, validated);
    }
}
