package net.mindlevel.model;

public class Login {
    public final String username, password, session;

    public Login(String username, String password) {
        this(username, password, "");
    }

    public Login(String username, String password, String session) {
        this.username = username;
        this.password = password;
        this.session = session;
    }
}
