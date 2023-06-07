package com.meditationtracker.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "PracticeHistory")
public class PracticeHistory {
	@Id
	@GeneratedValue
	int id;

	@Column(name="PRACTICE_ID", nullable = false)
	private int practiceId;
	
	@Column(name="PRACTICE_DATE", nullable = false, columnDefinition = "default CURRENT_DATE")
	private Date practiceDate;

	@Column(name="DONE_COUNT", nullable = false)
	private int doneCount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPracticeId() {
		return practiceId;
	}

	public void setPracticeId(int practiceId) {
		this.practiceId = practiceId;
	}

	public Date getPracticeDate() {
		return practiceDate;
	}

	public void setPracticeDate(Date practiceDate) {
		this.practiceDate = practiceDate;
	}

	public int getDoneCount() {
		return doneCount;
	}

	public void setDoneCount(int doneCount) {
		this.doneCount = doneCount;
	}
}
