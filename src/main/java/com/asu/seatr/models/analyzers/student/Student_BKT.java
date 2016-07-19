package com.asu.seatr.models.analyzers.student;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.PropertyValueException;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.interfaces.StudentAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

@Entity
@Table(name = "s_bkt", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
// Sample analyzer with properties placement_score and year
public class Student_BKT implements StudentAnalyzerI{
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable=false)//internal student id
	Student student;
	
	@ManyToOne
	@JoinColumn(name = "course_id", referencedColumnName = "id", nullable=false)//internal course id
	Course course;
	

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void createStudent(String student_ext_id, String external_course_id, int analyzer_id) throws CourseException, StudentException {
		
		Course course = CourseHandler.getByExternalId(external_course_id);		
		Student student = new Student();
		student.setExternal_id(student_ext_id);
		student.setCourse(course);
		try {
			student = StudentHandler.save(student);
		} catch(ConstraintViolationException cve) {
			student = StudentHandler.getByExternalId(student_ext_id, external_course_id);
			//throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ALREADY_PRESENT);
		} catch(PropertyValueException pve) {
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_PROPERTY_NULL);
		}
		
		this.student = student;
		this.course = course;
	}

	@Override
	public void deleteStudent(String student_ext_id, String external_course_id, int analyzer_id) {
		
	}

	@Override
	public void updateStudent(String student_ext_id, String external_course_id, int analyzer_id) {		
		
	}

	@Override
	public Student getStudent(String external_student_id, String external_course_id, int analyzer_id) {		
		return null;
	}

}
