package com.asu.seatr.rest.models.analyzer2;

import com.asu.seatr.rest.models.interfaces.SAReaderI;

public class SAReader2 implements SAReaderI {
	
	private String external_student_id;
	private String external_course_id;
	
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
	

}
