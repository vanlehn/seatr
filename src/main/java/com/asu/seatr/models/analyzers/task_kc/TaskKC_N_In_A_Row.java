
package com.asu.seatr.models.analyzers.task_kc;

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
@Table(name = "tk_a2", uniqueConstraints = @UniqueConstraint(columnNames = {"task_id","kc_id"}))
public class TaskKC_N_In_A_Row implements TaskKCAnalyzerI{
	
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
		return 0;
	}

	@Override
	public void setId(int id) {
		
	}
	
	@Override
	public Task getTask() {
		return task;
	}
	@Override
	public void setTask(Task task) {
		this.task = task;
	}
	@Override
	public KnowledgeComponent getKc() {
		return kc;
	}
	@Override
	public void setKc(KnowledgeComponent kc) {
		this.kc = kc;
	}

	


}
