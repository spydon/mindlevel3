package net.mindlevel.model;

import java.io.Serializable;

public class Category implements Serializable {
    public final String name, image;
    public final int id;

    public Category(int id,
                    String name,
                    String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    @Override
    public boolean equals(Object m) {
        return m == this || (m instanceof Category && this.hashCode() == m.hashCode());
    }

    @Override
    public int hashCode() {
        return id;
    }
}
