package com.asu.seatr.rest.models;

public class TKCAReader {

	private Boolean replace;
	private String external_course_id;
	private Integer from_analyzer_id;
	private Integer to_analyzer_id;
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
	public Integer getFrom_analyzer_id() {
		return from_analyzer_id;
	}
	public void setFrom_analyzer_id(Integer from_analyzer_id) {
		this.from_analyzer_id = from_analyzer_id;
	}
	public Integer getTo_analyzer_id() {
		return to_analyzer_id;
	}
	public void setTo_analyzer_id(Integer to_analyzer_id) {
		this.to_analyzer_id = to_analyzer_id;
	}

}
