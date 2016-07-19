package com.asu.seatr.rest.models.analyzer.bkt;

import java.io.Serializable;

import com.asu.seatr.rest.models.interfaces.TKReaderI;

@SuppressWarnings("serial")
public class TLReader_BKT implements Serializable {

	private String external_course_id;
	private String external_student_id;
	private String[] external_task_ids;


	public String getExternal_student_id() {
		return external_student_id;
	}

	public void setExternal_student_id(String external_student_id) {
		this.external_student_id = external_student_id;
	}

	public String[] getExternal_task_ids() {
		return external_task_ids;
	}

	public void setExternal_task_ids(String[] external_task_ids) {
		this.external_task_ids = external_task_ids;
	}

	public String getExternal_course_id() {
		return external_course_id;
	}

	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
	}

}
