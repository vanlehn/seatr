package com.asu.seatr.rest.models.analyzer1;

import com.asu.seatr.rest.models.interfaces.TaskReaderI;

public class TAReader1 implements TaskReaderI{

	private String external_task_id;
	private String external_course_id;
	private Integer s_difficulty_level;

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

	public Integer getS_difficulty_level() {
		return s_difficulty_level;
	}

	public void setS_difficulty_level(Integer s_difficulty_level) {
		this.s_difficulty_level = s_difficulty_level;
	}
}
