package com.asu.seatr.rest.models.analyzer.unansweredtasks;

import com.asu.seatr.rest.models.interfaces.KAReaderI;

public class KAReader_UnansweredTasks implements KAReaderI{


	private String external_kc_id;
	private String external_course_id;
	private Integer s_unit;

	public Integer getS_unit() {
		return s_unit;
	}

	public void setS_unit(Integer s_unit) {
		this.s_unit = s_unit;
	}

	@Override
	public String getExternal_kc_id() {
		return external_kc_id;
	}

	@Override
	public void setExternal_kc_id(String external_kc_id) {
		this.external_kc_id = external_kc_id;
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
