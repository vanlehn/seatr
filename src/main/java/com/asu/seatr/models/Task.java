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

import com.asu.seatr.models.analyzers.task.Task_UnansweredTasks;
import com.asu.seatr.models.analyzers.task.Task_N_In_A_Row;
import com.asu.seatr.models.analyzers.task.Task_Required_Optional;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_UnansweredTasks;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_N_In_A_Row;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_Required_Optional;

@Entity
@Table(name = "task", uniqueConstraints = @UniqueConstraint(columnNames = {"external_id", "course_id"}))
public class Task {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	@Column(name = "external_id", nullable=false)
	private String external_id;

	@ManyToOne
	@JoinColumn(name = "course_id", referencedColumnName = "id", nullable=false)
	private Course course;

	@OneToMany(mappedBy = "task", cascade=CascadeType.ALL)
	private List<StudentTask> StudentTask;

	@OneToMany(mappedBy = "task", cascade=CascadeType.ALL)
	private List<Task_UnansweredTasks> T_A1;
	
	@OneToMany(mappedBy = "task", cascade=CascadeType.ALL)
	private List<Task_N_In_A_Row> T_A2;
	
	@OneToMany(mappedBy = "task", cascade=CascadeType.ALL)
	private List<Task_Required_Optional> T_A3;

	@OneToMany(mappedBy = "task", cascade=CascadeType.ALL)
	private List<TaskKC_UnansweredTasks> TK_A1;
	
	@OneToMany(mappedBy = "task", cascade=CascadeType.ALL)
	private List<TaskKC_N_In_A_Row> TK_A2;
	
	@OneToMany(mappedBy = "task", cascade=CascadeType.ALL)
	private List<TaskKC_Required_Optional> TK_A3;

	public final static String p_id = "id";
	public final static String p_external_id = "external_id";
	public final static String p_course = "course";

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
	public List<Task_UnansweredTasks> getT_A1() {
		return T_A1;
	}
	public void setT_A1(List<Task_UnansweredTasks> t_A1) {
		T_A1 = t_A1;
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
	public List<TaskKC_UnansweredTasks> getTK_A1() {
		return TK_A1;
	}
	public void setTK_A1(List<TaskKC_UnansweredTasks> tK_A1) {
		TK_A1 = tK_A1;
	}	
	public List<TaskKC_N_In_A_Row> getTK_A2() {
		return TK_A2;
	}
	public void setTK_A2(List<TaskKC_N_In_A_Row> tK_A2) {
		TK_A2 = tK_A2;
	}	
	public List<TaskKC_Required_Optional> getTK_A3() {
		return TK_A3;
	}
	public void setTK_A3(List<TaskKC_Required_Optional> tK_A3) {
		TK_A3 = tK_A3;
	}	

}
