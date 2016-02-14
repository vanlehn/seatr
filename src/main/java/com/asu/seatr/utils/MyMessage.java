package com.asu.seatr.utils;

public enum MyMessage {
	STUDENT_NOT_FOUND("student not found"),
	STUDENT_CREATED("student created"),
	STUDENT_ANALYZER_DELETED("student analyzer record deleted"),
	STUDENT_DELETED("student deleted"),
	BAD_REQUEST("bad request"), 
	STUDENT_UPDATED("student updated"), 
	STUDENT_ALREADY_PRESENT("student already present"),
	COURSE_NOT_FOUND("course not found"),
	COURSE_CREATED("course created"),
	COURSE_DELETED("courese deleted"),
	COURSE_UPDATED("course updated"),
	COURSE_ALREADY_PRESENT("course already present"),
	TASK_NOT_FOUND("task not found"),
	TASK_CREATED("task created"),
	TASK_DELETED("task deleted"),
	TASK_UPDATED("task updated"),
	TASK_ALREADY_PRESENT("student_task already present"),
	STUDENT_TASK_NOT_FOUND("student_task not found"),
	STUDENT_TASK_CREATED("student_task created"),
	STUDENT_TASK_DELETED("student_task deleted"),
	STUDENT_TASK_UPDATED("student_task updated"),
	STUDENT_TASK_ALREADY_PRESENT("student_task already present");
	
	public String value;
	
	private MyMessage(String value) {
		this.value = value;
	}	
	
	public String getValue() {
		return this.value;
	}
}
