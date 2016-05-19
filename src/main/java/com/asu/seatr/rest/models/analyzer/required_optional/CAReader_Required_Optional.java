package com.asu.seatr.rest.models.analyzer.required_optional;

public class CAReader_Required_Optional {
	private String external_course_id;
	private String description;
	private Integer s_units;
	private Integer d_current_unit_no;
	private Integer d_max_n;

	public String getExternal_course_id() {
		return external_course_id;
	}
	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
	}	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getS_units() {
		return s_units;
	}
	public void setS_units(Integer s_units) {
		this.s_units = s_units;
	}
	public Integer getD_current_unit_no() {
		return d_current_unit_no;
	}
	public void setD_current_unit_no(Integer d_current_unit_no) {
		this.d_current_unit_no = d_current_unit_no;
	}
	public Integer getD_max_n() {
		return d_max_n;
	}
	public void setD_max_n(Integer d_max_n) {
		this.d_max_n = d_max_n;
	}

}
