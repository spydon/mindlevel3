package net.mindlevel.model;

import java.io.Serializable;
import java.util.List;

public class Accomplishment implements Serializable {

    public final String title, description, image;
    public final int id, missionId, score;
    public final long created;

    public Accomplishment(int id,
                          String title,
                          String description,
                          String image,
                          int missionId,
                          int score,
                          long created) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.missionId = missionId;
        this.score = score;
        this.created = created;
    }
}
