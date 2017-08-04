package net.mindlevel.dummy;

import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Mission;
import net.mindlevel.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Accomplishment> ACCOMPLISHMENTS = new ArrayList<>();
    public static final List<Mission> MISSIONS = new ArrayList<>();
    public static final List<String> URLS = new ArrayList<>();
    public static final User USER = new User("spydon", "Description description", "http://i.imgur.com/DvpvklR.png", 0);

    private static final int COUNT = 25;

    static {
        URLS.add("http://i.imgur.com/DvpvklR.png");
        URLS.add("https://www.gstatic.com/webp/gallery/1.sm.jpg");
        URLS.add("https://www.gstatic.com/webp/gallery/2.sm.jpg");
        URLS.add("https://www.gstatic.com/webp/gallery/3.sm.jpg");
        URLS.add("https://www.gstatic.com/webp/gallery/4.sm.jpg");
        URLS.add("https://www.gstatic.com/webp/gallery/5.sm.jpg");
        URLS.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0qEm4tEK8X3SLrYJKmfW6w7T3yb4oeNiUbM8-D1X7_0EgGsixsg3bZ2g");
        Collections.shuffle(URLS);
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            createDummyItem(i);
        }
    }

    private static void createDummyItem(int position) {
        Mission mission = new Mission(
                position,
                "Mission " + position,
                makeDetails(position),
                URLS.get(position % URLS.size()),
                0, "spydon", true);
        Accomplishment accomplishment = new Accomplishment(
                position,
                "Accomplishment with a " + position,
                makeDetails(position),
                URLS.get(position % URLS.size()),
                mission.id, 0, 0L);
        MISSIONS.add(mission);
        ACCOMPLISHMENTS.add(accomplishment);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
}
