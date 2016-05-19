package com.asu.seatr.rest.models.analyzer.required_optional;

import com.asu.seatr.rest.models.interfaces.TKReaderI;

public class TKReader_Required_Optional implements TKReaderI{

	private boolean replace;
	private String external_course_id;
	private TKAReader_Required_Optional[] tkaReader;
	@Override
	public boolean getReplace() {
		return replace;
	}

	@Override
	public void setReplace(boolean replace) {
		this.replace = replace;
	}


	public TKAReader_Required_Optional[] getTkaReader() {
		return tkaReader;
	}



	public void setTkaReader(TKAReader_Required_Optional[] tkaReader) {

		this.tkaReader = tkaReader;
	}

	public String getExternal_course_id() {
		return external_course_id;
	}

	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
	}

}
