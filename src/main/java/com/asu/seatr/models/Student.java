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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.analyzers.student.Student_UnansweredTasks;
import com.asu.seatr.models.analyzers.studenttask.RecommTask_N_In_A_Row;
import com.asu.seatr.models.analyzers.studenttask.RecommTask_UnansweredTasks;
import com.asu.seatr.models.analyzers.studenttask.STU_N_In_A_Row;
import com.asu.seatr.models.analyzers.studenttask.StuTaskUtility_BKT;
import com.asu.seatr.models.analyzers.student.Student_BKT;
import com.asu.seatr.models.analyzers.student.Student_N_In_A_Row;
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
	private List<Student_N_In_A_Row> S_A2;
	
	@OneToMany(mappedBy = "student", cascade=CascadeType.ALL)
	private List<Student_Required_Optional> S_A3;
	
	@OneToOne(mappedBy = "student", cascade=CascadeType.ALL)
	private Student_BKT S_BKT;
	
	@OneToMany(mappedBy = "student", cascade=CascadeType.ALL)
	private List<RecommTask_N_In_A_Row> recommTask_N_In_A_Row;
	
	@OneToMany(mappedBy = "student", cascade=CascadeType.ALL)
	private List<RecommTask_UnansweredTasks> recommTask_UnansweredTasks;
	
	@OneToMany(mappedBy = "student", cascade=CascadeType.ALL)
	private List<STU_N_In_A_Row> stu_N_In_A_Row;
	
	@OneToMany(mappedBy = "student", cascade=CascadeType.ALL)
	private List<StuTaskUtility_BKT> stu_BKT;

	public Student_BKT getS_BKT() {
		return S_BKT;
	}
	public void setS_BKT(Student_BKT s_BKT) {
		S_BKT = s_BKT;
	}
	public List<StuTaskUtility_BKT> getStu_BKT() {
		return stu_BKT;
	}
	public void setStu_BKT(List<StuTaskUtility_BKT> stu_BKT) {
		this.stu_BKT = stu_BKT;
	}
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
	public List<STU_N_In_A_Row> getStu_N_In_A_Row() {
		return stu_N_In_A_Row;
	}
	public void setStu_N_In_A_Row(List<STU_N_In_A_Row> stu_N_In_A_Row) {
		this.stu_N_In_A_Row = stu_N_In_A_Row;
	}
	
}
