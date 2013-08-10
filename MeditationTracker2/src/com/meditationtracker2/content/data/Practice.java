package com.meditationtracker2.content.data;

import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Practices")
public class Practice {
	
/* _id INTEGER PRIMARY KEY, 
 * ISNGONDRO BOOLEAN NOT NULL DEFAULT 0, 
 * SORT_ORDER INTEGER NOT NULL, 
 * TITLE TEXT NOT NULL, 
 * ICONURL TEXT NOT NULL, 
 * THUMBURL TEXT NOT NULL, 
 * TOTALCOUNT INTEGER NOT NULL DEFAULT 111111, 
 * SCHEDULEDCOUNT INTEGER NOT NULL DEFAULT 0, 
 * MALASIZE INTEGER NOT NULL DEFAULT 0) 	
	*/

	@DatabaseField(generatedId = true, allowGeneratedIdInsert = true, columnName = "_id")
    public int id;
	
	@DatabaseField(canBeNull = false, defaultValue = "0", columnName = "ISNGONDRO")
	public boolean isNgondro;
	
	@DatabaseField(canBeNull = false, columnName = "TITLE")
    public String title;
	
	@DatabaseField(canBeNull = false, columnName = "ICONURL")
	public String imageUrl = "content://com.meditationtracker2.images/sixteenth_karmapa";
	
	@DatabaseField(canBeNull = false, columnName = "TOTALCOUNT", defaultValue = "111111")
	public long totalCount = 111111;

	@DatabaseField(canBeNull = false, columnName = "SCHEDULEDCOUNT", defaultValue = "0")
	public int scheduledForToday;
	
	@DatabaseField(canBeNull = false, columnName = "MALASIZE", defaultValue = "0")
	public int malaSize = 108;
	
	@ForeignCollectionField(eager = false)
	public ForeignCollection<PracticeHistory> history;
	
	public long currentCount;
	public long completedToday;
	public Date lastPracticeDate;

	public Practice() {
	}
	
	public Practice(int id, String title, int imageResId, String imageUrl,
			int totalCount, int currentCount, int scheduledForToday,
			int completedToday, Date lastPracticeDate) {
		this.id = id;
		this.title = title;
		this.imageUrl = imageUrl;
		this.totalCount = totalCount;
		this.currentCount = currentCount;
		this.scheduledForToday = scheduledForToday;
		this.completedToday = completedToday;
		this.lastPracticeDate = lastPracticeDate;
	}

	public Practice(boolean isNgondro, String title, String imageUrl, int totalCount) {
		this.isNgondro = isNgondro;
		this.title = title;
		this.imageUrl = imageUrl;
		this.totalCount = totalCount;
	}

	public Practice setId(int id) {
		this.id = id;
		return this;
	}
	
	@Override
	public String toString() {
		return title;
	}
}