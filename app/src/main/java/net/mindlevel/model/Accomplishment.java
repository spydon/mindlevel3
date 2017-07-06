package net.mindlevel.model;

import java.io.Serializable;

public class Accomplishment implements Serializable{

    public final String id, title, description, imageUrl;
    public final Mission mission;

    public Accomplishment(String id, String title, String description, String imageUrl, Mission mission) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.mission = mission;
    }
}
