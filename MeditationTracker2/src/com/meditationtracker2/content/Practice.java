package com.meditationtracker2.content;

import java.sql.Date;

public class Practice {
    public int id;
    public String title;
	public int imageResId;
	public String imageUrl;
	public int totalCount;
	public int currentCount;
	public int scheduledForToday;
	public int completedToday;
	public Date lastPracticeDate;
	public Date scheduledCompletion;
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
			int completedToday, Date lastPracticeDate, Date scheduledCompletion) {
		super();
		this.id = id;
		this.title = title;
		this.imageResId = imageResId;
		this.imageUrl = imageUrl;
		this.totalCount = totalCount;
		this.currentCount = currentCount;
		this.scheduledForToday = scheduledForToday;
		this.completedToday = completedToday;
		this.lastPracticeDate = lastPracticeDate;
		this.scheduledCompletion = scheduledCompletion;
	}




	@Override
    public String toString() {
        return title;
    }

	public void addSession(int totalCount2) {
		// TODO Auto-generated method stub
		
	}
}