package net.mindlevel.model;

import java.io.Serializable;

public class Mission implements Serializable {
    public final String id, title, description, imageUrl;

    public Mission(String id, String title, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
