package com.meditationtracker2.model;


public class BaseModel<T> {
	private T originalModel;

	public BaseModel() {
	}
	
	public BaseModel(T originalModel) {
		this.originalModel = originalModel;
	}

	public T getModel() {
		return originalModel;
	}

	public void setModel(T original) {
		this.originalModel = original;
	}

}
