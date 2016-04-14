package com.asu.seatr.models.analyzers.student;

import javax.persistence.Column;
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
@Table(name = "s_a3", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
// Sample analyzer with properties placement_score and year
public class S_A3 implements StudentAnalyzerI{
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

	//Properties that dont change
	@Column(name = "s_placement_score")
	private Double s_placement_score;
	@Column(name = "s_year")
	private String s_year;

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

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

	@Override
	public void updateStudent(String student_ext_id, String external_course_id, int analyzer_id) {
		// TODO Auto-generated method stub

	}

	@Override
	public Student getStudent(String external_student_id, String external_course_id, int analyzer_id) {

		/*
		Student student = StudentHandler.getByExternalId(external_student_id, external_course_id);		
		this.student = student;
		this.course = student.getCourse();
		return student;
		 */
		return null;
	}

}
