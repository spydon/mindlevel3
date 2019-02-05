package net.mindlevel.model;

import java.io.Serializable;

public class Comment implements Serializable {
    public final String comment, username;
    public final int id, threadId;
    public final long created;

    public Comment(int threadId, String comment, String username) {
        this(0, threadId, comment, username, 0L);
    }

    public Comment(int id,
                   int threadId,
                   String comment,
                   String username,
                   long created) {
        this.id = id;
        this.threadId = threadId;
        this.comment = comment;
        this.username = username;
        this.created = created;
    }

    @Override
    public boolean equals(Object m) {
        return m == this || (m instanceof Comment && this.hashCode() == m.hashCode());
    }

    @Override
    public int hashCode() {
        return id;
    }
}
