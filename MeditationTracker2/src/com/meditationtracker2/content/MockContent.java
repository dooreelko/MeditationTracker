package com.meditationtracker2.content;

import java.sql.Date;
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

    @SuppressWarnings("deprecation")
	public MockContent(Context where) {
        // Add 3 sample items.
        addItem(new Practice(0, where.getString(R.string.refuge), 
        		R.drawable.refuge, "url://meditracker/refuge", 
        		111111, 12345, 108,216, 
        		new Date(2013, 7, 19), new Date(2014, 8, 20)));
        
        addItem(new Practice(1, where.getString(R.string.diamondMind), 
        		R.drawable.diamond_mind, "url://meditracker/diamondmind",
        		111111, 8634, 216, 324, 
        		new Date(2013, 8, 18), new Date(2014, 7, 21)));
        
        addItem(new Practice(2, where.getString(R.string.mandalaOffering), 
        		R.drawable.mandala_offering, "url://meditracker/mandala",
        		111111, 54321, 20, 100, 
        		new Date(2013, 9, 17), new Date(2014, 6, 22)));
        
        addItem(new Practice(3, where.getString(R.string.guruYoga), 
        		R.drawable.guru_yoga, "url://meditracker/guru",
        		111111, 2364, 150, 300,
        		new Date(2013, 10, 16), new Date(2014, 5, 23)));
        
        addItem(new Practice(4, "My very custom practice", 
        		R.drawable.sixteenth_karmapa, "url://meditracker/karmapa", 
        		111111, 54321, 80, 99,
        		new Date(2013, 11, 15), new Date(2014, 4, 24)));
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

	@Override
	public void savePractice(Practice practice) {
		ITEM_MAP.put(practice.id, practice);
	}
}
