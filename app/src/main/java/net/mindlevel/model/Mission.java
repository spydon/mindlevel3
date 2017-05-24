package net.mindlevel.model;

import java.io.Serializable;

public class Mission implements Serializable {
    public final String id, name, description, imageUrl;

    public Mission(String id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
