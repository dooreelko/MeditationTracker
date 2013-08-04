package com.meditationtracker2.model;

import com.meditationtracker2.R;
import com.meditationtracker2.content.Practice;

import doo.bandera.ModelNormalizer.ViewState;
import doo.bandera.annotations.BindModel;
import doo.bandera.annotations.BindState;

public class PracticeDoModel extends BaseModel<Practice> {
	private int malaCount;
	private int malaSize;
	private int totalCount;

	private ViewState malaCountState = ViewState.NotSet;
	
	public PracticeDoModel(Practice originalModel) {
		super(originalModel);
		malaSize = originalModel.malaSize;
	}

	@BindModel(R.id.editMalaCount)
	public int getMalaCount() {
		return malaCount;
	}

	@BindModel(R.id.editMalaCount)
	public void setMalaCount(int malaCount) {
		this.malaCount = malaCount;
		recalculateTotal();
	}

	@BindModel(R.id.editMalaSize)
	public int getMalaSize() {
		return malaSize;
	}

	@BindModel(R.id.editMalaSize)
	public void setMalaSize(int malaSize) {
		this.malaSize = malaSize;
		recalculateTotal();
	}

	protected void recalculateTotal() {
		totalCount = malaSize*malaCount;
	}

	@BindModel(R.id.editSessionCompletedCount)
	public int getTotalCount() {
		return totalCount;
	}

	@BindModel(R.id.editSessionCompletedCount)
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		
		if (totalCount != malaCount*malaSize) {
			malaCountState = ViewState.ReadOnly;
		} else {
			malaCountState = ViewState.Normal;
		}
	}
	
	@BindState({R.id.editMalaCount, R.id.editMalaSize})
	public ViewState getMalaCountStates() {
		return malaCountState;
	}

	public void addMala() {
		malaCount++;
		recalculateTotal();
	}
}
