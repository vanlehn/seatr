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

import com.asu.seatr.models.analyzers.course.*;
import com.asu.seatr.models.analyzers.student.S_A1;
import com.asu.seatr.models.analyzers.task.T_A1;
@Entity
@Table( name="course")

public class Course {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@Column(name="external_id", nullable=false, unique=true)
	private String external_id;
	
	@Column(name="description")
	private String description;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<CourseAnalyzerMap> CourseAnalyzerMap;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<KnowledgeComponent> KnowledgeComponent;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<Student> Student;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<UserCourse> UserCourse;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<Task> Task;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<C_A1> C_A1;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<S_A1> S_A1;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<T_A1> T_A1;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getExternal_id() {
		return external_id;
	}
	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}
	
	public List<CourseAnalyzerMap> getCourseAnalyzerMap() {
		return CourseAnalyzerMap;
	}
	public void setCourseAnalyzerMap(List<CourseAnalyzerMap> courseAnalyzerMap) {
		CourseAnalyzerMap = courseAnalyzerMap;
	}
	public List<KnowledgeComponent> getKnowledgeComponent() {
		return KnowledgeComponent;
	}
	public void setKnowledgeComponent(List<KnowledgeComponent> knowledgeComponent) {
		KnowledgeComponent = knowledgeComponent;
	}
	public List<Student> getStudent() {
		return Student;
	}
	public void setStudent(List<Student> student) {
		Student = student;
	}
	public List<UserCourse> getUserCourse() {
		return UserCourse;
	}
	public void setUserCourse(List<UserCourse> userCourse) {
		UserCourse = userCourse;
	}
	public List<Task> getTask() {
		return Task;
	}
	public void setTask(List<Task> task) {
		Task = task;
	}
	public List<C_A1> getC_A1() {
		return C_A1;
	}
	public void setC_A1(List<C_A1> c_A1) {
		C_A1 = c_A1;
	}
	public List<S_A1> getS_A1() {
		return S_A1;
	}
	public void setS_A1(List<S_A1> s_A1) {
		S_A1 = s_A1;
	}
	public List<T_A1> getT_A1() {
		return T_A1;
	}
	public void setT_A1(List<T_A1> t_A1) {
		T_A1 = t_A1;
	}	
	
	
}