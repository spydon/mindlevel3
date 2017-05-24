package net.mindlevel.model;

import java.io.Serializable;

public class Accomplishment extends Mission implements Serializable{
    public Accomplishment(String id, String name, String description, String imageUrl) {
        super(id, name, description, imageUrl);
    }
}
