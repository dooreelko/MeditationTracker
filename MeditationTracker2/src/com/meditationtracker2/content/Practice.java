package com.meditationtracker2.content;

public class Practice {
    public int id;
    public String title;
	public final int imageResId;
	public final int totalCount;
	public final int currentCount;

    public Practice(int id, String title, int totalCount, int currentCount, int resId) {
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