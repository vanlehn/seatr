package com.asu.seatr.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import org.hibernate.SessionFactory;

import com.asu.seatr.handlers.Handler;
import com.asu.seatr.models.Analyzer;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.analyzers.kc.KC_N_In_A_Row;
import com.asu.seatr.models.analyzers.kc.KC_Required_Optional;
import com.asu.seatr.models.analyzers.kc.KC_UnansweredTasks;
import com.asu.seatr.models.analyzers.student.SKC_N_In_A_Row;
import com.asu.seatr.models.analyzers.studenttask.RecommTask_N_In_A_Row;
import com.asu.seatr.models.analyzers.studenttask.STU_N_In_A_Row;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_N_In_A_Row;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_Required_Optional;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_UnansweredTasks;
import com.asu.seatr.persistence.HibernateUtil;

@SuppressWarnings({ "rawtypes" })
public class Utilities {

	private static boolean isJUnitTest;
	public static Boolean checkExists(String st) {
		if(st == null || st.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	public static Boolean checkExists(Boolean b) {
		return (b != null);
	}
	public static Boolean checkExists(Integer b) {
		return (b != null);
	}
	public static Boolean checkExists(Double b) {
		return (b != null);
	}

	public static String getTKTableNameByAnalyzerId(Integer analyzer_id) {
		String tka = "TaskKC_";
		//write code to get analyzer name from id and append the analyzer name here
		return tka + analyzer_id.toString();
	}

	public static boolean isInteger(String s, int radix) {
		if(s.isEmpty()) return false;
		for(int i = 0; i < s.length(); i++) {
			if(i == 0 && s.charAt(i) == '-') {
				if(s.length() == 1) return false;
				else continue;
			}
			if(Character.digit(s.charAt(i),radix) < 0) return false;
		}
		return true;
	}

	public static Class getTKClass(Integer from_analyzer_id) {

		switch(from_analyzer_id) {
		case 1: return TaskKC_UnansweredTasks.class;
		case 2: return TaskKC_N_In_A_Row.class;
		case 3: return TaskKC_Required_Optional.class;
		default: return null;
		}

	}

	public static boolean isJUnitTest() {
		return isJUnitTest;
	}

	public static void setJUnitTest(boolean pIsJUnitTest) {
		isJUnitTest = pIsJUnitTest;
	}
	
	public static SessionFactory getSessionFactory() {
		SessionFactory sf;
		if(isJUnitTest) {
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else {	
			sf = HibernateUtil.getSessionFactory();
		}
		return sf;
	}
	
	public static void writeToGraphite(String metric,Double responseTime,Long timestamp)
	{
			if(!isJUnitTest)
			{
				Socket socket = null;
				Writer writer = null;
				try
				{
					socket = new Socket("localhost", 2003);
					writer = new OutputStreamWriter(socket.getOutputStream());
					String sentMessage = metric + " " + responseTime + " " + timestamp + "\n";
			        System.out.println(sentMessage);
			        writer.write(sentMessage);
			        writer.flush();
			        
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if(socket != null)
						{
							socket.close();
						}
						if(writer != null)
						{
							writer.close();
						}
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
	}
	public static void writeToGraphite(String metric,Long responseTime,Long timestamp)
	{
			if(!isJUnitTest)
			{
				Socket socket = null;
				Writer writer = null;
				try
				{
					socket = new Socket("localhost", 2003);
					writer = new OutputStreamWriter(socket.getOutputStream());
					String sentMessage = metric + " " + responseTime + " " + timestamp + "\n";
			        System.out.println(sentMessage);
			        writer.write(sentMessage);
			        writer.flush();
			        
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if(socket != null)
						{
							socket.close();
						}
						if(writer != null)
						{
							writer.close();
						}
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
	}
	public static void clearDatabase()
	{
		Handler.hqlTruncate("RecommTask_UnansweredTasks");
		Handler.hqlTruncate("RecommTask_N_In_A_Row");
		Handler.hqlTruncate("CourseAnalyzerMap");
		Handler.hqlTruncate("StudentTask_UnansweredTasks");
		Handler.hqlTruncate("StudentTask_N_In_A_Row");
		Handler.hqlTruncate("StudentTask_Required_Optional");
		Handler.hqlTruncate("StudentTask");
		Handler.hqlTruncate("KC_UnansweredTasks");
		Handler.hqlTruncate("KC_N_In_A_Row");
		Handler.hqlTruncate("KC_Required_Optional");
		Handler.hqlTruncate("TaskKC_UnansweredTasks");
		Handler.hqlTruncate("TaskKC_N_In_A_Row");
		Handler.hqlTruncate("TaskKC_Required_Optional");
		Handler.hqlTruncate("SKC_N_In_A_Row");
		Handler.hqlTruncate("STU_N_In_A_Row");
		Handler.hqlTruncate("KnowledgeComponent");
		Handler.hqlTruncate("Student_UnansweredTasks");
		Handler.hqlTruncate("Student_N_In_A_Row");
		Handler.hqlTruncate("Student_Required_Optional");
		Handler.hqlTruncate("Student");
		Handler.hqlTruncate("Task_UnansweredTasks");
		Handler.hqlTruncate("Task_N_In_A_Row");
		Handler.hqlTruncate("Task_Required_Optional");
		Handler.hqlTruncate("Task");
		Handler.hqlTruncate("Course_UnansweredTasks");
		Handler.hqlTruncate("Course_N_In_A_Row");
		Handler.hqlTruncate("Course_Required_Optional");
		Handler.hqlTruncate("UserCourse");
		Handler.hqlTruncate("Course");
	}

}
