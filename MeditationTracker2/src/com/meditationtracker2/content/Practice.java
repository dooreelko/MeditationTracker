package com.meditationtracker2.content;

import java.util.Date;

public class Practice {
    public int id;
    public String title;
	public String imageUrl = "content://com.meditationtracker2.images/sixteenth_karmapa";
	public int totalCount = 111111;
	public int currentCount;
	public int scheduledForToday;
	public int completedToday;
	public Date lastPracticeDate;
	public int malaSize = 108;

    public Practice(int id, String title, int totalCount, int currentCount, int resId) {
        this.id = id;
        this.title = title;
		this.totalCount = totalCount;
		this.currentCount = currentCount;
    }

	public Practice(int id, String title, int imageResId, String imageUrl,
			int totalCount, int currentCount, int scheduledForToday,
			int completedToday, Date lastPracticeDate) {
		super();
		this.id = id;
		this.title = title;
		this.imageUrl = imageUrl;
		this.totalCount = totalCount;
		this.currentCount = currentCount;
		this.scheduledForToday = scheduledForToday;
		this.completedToday = completedToday;
		this.lastPracticeDate = lastPracticeDate;
	}

	public Practice() {
	}

	@Override
    public String toString() {
        return title;
    }

	public void addSession(int totalCount2) {
		// TODO Auto-generated method stub
		
	}
}