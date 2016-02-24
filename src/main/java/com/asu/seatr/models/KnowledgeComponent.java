package com.asu.seatr.models;

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
import com.asu.seatr.exceptions.KCNotFoundException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.KnowledgeComponentHandler;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

@Entity
@Table(name = "kc", uniqueConstraints = @UniqueConstraint(columnNames = {"external_id","course_id"}))
public class KnowledgeComponent {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@Column(name = "external_id")
	private String external_id;
	
	@ManyToOne(targetEntity = Course.class)
	@JoinColumn(name = "course_id", referencedColumnName = "id")
	private Course course;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getExternal_id() {
		return external_id;
	}

	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}
	
	


}
