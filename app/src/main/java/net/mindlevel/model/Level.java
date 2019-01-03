package net.mindlevel.model;

import java.util.Collections;

public class Level {
    public final int level;
    private final int[] icons = {0x1F476, 0x270A, 0x1F525, 0x1F47D, 0x1F331};
    private final int[] stages = {0, 1, 5, 10, 15};

    public Level(int level) {
        this.level = level;
    }

    public String getVisualLevel() {
        int lastStage = 0;
        String visualLevel = getEmojiByUnicode(icons[0]);
        for(int i = 0; i < stages.length; i++) {
            if(stages[i] > level) {
                int diff = level-lastStage+1;
                String icon = getEmojiByUnicode(icons[i-1]);
                StringBuilder b = new StringBuilder();
                for (String c : Collections.nCopies(diff, icon)) {
                    b.append(c);
                }
                visualLevel = b.toString();
                break;
            } else {
                lastStage = stages[i];
            }
        }
        return visualLevel;
    }

    private String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
