package com.asu.seatr.factory;

import com.asu.seatr.models.analyzers.task_kc.TaskKC_N_In_A_Row;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_Required_Optional;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_UnansweredTasks;
import com.asu.seatr.models.interfaces.TaskKCAnalyzerI;
import com.asu.seatr.utils.Utilities;

public class TaskKCFactory {

	public static TaskKCAnalyzerI getObject(String objectName)
	{
		if(!Utilities.checkExists(objectName))
		{
			return null;
		}
		if(objectName.equalsIgnoreCase("UnansweredTasks"))
		{
			return new TaskKC_UnansweredTasks();
		}
		else if(objectName.equalsIgnoreCase("N_In_A_Row"))
		{
			return new TaskKC_N_In_A_Row();
		}
		else if(objectName.equalsIgnoreCase("Required_Optional"))
		{
			return new TaskKC_Required_Optional();
		}
		return null;
	}
}
