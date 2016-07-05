package com.asu.seatr.rest.models.analyzer.n_in_a_row;

import com.asu.seatr.rest.models.interfaces.KAReaderI;

public class KAReader_N_In_A_Row implements KAReaderI{

	
	private String external_kc_id;
	private String external_course_id;

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
