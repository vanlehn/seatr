package com.asu.seatr.rest.models.analyzer2;

import com.asu.seatr.rest.models.interfaces.KAReaderI;

public class KAReader2 implements KAReaderI{

	
	private String external_kc_id;
	private String external_course_id;

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
	public String getExternal_course_id() {
		// TODO Auto-generated method stub
		return external_course_id;
	}

	@Override
	public void setExternal_course_id(String external_course_id) {
		// TODO Auto-generated method stub
		this.external_course_id = external_course_id;
	}

}
