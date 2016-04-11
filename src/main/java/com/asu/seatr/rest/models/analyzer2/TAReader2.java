package com.asu.seatr.rest.models.analyzer2;

import com.asu.seatr.rest.models.interfaces.TaskReaderI;

public class TAReader2 implements TaskReaderI{

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
