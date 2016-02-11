package com.asu.seatr.models;

public class CourseAnalyzer {
	private int id;
	private int course_id;
	private int analyzer_id;
	private boolean active;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCourse_id() {
		return course_id;
	}
	public void setCourse_id(int course_id) {
		this.course_id = course_id;
	}
	public int getAnalyzer_id() {
		return analyzer_id;
	}
	public void setAnalyzer_id(int analyzer_id) {
		this.analyzer_id = analyzer_id;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}	
}
