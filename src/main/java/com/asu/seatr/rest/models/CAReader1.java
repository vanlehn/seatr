package com.asu.seatr.rest.models;

public class CAReader1 {
	private String external_course_id;
	private String description;
	private Double threshold;
	private String teaching_unit;
	
	public String getExternal_course_id() {
		return external_course_id;
	}
	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
	}
	public Double getThreshold() {
		return threshold;
	}
	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}
	public String getTeaching_unit() {
		return teaching_unit;
	}
	public void setTeaching_unit(String teaching_unit) {
		this.teaching_unit = teaching_unit;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
