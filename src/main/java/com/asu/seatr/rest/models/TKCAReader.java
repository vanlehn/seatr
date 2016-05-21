package com.asu.seatr.rest.models;

public class TKCAReader {

	private Boolean replace;
	private String external_course_id;
	private String from_analyzer;
	private String to_analyzer;
	public Boolean getReplace() {
		return replace;
	}
	public void setReplace(Boolean replace) {
		this.replace = replace;
	}
	public String getExternal_course_id() {
		return external_course_id;
	}
	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
	}
	public String getFrom_analyzer() {
		return from_analyzer;
	}
	public void setFrom_analyzer(String from_analyzer) {
		this.from_analyzer = from_analyzer;
	}
	public String getTo_analyzer() {
		return to_analyzer;
	}
	public void setTo_analyzer(String to_analyzer) {
		this.to_analyzer = to_analyzer;
	}


}
