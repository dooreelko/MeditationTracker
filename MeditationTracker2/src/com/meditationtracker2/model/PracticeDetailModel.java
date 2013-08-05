package com.meditationtracker2.model;

import java.util.Calendar;
import java.util.Date;

import com.meditationtracker2.R;
import com.meditationtracker2.content.Practice;

import doo.bandera.ModelNormalizer.ViewState;
import doo.bandera.annotations.BindModel;
import doo.bandera.annotations.BindState;

public class PracticeDetailModel extends BaseModel<Practice> {

	public PracticeDetailModel(Practice originalModel) {
		super(originalModel);
	}

	@BindModel({ R.id.practice_image, R.id.buttonStart })
	public String getPracticeImage() {
		return getModel().imageUrl;
	}

	@BindModel(R.id.textScheduledForToday)
	public int getScheduledToday() {
		return getModel().scheduledForToday;
	}

	@BindModel(R.id.textCompletedToday)
	public int getCompletedToday() {
		return getModel().completedToday;
	}

	@BindModel(R.id.textLastPracticeDate)
	public Date getLastPracticeDate() {
		return getModel().lastPracticeDate;
	}

	@BindModel(R.id.textScheduledCompletion)
	public Date getScheduledCompletionDate() {
		Calendar cal = Calendar.getInstance();
		if (getModel().scheduledForToday != 0) {
			cal.add(Calendar.DAY_OF_YEAR, getModel().totalCount / getModel().scheduledForToday + 1);
		}
		
		return new Date(cal.getTimeInMillis());
	}

	@BindModel(R.id.textCurrentCount)
	public int getCurrentCount() {
		return getModel().currentCount;
	}

	@BindModel(R.id.textTotalCount)
	public int getTotalCount() {
		return getModel().totalCount;
	}

	@BindModel(R.id.progressBarPractice)
	public int getProgressPercent() {
		int totalCount = getModel().totalCount;
		return totalCount != 0 ? getModel().currentCount * 100 / totalCount : 0;
	}

	@BindState({ R.id.textOf, 
				 R.id.textTotalCount, 
				 R.id.progressBarPractice })
	public ViewState getTotalCountVisibility() {
		return getModel().totalCount > 0 ? ViewState.Normal : ViewState.Invisible;
	}

	@BindState({ R.id.textScheduledForToday, R.id.titleScheduledForToday })
	public ViewState getScheduledVisibility() {
		return hasSchedule() ? ViewState.Normal : ViewState.Gone;
	}

	protected boolean hasSchedule() {
		return getModel().totalCount > 0 && getModel().scheduledForToday > 0;
	}

	@BindState({ R.id.textScheduledCompletion, R.id.titleScheduledCompletion })
	public ViewState getScheduledCompletionVisibility() {
		return hasSchedule() ? ViewState.Normal : ViewState.Invisible;
	}
	
	@BindState({ R.id.textCompletedToday, R.id.titleCompletedToday })
	public ViewState getCompletedTodayVisibility() {
		return getModel().completedToday > 0 ? ViewState.Normal : ViewState.Invisible;
	}
	
	@BindState({ R.id.textLastPracticeDate, R.id.titleLastPractice })
	public ViewState getLastPracticeVisibility() {
		// like if it's at least 5 milliseconds after the Epoch
		return getModel().lastPracticeDate.getTime() > 5 ? ViewState.Normal : ViewState.Gone;
	}
	
}