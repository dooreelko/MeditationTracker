package com.meditationtracker2.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;

import com.meditationtracker2.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class MockContent implements IPracticeProvider {
    /**
     * A map of sample (dummy) items, by ID.
     */
    @SuppressLint("UseSparseArrays")
	public Map<Integer, Practice> ITEM_MAP = new HashMap<Integer, Practice>();

    public MockContent(Context where) {
        // Add 3 sample items.
        addItem(new Practice(1, where.getString(R.string.refuge), 111111, 12345, R.drawable.refuge));
        addItem(new Practice(2, where.getString(R.string.diamondMind), 111111, 8634, R.drawable.diamond_mind));
        addItem(new Practice(3, where.getString(R.string.mandalaOffering), 111111, 54321, R.drawable.mandala_offering));
        addItem(new Practice(4, where.getString(R.string.guruYoga), 111111, 2364, R.drawable.guru_yoga));
        addItem(new Practice(5, "My very custom practice", 111111, 54321, R.drawable.sixteenth_karmapa));
    }

    private void addItem(Practice item) {
        ITEM_MAP.put(item.id, item);
    }

	@Override
	public List<Practice> getPractices() {
		return new ArrayList<Practice>(ITEM_MAP.values());
	}

	@Override
	public Practice getPractice(int id) {
		return ITEM_MAP.get(id);
	}
}
