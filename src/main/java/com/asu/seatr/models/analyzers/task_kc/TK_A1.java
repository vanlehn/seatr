package com.asu.seatr.models.analyzers.task_kc;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.Task_KCAnalyzerI;


@Entity
@Table(name = "tk_a1")
public class TK_A1 implements Task_KCAnalyzerI{

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


}
