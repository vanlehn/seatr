package com.asu.seatr.models.analyzers.course;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.Course;
import com.asu.seatr.models.interfaces.CourseAnalyzerI;
import com.asu.seatr.handlers.CourseHandler;


@Entity
@Table(name="c_a1", uniqueConstraints=@UniqueConstraint(columnNames={"student_id", "course_id"}))
public class C_A1{
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="course", referencedColumnName="id") //id of a course
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

	public void createCourse(String course_ext_id, int analyzer_id) {
		//Course course=CourseHandler.getByExternalId(external_id)
		
	}

	public void deleteCourse(int course_id, int analyzer_id) {
		// TODO Auto-generated method stub
		
	}

	public void updateCourse(int course_id, int analyzer_id) {
		// TODO Auto-generated method stub
		
	}

	public Course getCourse(int course_id, int analyzer_id) {
		// TODO Auto-generated method stub
		return null;
	}

}
