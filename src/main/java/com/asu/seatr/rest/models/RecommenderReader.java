package com.asu.seatr.rest.models;



public class RecommenderReader{

	private String external_student_id;
	private String external_course_id;
	private Integer number_of_tasks;

	public String getExternal_student_id() {
		return external_student_id;
	}


	public void setExternal_student_id(String external_student_id) {
		this.external_student_id = external_student_id;
	}


	public String getExternal_course_id() {
		return external_course_id;
	}


	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
	}


	public Integer getNumber_of_tasks() {
		return number_of_tasks;
	}


	public void setNumber_of_tasks(Integer number_of_tasks) {
		this.number_of_tasks = number_of_tasks;
	}

}
