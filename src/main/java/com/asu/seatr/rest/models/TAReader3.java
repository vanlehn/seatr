package com.asu.seatr.rest.models;

import com.asu.seatr.rest.models.interfaces.TaskReaderI;

public class TAReader3 implements TaskReaderI{

	private String external_task_id;
	private String external_course_id;
	private Integer s_unit_no;
	private Integer s_sequence_no;
	private Boolean s_is_required;

	@Override
	public String getExternal_task_id() {
		return external_task_id;
	}
	@Override
	public void setExternal_task_id(String external_task_id) {
		this.external_task_id = external_task_id;
	}
	@Override
	public String getExternal_course_id() {
		return external_course_id;
	}
	@Override
	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
	}
	public Integer getS_unit_no() {
		return s_unit_no;
	}
	public void setS_unit_no(Integer s_unit_no) {
		this.s_unit_no = s_unit_no;
	}
	public Integer getS_sequence_no() {
		return s_sequence_no;
	}
	public void setS_sequence_no(Integer s_sequence_no) {
		this.s_sequence_no = s_sequence_no;
	}
	public Boolean getS_is_required() {
		return s_is_required;
	}
	public void setS_is_required(Boolean s_is_required) {
		this.s_is_required = s_is_required;
	}


}
