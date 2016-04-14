package com.asu.seatr.rest.models;

import com.asu.seatr.rest.models.interfaces.TKAReaderI;
import com.asu.seatr.rest.models.interfaces.TKReaderI;

public class TKReader1 implements TKReaderI{

	private boolean replace;
	private String external_course_id;
	private String external_kc_id;
	private String external_task_id;
	private Integer min_mastery_level;
	

	@Override
	public boolean getReplace() {
		// TODO Auto-generated method stub
		return replace;
	}

	@Override
	public void setReplace(boolean replace) {
		// TODO Auto-generated method stub
		this.replace = replace;
	}

	@Override
	public String getExternal_course_id() {
		return external_course_id;
	}

	@Override
	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
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
	public String getExternal_task_id() {
		return external_task_id;
	}

	@Override
	public void setExternal_task_id(String external_task_id) {
		this.external_task_id = external_task_id;
	}
	
	public Integer getMin_mastery_level() {
		return min_mastery_level;
	}

	
	public void setMin_mastery_level(Integer min_mastery_level) {
		this.min_mastery_level = min_mastery_level;
	}	

}
