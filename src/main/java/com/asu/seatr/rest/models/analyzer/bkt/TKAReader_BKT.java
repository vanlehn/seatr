package com.asu.seatr.rest.models.analyzer.bkt;

import java.io.Serializable;

import com.asu.seatr.rest.models.interfaces.TKAReaderI;

@SuppressWarnings("serial")
public class TKAReader_BKT implements TKAReaderI,Serializable {

	private String external_kc_id;
	private String external_task_id;
	
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
	

}
