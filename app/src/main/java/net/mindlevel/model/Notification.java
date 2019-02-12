package net.mindlevel.model;

import java.io.Serializable;

public class Notification implements Serializable {

    public final String title, description, image, type;
    public final int id, targetId;
    public final long created;

    public Notification(String title, String description, int targetId, String type) {
        this(0, title, description, "", targetId, type, 0);
    }

    public Notification(int id,
                        String title,
                        String description,
                        String image,
                        int targetId,
                        String type,
                        long created) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.targetId = targetId;
        this.type = type;
        this.created = created;
    }

    public Type getType() {
        return Type.valueOf(type.toUpperCase());
    }

    @Override
    public boolean equals(Object a) {
        return a == this || (a instanceof Notification && this.hashCode() == a.hashCode());
    }

    @Override
    public int hashCode() {
        return id;
    }

    public enum Type {
        COMMENT("comment"), ACCOMPLISHMENT("accomplishment"), CHALLENGE("challenge"), OTHER("other");

        private String type;

        private Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
