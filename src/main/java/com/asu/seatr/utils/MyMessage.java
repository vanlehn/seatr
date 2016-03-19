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
	COURSE_ANALYZER_NOT_FOUND("course analyzer is not found"),
	COURSE_CREATED("course created"),
	COURSE_ANALYZER_DELETED("course analyzer record deleted"),
	COURSE_DELETED("courese deleted"),
	COURSE_UPDATED("course updated"),
	COURSE_ALREADY_PRESENT("course already present"),
	TASK_NOT_FOUND("task not found"),
	TASK_CREATED("task created"),
	TASK_ANALYZER_DELETED("task analyzer record deleted"),
	TASK_DELETED("task deleted"),
	TASK_UPDATED("task updated"),
	TASK_ALREADY_PRESENT("task already present"),
	STUDENT_TASK_NOT_FOUND("student_task not found"),
	STUDENT_TASK_CREATED("student_task created"),
	STUDENT_TASK_ANALYZER_DELETED("student_task analyzer record deleted"),
	STUDENT_TASK_DELETED("student_task deleted"),
	STUDENT_TASK_UPDATED("student_task updated"),
	STUDENT_TASK_ALREADY_PRESENT("student_task already present"),
	NO_TASK_PRESENT_FOR_COURSE("no tasks present for given course"),
	KC_NOT_FOUND("knowledge component not found"),
	NO_TASK_PRESENT("no tasks present for given course"), 
	STUDENT_ANALYZER_NOT_FOUND("student analyzer not found for the given data"), 
	STUDENT_PROPERTY_NULL("student property is null"), 
	COURSE_PROPERTY_NULL("course property is null"),
	NO_ANALYZER_FOR_KC("no analyzer record found for given kc"),
	KC_NOT_FOUND_FOR_TASK("no knowledge componenent found for given task"),
	KC_PROPERTY_NULL("kc property is null"),
	KC_CREATED("kc created"),
	TASK_PROPERTY_NULL("Task property is null"),
	STUDENT_TASK_PROPERTY_NULL("Student Task property is null"),
	KC_ALREADY_PRESENT("Knowledge Component already present"),
	KC_TASK_MAP_ALREADY_PRESENT("kc task map already present"),
	KC_TASK_CREATED("KC task map created"), 
	COURSE_ANALYZER_ALREADY_PRESENT("course analyzer already present"),
	STUDENT_ANALYZER_ALREADY_PRESENT("student analyzer already present"),
	COURSE_ANALYZER_UPDATED("course analyzer mapping is updated."),
	TASK_ANALYZER_NOT_FOUND("task analyzer not found"),
	TASK_ANALYZER_ALREADY_PRESENT("task analyzer already present"),
	ANALYZER_NOT_FOUND("The specified analyzer is not found."), 
	COURSE_ANALYZER_MAP_NOT_FOUND("course analyzer mapping not found"), 
	KC_NOT_FOUND_FOR_COURSE("no knowledge component found for the given course"), 
	KC_TASK_MAP_COPIED("knowledge component - task mapping copied"), 
	USER_NOT_FOUND("username not found"),
	USER_NO_ACCESS_TO_COURSE("user does not have access to the course");
	
	public String value;
	
	private MyMessage(String value) {
		this.value = value;
	}	
	
	public String getValue() {
		return this.value;
	}
}
