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
@Table( name = "course_analyzer", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "analyzer_id"}))
public class CourseAnalyzer {
	
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	@ManyToOne
	@JoinColumn(name = "course_id", referencedColumnName = "id")
	private int course_id;
	
	@ManyToOne
	@JoinColumn(name = "analyzer_id", referencedColumnName = "id")
	private int analyzer_id;
	
	@Column(name = "active")
	private boolean active;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCourse_id() {
		return course_id;
	}
	public void setCourse_id(int course_id) {
		this.course_id = course_id;
	}
	public int getAnalyzer_id() {
		return analyzer_id;
	}
	public void setAnalyzer_id(int analyzer_id) {
		this.analyzer_id = analyzer_id;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}	
}
