package com.asu.seatr.rest.models;

public class SAReader1 implements StudentReaderI {
	
	private String external_student_id;
	private Integer external_course_id;
	private Double s_placement_score;
	private String s_year;
	
	public String getExternal_student_id() {
		return external_student_id;
	}
	public void setExternal_student_id(String external_student_id) {
		this.external_student_id = external_student_id;
	}
	public Integer getExternal_course_id() {
		return external_course_id;
	}
	public void setExternal_course_id(Integer external_course_id) {
		this.external_course_id = external_course_id;
	}
	public Double getS_placement_score() {
		return s_placement_score;
	}
	public void setS_placement_score(Double s_placement_score) {
		this.s_placement_score = s_placement_score;
	}
	public String getS_year() {
		return s_year;
	}
	public void setS_year(String s_year) {
		this.s_year = s_year;
	}
	

}