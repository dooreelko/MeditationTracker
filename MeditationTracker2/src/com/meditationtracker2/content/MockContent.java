package com.meditationtracker2.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.meditationtracker2.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MockContent {

    /**
     * An array of sample (dummy) items.
     */
    public List<Practice> ITEMS = new ArrayList<Practice>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public Map<String, Practice> ITEM_MAP = new HashMap<String, Practice>();

    public MockContent(Context where) {
        // Add 3 sample items.
        addItem(new Practice("1", where.getString(R.string.refuge), 111111, 12345, R.drawable.refuge));
        addItem(new Practice("2", where.getString(R.string.diamondMind), 111111, 8634, R.drawable.diamond_mind));
        addItem(new Practice("3", where.getString(R.string.mandalaOffering), 111111, 54321, R.drawable.mandala_offering));
        addItem(new Practice("4", where.getString(R.string.guruYoga), 111111, 2364, R.drawable.guru_yoga));
        addItem(new Practice("5", "My very custom practice", 111111, 54321, R.drawable.sixteenth_karmapa));
    }

    private void addItem(Practice item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    
    /**
     * A dummy item representing a piece of content.
     */
    public class Practice {
        public String id;
        public String title;
		public final int imageResId;
		public final int totalCount;
		public final int currentCount;

        public Practice(String id, String title, int totalCount, int currentCount, int resId) {
            this.id = id;
            this.title = title;
			this.totalCount = totalCount;
			this.currentCount = currentCount;
			this.imageResId = resId;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
