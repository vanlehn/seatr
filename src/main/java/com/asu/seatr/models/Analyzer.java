package com.asu.seatr.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name = "analyzer")
public class Analyzer {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	@Column(name = "name", nullable=false)
	private String name;

	@Column(name = "description")
	private String description;

	@OneToMany(mappedBy = "analyzer", cascade=CascadeType.ALL)
	private List<CourseAnalyzerMap> CourseAnalyzerMap;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString()
	{
		return this.name;
	}

	public List<CourseAnalyzerMap> getCourseAnalyzerMap() {
		return CourseAnalyzerMap;
	}
	public void setCourseAnalyzerMap(List<CourseAnalyzerMap> courseAnalyzerMap) {
		CourseAnalyzerMap = courseAnalyzerMap;
	}
}
