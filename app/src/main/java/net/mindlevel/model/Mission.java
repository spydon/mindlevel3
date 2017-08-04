package net.mindlevel.model;

import java.io.Serializable;

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
}
