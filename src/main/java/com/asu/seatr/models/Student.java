package com.asu.seatr.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "student", uniqueConstraints = @UniqueConstraint(columnNames = {"external_id","course_id"}))
public class Student {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@Column(name = "external_id")
	private int external_id;
	
	@ManyToOne
	@JoinColumn(name = "course_id", referencedColumnName = "id")
	private Course course;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getExternal_id() {
		return external_id;
	}
	public void setExternal_id(int external_id) {
		this.external_id = external_id;
	}
	public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) {
		this.course = course;
	}		
}
