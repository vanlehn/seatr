package com.asu.seatr.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.analyzers.student.Student_UnansweredTasks;
import com.asu.seatr.models.analyzers.student.Student_Required_Optional;

@Entity
@Table(name = "student", uniqueConstraints = @UniqueConstraint(columnNames = {"external_id","course_id"}))
public class Student {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	@Column(name = "external_id", nullable=false)
	private String external_id;

	@ManyToOne(targetEntity = Course.class)
	@JoinColumn(name = "course_id", referencedColumnName = "id", nullable=false)
	private Course course;

	@OneToMany(mappedBy = "student", cascade=CascadeType.ALL)
	private List<StudentTask> StudentTask;

	@OneToMany(mappedBy = "student", cascade=CascadeType.ALL)
	private List<Student_UnansweredTasks> S_A1;

	@OneToMany(mappedBy = "student", cascade=CascadeType.ALL)
	private List<Student_Required_Optional> S_A3;

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
	public List<StudentTask> getStudentTask() {
		return StudentTask;
	}
	public void setStudentTask(List<StudentTask> studentTask) {
		StudentTask = studentTask;
	}
	public List<Student_UnansweredTasks> getS_A1() {
		return S_A1;
	}
	public void setS_A1(List<Student_UnansweredTasks> s_A1) {
		S_A1 = s_A1;
	}	
}
