package com.meditationtracker2.model;

import java.util.Calendar;
import java.util.Date;

import android.net.Uri;

import com.meditationtracker2.R;
import com.meditationtracker2.content.data.Practice;

import doo.bandera.ModelNormalizer.ViewState;
import doo.bandera.annotations.BindModel;
import doo.bandera.annotations.BindState;

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
		scheduledForToday = originalModel.scheduledForToday;
		recalculateScheduledEnd();
	}

	public void updatePractice(Practice practice) {
		practice.imageUrl = imageUri.toString();
		practice.title = title;
		practice.totalCount = totalCount;
		practice.currentCount = currentCount;
		practice.scheduledForToday = scheduledForToday;
	}
	
	
	@BindModel({ R.id.practice_image, R.id.buttonPracticeImage })
	public Uri getImageUri() {
		return imageUri;
	}
	
	public void setImageUri(Uri imageUri) {
		this.imageUri = imageUri;
	}

	@BindModel(R.id.editPracticeName)
	public String getTitle() {
		return title;
	}

	@BindModel(R.id.editPracticeName)
	public void setTitle(String title) {
		this.title = title;
	}

	@BindModel(R.id.editPracticeTotalCount)
	public int getTotalCount() {
		return totalCount;
	}

	@BindModel(R.id.editPracticeTotalCount)
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

	@BindModel(R.id.editPracticeCompletedCount)
	public int getCurrentCount() {
		return currentCount;
	}

	@BindModel(R.id.editPracticeCompletedCount)
	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
		
		recalculateScheduledEnd();
	}

	@BindModel(R.id.editScheduledPerSession)
	public int getScheduledForToday() {
		return scheduledForToday;
	}

	@BindModel(R.id.editScheduledPerSession)
	public void setScheduledForToday(int scheduledForToday) {
		this.scheduledForToday = scheduledForToday;
		
		recalculateScheduledEnd();
	}

	@BindModel(R.id.datePickerScheduledEnd)
	public Date getScheduledCompletion() {
		return scheduledCompletion;
	}

	@BindState({R.id.datePickerScheduledEnd, R.id.textScheduledCompletion})
	public ViewState getScheduledCompletionVisibility() {
		return totalCount > 0 ? ViewState.Normal : ViewState.Gone;
	}
	
	
	@BindModel(R.id.datePickerScheduledEnd)
	public void setScheduledCompletion(Date scheduledCompletion) {
		this.scheduledCompletion = scheduledCompletion;

		Calendar cal = Calendar.getInstance();
		long daysLeft = (scheduledCompletion.getTime() - cal.getTimeInMillis()) / (1000*60*60*24);
		
		scheduledForToday = (int) (daysLeft <= 0 ? 0 : (totalCount - currentCount) / daysLeft);
	}

}
