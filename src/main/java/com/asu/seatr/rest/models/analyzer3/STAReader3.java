package com.asu.seatr.rest.models.analyzer3;

import com.asu.seatr.rest.models.interfaces.STAReaderI;

public class STAReader3 implements STAReaderI {

	private String external_task_id;
	private String external_student_id;
	private String external_course_id;
	private Boolean d_is_answered;	
	//private Integer d_current_n;

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
	public Boolean getD_is_answered() {
		return d_is_answered;
	}
	public void setD_is_answered(Boolean d_is_answered) {
		this.d_is_answered = d_is_answered;
	}
	//	public Integer getD_current_n() {
	//		return d_current_n;
	//	}
	//	public void setD_current_n(Integer d_current_n) {
	//		this.d_current_n = d_current_n;
	//	}
}
