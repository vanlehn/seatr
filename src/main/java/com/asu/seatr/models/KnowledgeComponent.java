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

import com.asu.seatr.models.analyzers.kc.KC_BKT;
import com.asu.seatr.models.analyzers.kc.KC_N_In_A_Row;
import com.asu.seatr.models.analyzers.kc.KC_Required_Optional;
import com.asu.seatr.models.analyzers.kc.KC_UnansweredTasks;
import com.asu.seatr.models.analyzers.student.SKC_BKT;
import com.asu.seatr.models.analyzers.student.SKC_N_In_A_Row;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_BKT;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_N_In_A_Row;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_Required_Optional;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_UnansweredTasks;

@Entity
@Table(name = "kc", uniqueConstraints = @UniqueConstraint(columnNames = {"external_id","course_id"}))
public class KnowledgeComponent {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	@Column(name = "external_id")
	private String external_id;

	@ManyToOne//(targetEntity = Course.class)
	@JoinColumn(name = "course_id", referencedColumnName = "id")
	private Course course;

	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<KC_UnansweredTasks> K_A1;
	
	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<KC_N_In_A_Row> K_A2;
	
	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<KC_Required_Optional> K_A3;
	
	@OneToOne(mappedBy = "kc", cascade=CascadeType.ALL)
	private KC_BKT K_BKT;

	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<TaskKC_UnansweredTasks> TK_A1;
	
	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<TaskKC_N_In_A_Row> TK_A2;
	
	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<TaskKC_Required_Optional> TK_A3;
	
	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<TaskKC_BKT> TK_BKT;
	
	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<SKC_N_In_A_Row> skc_N_In_A_Row;
	
	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<SKC_BKT> skc_BKT;	

	public KC_BKT getK_BKT() {
		return K_BKT;
	}

	public void setK_BKT(KC_BKT k_BKT) {
		K_BKT = k_BKT;
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

	public List<KC_UnansweredTasks> getK_A1() {
		return K_A1;
	}

	public void setK_A1(List<KC_UnansweredTasks> k_A1) {
		K_A1 = k_A1;
	}

	
	
	public List<TaskKC_UnansweredTasks> getTK_A1() {
		return TK_A1;
	}

	public void setTK_A1(List<TaskKC_UnansweredTasks> tK_A1) {
		TK_A1 = tK_A1;
	}

	public List<KC_N_In_A_Row> getK_A2() {
		return K_A2;
	}

	public void setK_A2(List<KC_N_In_A_Row> k_A2) {
		K_A2 = k_A2;
	}

	public List<KC_Required_Optional> getK_A3() {
		return K_A3;
	}

	public void setK_A3(List<KC_Required_Optional> k_A3) {
		K_A3 = k_A3;
	}

	public List<SKC_N_In_A_Row> getSkc_N_In_A_Row() {
		return skc_N_In_A_Row;
	}

	public void setSkc_N_In_A_Row(List<SKC_N_In_A_Row> skc_N_In_A_Row) {
		this.skc_N_In_A_Row = skc_N_In_A_Row;
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

	public List<TaskKC_BKT> getTK_BKT() {
		return TK_BKT;
	}

	public void setTK_BKT(List<TaskKC_BKT> tK_BKT) {
		TK_BKT = tK_BKT;
	}

	public List<SKC_BKT> getSkc_BKT() {
		return skc_BKT;
	}

	public void setSkc_BKT(List<SKC_BKT> skc_BKT) {
		this.skc_BKT = skc_BKT;
	}




}
