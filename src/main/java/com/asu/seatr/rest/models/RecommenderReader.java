package com.asu.seatr.rest.models;



public class RecommenderReader{

	private String external_student_id;
	private String external_course_id;
	private Integer number_of_tasks;

	public String getExternal_student_id() {
		// TODO Auto-generated method stub
		return external_student_id;
	}


	public void setExternal_student_id(String external_student_id) {
		// TODO Auto-generated method stub
		this.external_student_id = external_student_id;
	}


	public String getExternal_course_id() {
		// TODO Auto-generated method stub
		return external_course_id;
	}


	public void setExternal_course_id(String external_course_id) {
		// TODO Auto-generated method stub
		this.external_course_id = external_course_id;
	}


	public Integer getNumber_of_tasks() {
		// TODO Auto-generated method stub
		return number_of_tasks;
	}


	public void setNumber_of_tasks(Integer number_of_tasks) {
		// TODO Auto-generated method stub
		this.number_of_tasks = number_of_tasks;
	}

}
