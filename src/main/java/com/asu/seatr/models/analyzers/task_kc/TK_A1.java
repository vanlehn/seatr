package com.asu.seatr.models.analyzers.task_kc;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.TaskKCAnalyzerI;


@Entity
@Table(name = "tk_a1", uniqueConstraints = @UniqueConstraint(columnNames = {"task_id","kc_id"}))
public class TK_A1 implements TaskKCAnalyzerI{

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "task_id", referencedColumnName = "id")
	private Task task;
	
	@ManyToOne
	@JoinColumn(name = "kc_id", referencedColumnName = "id")
	private KnowledgeComponent kc;
	
	@Column(name = "min_mastery_level")
	private int s_min_mastery_level;
	

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		
	}
	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public KnowledgeComponent getKc() {
		return kc;
	}

	public void setKc(KnowledgeComponent kc) {
		this.kc = kc;
	}
	
	public int getS_min_mastery_level() {
		return s_min_mastery_level;
	}

	public void setS_min_mastery_level(int s_min_mastery_level) {
		this.s_min_mastery_level = s_min_mastery_level;
	}
	


}
