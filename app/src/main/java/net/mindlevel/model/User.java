package net.mindlevel.model;

public class User {
    public final String username, description, image;
    public final int score;

    public User(String username, String description, String image, int score) {
        this.username = username;
        this.description = description;
        this.image = image;
        this.score = score;
    }
}
