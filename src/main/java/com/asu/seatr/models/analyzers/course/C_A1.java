package com.asu.seatr.models.analyzers.course;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.Course;
import com.asu.seatr.models.interfaces.CourseAnalyzerI;
import com.asu.seatr.models.interfaces.StudentAnalyzerI;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.handlers.AnalyzerHandler;
import com.asu.seatr.handlers.CourseHandler;


@Entity
@Table(name="c_a1")
public class C_A1{
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private int id;
	
	@OneToOne
	@JoinColumn(name="course_id", referencedColumnName="id", unique = true, nullable=false) //id of a course
	Course course;
	
	//Properties
	@Column(name="mastery_threshold")
	private Double threshold;
	@Column(name="teaching_unit")
	private String teaching_unit;
	
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setId(int id) {
		// TODO Auto-generated method stub
		
	}

	public void setCourseByExt_id(String course_ext_id) throws CourseException {
		Course course=CourseHandler.getByExternalId(course_ext_id);
		this.course=course;
		
	}
	
	public String getCourseExtId(){
		return this.course.getExternal_id();
	}
	
	public String getCourseDesc(){
		return this.course.getDescription();
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

}
