package com.asu.seatr.rest.models.analyzer1;

import com.asu.seatr.rest.models.interfaces.TKAReaderI;

public class TKAReader1 implements TKAReaderI{

	private String external_kc_id;
	private String external_task_id;
	private Integer min_mastery_level;
	
	@Override
	public String getExternal_kc_id() {
		// TODO Auto-generated method stub
		return external_kc_id;
	}


	@Override
	public void setExternal_kc_id(String external_kc_id) {
		// TODO Auto-generated method stub
		this.external_kc_id = external_kc_id;
	}


	@Override
	public String getExternal_task_id() {
		// TODO Auto-generated method stub
		return external_task_id;
	}

	@Override
	public void setExternal_task_id(String external_task_id) {
		// TODO Auto-generated method stub
		this.external_task_id = external_task_id;
	}
	
	public Integer getMin_mastery_level() {
		return min_mastery_level;
	}

	public void setMin_mastery_level(Integer min_mastery_level) {
		this.min_mastery_level = min_mastery_level;
	}

}
