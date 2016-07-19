package com.asu.seatr.rest.models.analyzer.bkt;

import com.asu.seatr.rest.models.interfaces.KAReaderI;

public class KAReader_BKT implements KAReaderI{

	
	private String external_kc_id;
	private String external_course_id;
	private Integer utility;
	private Double init_p;
	private Double learning_rate;

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

	public Integer getUtility() {
		return utility;
	}

	public void setUtility(Integer utility) {
		this.utility = utility;
	}

	public Double getInit_p() {
		return init_p;
	}

	public void setInit_p(Double init_p) {
		this.init_p = init_p;
	}

	public Double getLearning_rate() {
		return learning_rate;
	}

	public void setLearning_rate(Double learning_rate) {
		this.learning_rate = learning_rate;
	}
	
	

}
