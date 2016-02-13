package com.asu.seatr.rest.models;

public class TAReader1 implements TaskReaderI{

	private String external_task_id;
	private Integer external_course_id;
	private Integer s_difficulty_level;
	
	public String getExternal_task_id() {
		return external_task_id;
	}
	
	public void setExternal_task_id(String external_task_id) {
		this.external_task_id = external_task_id;
	}
	
	public Integer getExternal_course_id() {
		return external_course_id;
	}
	
	public void setExternal_course_id(Integer external_course_id) {
		this.external_course_id = external_course_id;
	}
	
	public Integer getS_difficulty_level() {
		return s_difficulty_level;
	}
	
	public void setS_difficulty_level(Integer s_difficulty_level) {
		this.s_difficulty_level = s_difficulty_level;
	}
}
