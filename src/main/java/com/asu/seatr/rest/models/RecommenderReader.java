package com.asu.seatr.rest.models;

public class RecommenderReader implements RecommenderReaderI{

	private String external_student_id;
	private Integer external_course_id;
	private Integer number_of_tasks;
	@Override
	public String getExternal_student_id() {
		// TODO Auto-generated method stub
		return external_student_id;
	}

	@Override
	public void setExternal_student_id(String external_student_id) {
		// TODO Auto-generated method stub
		this.external_student_id = external_student_id;
	}

	@Override
	public Integer getExternal_course_id() {
		// TODO Auto-generated method stub
		return external_course_id;
	}

	@Override
	public void setExternal_course_id(Integer external_course_id) {
		// TODO Auto-generated method stub
		this.external_course_id = external_course_id;
	}

	@Override
	public Integer getNumber_of_tasks() {
		// TODO Auto-generated method stub
		return number_of_tasks;
	}

	@Override
	public void setNumber_of_tasks(Integer number_of_tasks) {
		// TODO Auto-generated method stub
		this.number_of_tasks = number_of_tasks;
	}

}
