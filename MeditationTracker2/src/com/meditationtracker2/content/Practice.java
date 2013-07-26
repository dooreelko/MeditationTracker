package com.meditationtracker2.content;

import java.util.Calendar;
import java.util.Date;

import com.meditationtracker2.R;

public class Practice {
    public int id;
    public String title;
	public int imageResId;
	public String imageUrl;
	public int totalCount;
	public int currentCount;
	private int scheduledForToday;
	public int completedToday;
	public Date lastPracticeDate;
	public int malaSize;

    public Practice(int id, String title, int totalCount, int currentCount, int resId) {
        this.id = id;
        this.title = title;
		this.totalCount = totalCount;
		this.currentCount = currentCount;
		this.imageResId = resId;
    }

	public Practice(int id, String title, int imageResId, String imageUrl,
			int totalCount, int currentCount, int scheduledForToday,
			int completedToday, Date lastPracticeDate) {
		super();
		this.id = id;
		this.title = title;
		this.imageResId = imageResId;
		this.imageUrl = imageUrl;
		this.totalCount = totalCount;
		this.currentCount = currentCount;
		this.setScheduledForToday(scheduledForToday);
		this.completedToday = completedToday;
		this.lastPracticeDate = lastPracticeDate;
	}

	public Practice() {
		this.imageResId = R.drawable.sixteenth_karmapa;
	}

	@Override
    public String toString() {
        return title;
    }

	public void addSession(int totalCount2) {
		// TODO Auto-generated method stub
		
	}

	public Date getScheduledCompletion() {
		Calendar cal = Calendar.getInstance();
		if (scheduledForToday != 0) {
			cal.add(Calendar.DAY_OF_YEAR, totalCount / scheduledForToday + 1);
		}
		
		return new Date(cal.getTimeInMillis());
	}

	public void setScheduledCompletion(Calendar when) {
		Calendar cal = Calendar.getInstance();
		long daysLeft = (when.getTimeInMillis() - cal.getTimeInMillis()) / 1000*60*60*24;
		
		setScheduledForToday((int) (daysLeft <= 0 ? 0 : (totalCount - currentCount) / daysLeft));
	}
	
	public int getScheduledForToday() {
		return scheduledForToday;
	}

	public void setScheduledForToday(int scheduledForToday) {
		this.scheduledForToday = scheduledForToday;
	}
}