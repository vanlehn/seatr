package com.asu.seatr.rest.models.analyzer.bkt;

import java.io.Serializable;

import com.asu.seatr.rest.models.interfaces.TKReaderI;

@SuppressWarnings("serial")
public class TKReader_BKT implements TKReaderI,Serializable {

	private boolean replace;
	private String external_course_id;
	private TKAReader_BKT[] tkaReader;
	@Override
	public boolean getReplace() {
		return replace;
	}

	@Override
	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	public TKAReader_BKT[] getTkaReader() {
		return tkaReader;
	}
	
	public void setTkaReader(TKAReader_BKT[] tkaReader) {
		this.tkaReader =  tkaReader;
	}
	
	public String getExternal_course_id() {
		return external_course_id;
	}

	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
	}

}
