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

import com.asu.seatr.models.analyzers.course.Course_N_In_A_Row;
import com.asu.seatr.models.analyzers.course.Course_Required_Optional;
import com.asu.seatr.models.analyzers.course.Course_UnansweredTasks;
import com.asu.seatr.models.analyzers.student.Student_N_In_A_Row;
import com.asu.seatr.models.analyzers.student.Student_Required_Optional;
import com.asu.seatr.models.analyzers.student.Student_UnansweredTasks;
import com.asu.seatr.models.analyzers.studenttask.RecommTask_N_In_A_Row;
import com.asu.seatr.models.analyzers.studenttask.RecommTask_UnansweredTasks;
import com.asu.seatr.models.analyzers.task.Task_N_In_A_Row;
import com.asu.seatr.models.analyzers.task.Task_Required_Optional;
import com.asu.seatr.models.analyzers.task.Task_UnansweredTasks;
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
	private List<Course_UnansweredTasks> C_A1;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<Course_N_In_A_Row> C_A2;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<Course_Required_Optional> C_A3;

	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<Student_UnansweredTasks> S_A1;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<Student_N_In_A_Row> S_A2;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<Student_Required_Optional> S_A3;

	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<Task_UnansweredTasks> T_A1;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<Task_N_In_A_Row> T_A2;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<Task_Required_Optional> T_A3;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<RecommTask_N_In_A_Row> recommTask_N_In_A_Row;
	
	@OneToMany(mappedBy = "course", cascade=CascadeType.ALL)
	private List<RecommTask_UnansweredTasks> recommTask_UnansweredTasks;



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
	public List<Course_UnansweredTasks> getC_A1() {
		return C_A1;
	}
	public void setC_A1(List<Course_UnansweredTasks> c_A1) {
		C_A1 = c_A1;
	}
	public List<Student_UnansweredTasks> getS_A1() {
		return S_A1;
	}
	public void setS_A1(List<Student_UnansweredTasks> s_A1) {
		S_A1 = s_A1;
	}
	public List<Task_UnansweredTasks> getT_A1() {
		return T_A1;
	}
	public void setT_A1(List<Task_UnansweredTasks> t_A1) {
		T_A1 = t_A1;
	}
	public List<Course_N_In_A_Row> getC_A2() {
		return C_A2;
	}
	public void setC_A2(List<Course_N_In_A_Row> c_A2) {
		C_A2 = c_A2;
	}
	public List<Course_Required_Optional> getC_A3() {
		return C_A3;
	}
	public void setC_A3(List<Course_Required_Optional> c_A3) {
		C_A3 = c_A3;
	}
	public List<Student_N_In_A_Row> getS_A2() {
		return S_A2;
	}
	public void setS_A2(List<Student_N_In_A_Row> s_A2) {
		S_A2 = s_A2;
	}
	public List<Student_Required_Optional> getS_A3() {
		return S_A3;
	}
	public void setS_A3(List<Student_Required_Optional> s_A3) {
		S_A3 = s_A3;
	}
	public List<RecommTask_N_In_A_Row> getRecommTask_N_In_A_Row() {
		return recommTask_N_In_A_Row;
	}
	public void setRecommTask_N_In_A_Row(List<RecommTask_N_In_A_Row> recommTask_N_In_A_Row) {
		this.recommTask_N_In_A_Row = recommTask_N_In_A_Row;
	}
	public List<RecommTask_UnansweredTasks> getRecommTask_UnansweredTasks() {
		return recommTask_UnansweredTasks;
	}
	public void setRecommTask_UnansweredTasks(List<RecommTask_UnansweredTasks> recommTask_UnansweredTasks) {
		this.recommTask_UnansweredTasks = recommTask_UnansweredTasks;
	}
	public List<Task_N_In_A_Row> getT_A2() {
		return T_A2;
	}
	public void setT_A2(List<Task_N_In_A_Row> t_A2) {
		T_A2 = t_A2;
	}
	public List<Task_Required_Optional> getT_A3() {
		return T_A3;
	}
	public void setT_A3(List<Task_Required_Optional> t_A3) {
		T_A3 = t_A3;
	}	


}