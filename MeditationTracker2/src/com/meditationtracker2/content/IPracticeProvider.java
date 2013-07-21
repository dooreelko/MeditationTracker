package com.meditationtracker2.content;

import java.util.List;

public interface IPracticeProvider {

	public abstract List<Practice> getPractices();

	public abstract Practice getPractice(int id);

	public abstract void savePractice(Practice practice);
}