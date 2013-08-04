package com.meditationtracker2.content;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        addItem(new Practice(0, where.getString(R.string.refuge), 
        		R.drawable.refuge, "content://com.meditationtracker2.images/refuge", 
        		111111, 12345, 0, 216,
        		buildCalendar(2013, 7, 19)));
        
        addItem(new Practice(1, where.getString(R.string.diamondMind), 
        		R.drawable.diamond_mind, "content://com.meditationtracker2.images/diamondMind",
        		111111, 8634, 216, 324, 
        		buildCalendar(2013, 8, 18)));
        
        addItem(new Practice(2, where.getString(R.string.mandalaOffering), 
        		R.drawable.mandala_offering, "content://com.meditationtracker2.images/mandalaOffering",
        		111111, 54321, 20, 100, 
        		buildCalendar(2013, 9, 17)));
        
        addItem(new Practice(3, where.getString(R.string.guruYoga), 
        		R.drawable.guru_yoga, "content://com.meditationtracker2.images/guruYoga",
        		111111, 2364, 150, 300,
        		buildCalendar(2013, 10, 16)));
        
        addItem(new Practice(4, "My very custom practice", 
        		R.drawable.sixteenth_karmapa, "content://com.meditationtracker2.images/sixteenth_karmapa", 
        		0, 54321, 80, 99,
        		buildCalendar(2013, 11, 15)));
    }

	private Date buildCalendar(int y, int m, int d) {
		Calendar cal = Calendar.getInstance();
		cal.set(y, m, d);
		
		return new Date(cal.getTimeInMillis());
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
