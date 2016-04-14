package com.asu.seatr.models.analyzers.studenttask;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.RecommTaskI;


@Entity
@Table(name="recomm_task_a1")
public class RecommTask_A1 implements RecommTaskI{  //all the recommended tasks are not done

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	@ManyToOne
	@JoinColumn(name="student_id", referencedColumnName="id", nullable=false)
	private Student student;

	@ManyToOne
	@JoinColumn(name="task_id", referencedColumnName="id", nullable=false)
	private Task task;

	@ManyToOne
	@JoinColumn(name="course_id", referencedColumnName="id", nullable=false)
	private Course course;

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}



}
