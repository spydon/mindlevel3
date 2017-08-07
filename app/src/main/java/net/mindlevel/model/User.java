package net.mindlevel.model;

import java.io.Serializable;

public class User implements Serializable {
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
}
