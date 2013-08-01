package com.meditationtracker2.model;

import java.util.Calendar;
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

	public void updatePractice(Practice practice) {
		practice.imageUrl = imageUri.toString();
		practice.title = title;
		practice.totalCount = totalCount;
		practice.currentCount = currentCount;
		practice.setScheduledForToday(scheduledForToday);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(scheduledCompletion);
		practice.setScheduledCompletion(cal); //TODO: there's no need for setScheduledCompletion, just getter
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
		
		recalculateScheduledEnd();
	}

	private void recalculateScheduledEnd() {
		Calendar cal = Calendar.getInstance();
		if (scheduledForToday != 0) {
			cal.add(Calendar.DAY_OF_YEAR, (totalCount - currentCount) / scheduledForToday);
			scheduledCompletion = new Date(cal.getTimeInMillis());
		}
	}

	@Bind(R.id.editPracticeCompletedCount)
	public int getCurrentCount() {
		return currentCount;
	}

	@Bind(R.id.editPracticeCompletedCount)
	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
		
		recalculateScheduledEnd();
	}

	@Bind(R.id.editScheduledPerSession)
	public int getScheduledForToday() {
		return scheduledForToday;
	}

	@Bind(R.id.editScheduledPerSession)
	public void setScheduledForToday(int scheduledForToday) {
		this.scheduledForToday = scheduledForToday;
		
		recalculateScheduledEnd();
	}

	@Bind(R.id.datePickerScheduledEnd)
	public Date getScheduledCompletion() {
		return scheduledCompletion;
	}

	@Bind(R.id.datePickerScheduledEnd)
	public void setScheduledCompletion(Date scheduledCompletion) {
		this.scheduledCompletion = scheduledCompletion;

		Calendar cal = Calendar.getInstance();
		long daysLeft = (scheduledCompletion.getTime() - cal.getTimeInMillis()) / (1000*60*60*24);
		
		scheduledForToday = (int) (daysLeft <= 0 ? 0 : (totalCount - currentCount) / daysLeft);
	}

}
