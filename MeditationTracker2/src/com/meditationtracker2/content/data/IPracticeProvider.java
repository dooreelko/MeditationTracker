package com.meditationtracker2.content.data;

import java.util.List;


public interface IPracticeProvider {

	public List<Practice> getPractices();

	public Practice getPractice(int id);

	public void savePractice(Practice practice);
	
	public void addSession(Practice practice, int count);

	public void deletePractice(Practice practice);
}