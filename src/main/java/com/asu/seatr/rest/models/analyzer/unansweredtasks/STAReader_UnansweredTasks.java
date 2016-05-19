package com.asu.seatr.rest.models.analyzer.unansweredtasks;

import com.asu.seatr.rest.models.interfaces.STAReaderI;

public class STAReader_UnansweredTasks implements STAReaderI {

	private String external_task_id;
	private String external_student_id;
	private String external_course_id;
	private String d_status; 
	private Integer d_time_lastattempt;

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
	public Integer getD_time_lastattempt() {
		return d_time_lastattempt;
	}
	public void setD_time_lastattempt(Integer d_time_lastattempt) {
		this.d_time_lastattempt = d_time_lastattempt;
	}

}
