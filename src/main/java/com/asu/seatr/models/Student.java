package com.asu.seatr.models;

public class Student {
	private int id;
	private int external_id;
	private int course_id;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getExternal_id() {
		return external_id;
	}
	public void setExternal_id(int external_id) {
		this.external_id = external_id;
	}
	public int getCourse_id() {
		return course_id;
	}
	public void setCourse_id(int course_id) {
		this.course_id = course_id;
	}		
}
