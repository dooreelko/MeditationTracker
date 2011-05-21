package com.meditationtracker.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "Practices")
public class Practice {
	@Id
	@GeneratedValue
	private int id;

	@Column(name="ISNGONDRO", nullable = false, columnDefinition = "default false")
	private Boolean isNgondro;
	
	@Column(name="SORT_ORDER", nullable = false)
	private int sortOrder;
	
	@Column(name="TITLE", nullable = false)
	private String title;

	@Column(name="ICONURL", nullable = false)
	private String iconURL;
	
	@Column(name="THUMBURL", nullable = false)
	private String thumbURL;
	
	@Column(name="TOTALCOUNT", nullable = false, columnDefinition = "default 111111")
	private int totalCount;
	
//	@Column(name="SCHEDULEDCOMPLETION")
	@Column(name="SCHEDULEDCOUNT", nullable = false)
	private int scheduleCount;
	
	@Column(name="MALASIZE", nullable = false, columnDefinition = "default 0")
	private int malaSize;

	/*@OneToMany
	@JoinColumn(name="PRACTICE_ID")
	private Collection<PracticeHistory> history;*/
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Boolean getIsNgondro() {
		return isNgondro;
	}

	public void setIsNgondro(Boolean isNgondro) {
		this.isNgondro = isNgondro;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIconURL() {
		return iconURL;
	}

	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}

	public String getThumbURL() {
		return thumbURL;
	}

	public void setThumbURL(String thumbURL) {
		this.thumbURL = thumbURL;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getScheduleCount() {
		return scheduleCount;
	}

	public void setScheduleCount(int scheduleCount) {
		this.scheduleCount = scheduleCount;
	}

	public int getMalaSize() {
		return malaSize;
	}

	public void setMalaSize(int malaSize) {
		this.malaSize = malaSize;
	}
	
	

}
