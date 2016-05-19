package com.asu.seatr.rest.models.analyzer.n_in_a_row;

import com.asu.seatr.rest.models.interfaces.TaskReaderI;

public class TAReader_N_In_A_Row implements TaskReaderI{

	private String external_task_id;
	private String external_course_id;
	
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
	
}
