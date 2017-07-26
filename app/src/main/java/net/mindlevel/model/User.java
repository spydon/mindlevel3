package net.mindlevel.model;

public class User {
    public final String username, description, imageUrl;
    public final int score;

    public User(String username, String description, String imageUrl, int score) {
        this.username = username;
        this.description = description;
        this.imageUrl = imageUrl;
        this.score = score;
    }
}
