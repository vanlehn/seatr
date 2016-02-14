package com.asu.seatr.rest.models;

public interface RecommenderReaderI {
	public String getExternal_student_id();
	public void setExternal_student_id(String external_student_id);
	public Integer getExternal_course_id();
	public void setExternal_course_id(Integer external_course_id);
	public Integer getNumber_of_tasks();
	public void setNumber_of_tasks(Integer number_of_tasks);
}
