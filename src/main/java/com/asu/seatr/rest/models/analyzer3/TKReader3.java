package com.asu.seatr.rest.models.analyzer3;

import com.asu.seatr.rest.models.interfaces.TKReaderI;

public class TKReader3 implements TKReaderI{

	private boolean replace;
	private String external_course_id;
	private TKAReader3[] tkaReader;
	@Override
	public boolean getReplace() {
		return replace;
	}

	@Override
	public void setReplace(boolean replace) {
		this.replace = replace;
	}


	public TKAReader3[] getTkaReader() {
		return tkaReader;
	}



	public void setTkaReader(TKAReader3[] tkaReader) {

		this.tkaReader = tkaReader;
	}

	public String getExternal_course_id() {
		return external_course_id;
	}

	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
	}

}
