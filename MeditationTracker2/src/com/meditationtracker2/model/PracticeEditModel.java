package com.meditationtracker2.model;

import java.util.Date;

import android.net.Uri;

import com.meditationtracker2.R;
import com.meditationtracker2.content.Practice;

import doo.bundle.annotations.Bind;

public class PracticeEditModel extends BaseModel<Practice> {
	private Uri imageUri;
	private String title;
	private int totalCount;
	private int currentCount;
	private int scheduledForToday;
	private Date scheduledCompletion;

	public PracticeEditModel(Practice originalModel) {
		super(originalModel);

		imageUri = Uri.parse(originalModel.imageUrl);
		title = originalModel.title;
		totalCount = originalModel.totalCount;
		currentCount = originalModel.currentCount;
		scheduledForToday = originalModel.getScheduledForToday();
		scheduledCompletion = originalModel.getScheduledCompletion();
	}

	@Bind({ R.id.practice_image, R.id.buttonPracticeImage })
	public Uri getImageUri() {
		return imageUri;
	}
	
	public void setImageUri(Uri imageUri) {
		this.imageUri = imageUri;
	}

	@Bind(R.id.editPracticeName)
	public String getTitle() {
		return title;
	}

	@Bind(R.id.editPracticeName)
	public void setTitle(String title) {
		this.title = title;
	}

	@Bind(R.id.editPracticeTotal)
	public int getTotalCount() {
		return totalCount;
	}

	@Bind(R.id.editPracticeTotal)
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	@Bind(R.id.editPracticeCompletedCount)
	public int getCurrentCount() {
		return currentCount;
	}

	@Bind(R.id.editPracticeCompletedCount)
	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
	}

	@Bind(R.id.editScheduledPerSession)
	public int getScheduledForToday() {
		return scheduledForToday;
	}

	@Bind(R.id.editScheduledPerSession)
	public void setScheduledForToday(int scheduledForToday) {
		this.scheduledForToday = scheduledForToday;
	}

	@Bind(R.id.datePickerScheduledEnd)
	public Date getScheduledCompletion() {
		return scheduledCompletion;
	}

	@Bind(R.id.datePickerScheduledEnd)
	public void setScheduledCompletion(Date scheduledCompletion) {
		this.scheduledCompletion = scheduledCompletion;
	}
}
