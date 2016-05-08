package com.asu.seatr.models.analyzers.course;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.PropertyValueException;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.interfaces.CourseAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;


@Entity
@Table(name="c_a2")
public class C_A2 implements CourseAnalyzerI{
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private int id;
	
	@OneToOne
	@JoinColumn(name="course_id", referencedColumnName="id", unique = true, nullable=false) //id of a course
	Course course;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;		
	}
	
	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
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


	@Override
	public void createCourse(String external_course_id, String description) throws CourseException {
		Course course = new Course();
		course.setExternal_id(external_course_id);
		course.setDescription(description);
		try {
			CourseHandler.save(course);
		} 
		
		catch (ConstraintViolationException cve) {
			course = CourseHandler.getByExternalId(external_course_id);
			this.course = course;
		} 
		
		catch (PropertyValueException pve) {
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_PROPERTY_NULL);
		} 
		this.course = course;
		
	}

	

	@Override
	public void updateCourse(String external_course_id, String description) throws CourseException {
		
	}

	@Override
	public void deleteCourse(String external_course_id, int analyzer_id) {
		
	}

	@Override
	public Course getCourse(String external_course_id, int analyzer_id) {
		return null;
	}

	

}
