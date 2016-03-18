package com.asu.seatr.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name="user_course", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
public class UserCourse {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable=false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "course_id", referencedColumnName = "id", nullable=false)
	private Course course;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}
	
}