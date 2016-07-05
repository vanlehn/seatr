package com.asu.seatr.models;

import java.util.HashMap;

public class Student_KC {

	private String internal_student_id;
	private HashMap<String,Integer> kc_mastery = new HashMap<String,Integer>();
	
	public String getInternal_student_id() {
		return internal_student_id;
	}
	public void setInternal_student_id(String internal_student_id) {
		this.internal_student_id = internal_student_id;
	}
	public HashMap<String, Integer> getKc_mastery() {
		return kc_mastery;
	}
	public void setKc_mastery(HashMap<String, Integer> kc_mastery) {
		this.kc_mastery = kc_mastery;
	}
	
	
}
