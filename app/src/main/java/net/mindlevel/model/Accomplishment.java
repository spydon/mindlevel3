package net.mindlevel.model;

import java.io.Serializable;

public class Accomplishment implements Serializable {

    public final String title, description, image;
    public final int id, challengeId, score;
    public final long created;

    public Accomplishment(String title, String description, int challengeId) {
        this(0, title, description, "", challengeId, 0, 0);
    }
    public Accomplishment(int id,
                          String title,
                          String description,
                          String image,
                          int challengeId,
                          int score,
                          long created) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.challengeId = challengeId;
        this.score = score;
        this.created = created;
    }

    @Override
    public boolean equals(Object a) {
        return a == this || (a instanceof Accomplishment && this.hashCode() == a.hashCode());
    }

    @Override
    public int hashCode() {
        return id;
    }
}
