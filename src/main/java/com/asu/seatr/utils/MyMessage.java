package com.asu.seatr.utils;

public enum MyMessage {
	STUDENT_NOT_FOUND("student not found"),
	STUDENT_CREATED("student created"),
	STUDENT_ANALYZER_DELETED("student analyzer record deleted"),
	STUDENT_DELETED("student deleted"),
	BAD_REQUEST("bad request"), 
	STUDENT_UPDATED("student updated"), 
	STUDENT_ALREADY_PRESENT("student already present");
	
	public String value;
	
	private MyMessage(String value) {
		this.value = value;
	}	
	
	public String getValue() {
		return this.value;
	}
}
