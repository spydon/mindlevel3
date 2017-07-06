package net.mindlevel.dummy;

import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Mission;

import java.util.ArrayList;
import java.util.List;

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

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            createDummyItem(i);
        }
    }

    private static void createDummyItem(int position) {
        ACCOMPLISHMENTS.add(
                new Accomplishment(
                        String.valueOf(position),
                        "Accomplishment " + position,
                        makeDetails(position),
                        "http://i.imgur.com/DvpvklR.png"));
        MISSIONS.add(
                new Mission(
                        String.valueOf(position),
                        "Mission " + position,
                        makeDetails(position),
                        "http://i.imgur.com/DvpvklR.png"));
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
