package com.asu.seatr.models.analyzers.kc;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.PropertyValueException;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.KnowledgeComponentHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.interfaces.KCAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

@Entity
@Table(name = "k_bkt")
public class KC_BKT implements KCAnalyzerI{
	
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	@ManyToOne
	@JoinColumn(name = "kc_id", referencedColumnName = "id", unique=true)
	private KnowledgeComponent kc;
	
	@Column(name = "utility")
	private int utility;
	
	@Column(name = "init_p")
	private double init_p;
	
	@Column(name = "learning_rate")
	private double learning_rate;
	
	@Override
	public int getId() {
		return id;
	}
	@Override
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public KnowledgeComponent getKc() {
		return kc;
	}
	@Override
	public void setKc(KnowledgeComponent kc) {
		this.kc = kc;
	}	
	public int getUtility() {
		return utility;
	}
	public void setUtility(int utility) {
		this.utility = utility;
	}
	public double getInit_p() {
		return init_p;
	}
	public void setInit_p(double init_p) {
		this.init_p = init_p;
	}
	
	public double getLearning_rate() {
		return learning_rate;
	}
	public void setLearning_rate(double learning_rate) {
		this.learning_rate = learning_rate;
	}
	public void createKC(String external_kc_id, String external_course_id) throws CourseException, KCException{
		
		Course course = CourseHandler.getByExternalId(external_course_id);		
		KnowledgeComponent kc = new KnowledgeComponent();
		kc.setExternal_id(external_kc_id);
		kc.setCourse(course);
		try {
			kc = KnowledgeComponentHandler.save(kc);
		} catch(ConstraintViolationException cve) {
			throw new KCException(MyStatus.ERROR, MyMessage.KC_ALREADY_PRESENT);
		} catch(PropertyValueException pve) {
			throw new KCException(MyStatus.ERROR, MyMessage.KC_PROPERTY_NULL);
		}
		
		this.kc = kc;
	}
	


}
