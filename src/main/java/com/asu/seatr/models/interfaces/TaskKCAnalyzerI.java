package com.asu.seatr.models.interfaces;

import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Task;

public interface TaskKCAnalyzerI {

	public int getId();
	public void setId(int id);
	public Task getTask();
	public void setTask(Task task);
	public KnowledgeComponent getKc();
	public void setKc(KnowledgeComponent kc);
	
}
