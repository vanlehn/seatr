package com.asu.seatr.rest.models.analyzer2;

import com.asu.seatr.rest.models.interfaces.TKAReaderI;

public class TKAReader2 implements TKAReaderI{

	private String external_kc_id;
	private String external_task_id;
	
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
	

}
