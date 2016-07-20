package com.asu.seatr.rest.models.analyzer.bkt;

import com.asu.seatr.rest.models.interfaces.TaskReaderI;

public class TAReader_BKT implements TaskReaderI{

	private String external_task_id;
	private String external_course_id;
	private String type;
	private double difficulty;
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(double difficulty) {
		this.difficulty = difficulty;
	}
	
	
	
}
