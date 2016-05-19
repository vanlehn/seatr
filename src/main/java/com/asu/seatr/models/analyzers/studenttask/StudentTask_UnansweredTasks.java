package com.asu.seatr.models.analyzers.studenttask;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.PropertyValueException;
import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.StudentTaskHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.StudentTask;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.StudentTaskAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;


@Entity
@Table(name = "st_a1")
public class StudentTask_UnansweredTasks implements StudentTaskAnalyzerI{
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	@ManyToOne
	@JoinColumn(name = "student_task_id", referencedColumnName = "id", unique=true, nullable=false)//internal student id
	StudentTask studentTask;

	@Column(name = "d_status")
	private String d_status; // done or not

	@Column(name = "d_time_lastattempt")
	private Integer d_time_lastattempt; // time taken for the last attempt

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public StudentTask getStudentTask() {
		return studentTask;
	}

	public void setStudentTask(StudentTask studentTask) {
		this.studentTask = studentTask;
	}

	public String getD_status() {
		return d_status;
	}

	public void setD_status(String d_status) {
		this.d_status = d_status;
	}

	public Integer getD_time_lastattempt() {
		return d_time_lastattempt;
	}

	public void setD_time_lastattempt(Integer d_time_lastattempt) {
		this.d_time_lastattempt = d_time_lastattempt;
	}

	@Override
	public void createStudentTask(String external_student_id, String external_course_id, String external_task_id,
			int analyzer_id) throws CourseException, TaskException, StudentException {
		Student student = StudentHandler.getByExternalId(external_student_id, external_course_id);
		Task task = TaskHandler.readByExtId(external_task_id, external_course_id);
		StudentTask studentTask = new StudentTask();
		studentTask.setStudent(student);
		studentTask.setTask(task);
		studentTask.setTimestamp(System.currentTimeMillis());

		try
		{
			StudentTaskHandler.save(studentTask);
		}
		catch(PropertyValueException pve) {
			throw new TaskException(MyStatus.ERROR, MyMessage.STUDENT_TASK_PROPERTY_NULL);
		}
		this.studentTask = studentTask;


	}

	@Override
	public void deleteStudentTask(String external_student_id, String external_course_id, int task_id) {

	}

	@Override
	public void updateStudentTask(String external_student_id, String external_course_id, String external_task_id,
			int analyzer_id) {

	}

	@Override
	public StudentTask getStudentTask(String external_student_id, String external_course_id, int task_id) {
		return null;
	}


}
