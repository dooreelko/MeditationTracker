CREATE TABLE Practices (_ID INTEGER PRIMARY KEY, ISNGONDRO BOOLEAN, SORT_ORDER INTEGER, RESOURCEIDICON INTEGER, RESOURCEIDTITLE INTEGER, TITLE TEXT, ICONURL TEXT, TOTALCOUNT INTEGER, SCHEDULEDCOMPLETION INTEGER);

CREATE TABLE PracticeHistory (_ID INTEGER PRIMARY KEY, PRACTICE_ID INTEGER, PRACTICE_DATE DATETIME default CURRENT_TIMESTAMP, DONE_COUNT INTEGER);
