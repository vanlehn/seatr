package com.asu.seatr.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import org.hibernate.SessionFactory;

import com.asu.seatr.handlers.Handler;
import com.asu.seatr.models.analyzers.task_kc.TK_A1;
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
		String tka = "TK_A";
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
		case 1: return TK_A1.class;
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
		Handler.hqlTruncate("RecommTask_A1");
		Handler.hqlTruncate("CourseAnalyzerMap");
		Handler.hqlTruncate("ST_A1");
		Handler.hqlTruncate("ST_A2");
		Handler.hqlTruncate("ST_A3");
		Handler.hqlTruncate("StudentTask");
		Handler.hqlTruncate("T_A1");
		Handler.hqlTruncate("T_A2");
		Handler.hqlTruncate("T_A3");
		Handler.hqlTruncate("Task");
		Handler.hqlTruncate("S_A1");
		Handler.hqlTruncate("S_A2");
		Handler.hqlTruncate("S_A3");
		Handler.hqlTruncate("Student");
		Handler.hqlTruncate("C_A1");
		Handler.hqlTruncate("C_A2");
		Handler.hqlTruncate("C_A3");
		Handler.hqlTruncate("UserCourse");
		Handler.hqlTruncate("Course");
	}

}
