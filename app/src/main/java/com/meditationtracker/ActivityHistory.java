package com.meditationtracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;

public class ActivityHistory
{
	private static final int MAX_SIZE = 50;
	private final List<String> history = new ArrayList<String>();
	
	
	
	public void logOnCreate(Bundle savedInstanceState, Intent intent) {
		addEntry(String.format("OnCreate {%s %s}", savedInstanceState!=null ? savedInstanceState.toString() : "<no_saved_state>", intent != null ? intent.toString() : "<no_intent>"));
		
	}
	
	public void logOnActivityResult(int requestCode, int resultCode, Intent data) {
		addEntry(String.format("OnActivityResult {%d %d %s}", requestCode, resultCode, data!=null ? data.toString() : "<no_intent>"));
	}
	
	public String getLog() {
		StringBuilder sb = new StringBuilder();
		for (String s : history) {
			sb.append(s);
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	private void addEntry(String entry) {
		history.add(DateFormat.format("hh:mm:ss", Calendar.getInstance().getTime()) + ": " + entry);
		if (history.size() > MAX_SIZE){
			history.remove(0);
		}
	}
}
