package net.mindlevel.model;

import java.io.Serializable;

public class UserExtra implements Serializable {
    private final String username, password, email;

    public UserExtra(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
