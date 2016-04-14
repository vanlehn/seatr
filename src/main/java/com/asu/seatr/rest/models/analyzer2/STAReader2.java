package com.asu.seatr.rest.models.analyzer2;

import com.asu.seatr.rest.models.interfaces.STAReaderI;

public class STAReader2 implements STAReaderI {
	
	private String external_task_id;
	private String external_student_id;
	private String external_course_id;
	private String d_status; 
	
	@Override
	public String getExternal_task_id() {
		return external_task_id;
	}
	@Override
	public void setExternal_task_id(String external_task_id) {
		this.external_task_id = external_task_id;
	}
	@Override
	public String getExternal_student_id() {
		return external_student_id;
	}
	@Override
	public void setExternal_student_id(String external_student_id) {
		this.external_student_id = external_student_id;
	}
	@Override
	public String getExternal_course_id() {
		return external_course_id;
	}
	@Override
	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
	}
	public String getD_status() {
		return d_status;
	}
	public void setD_status(String d_status) {
		this.d_status = d_status;
	}
	
}
