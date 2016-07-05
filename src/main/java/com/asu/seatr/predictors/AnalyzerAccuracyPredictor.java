package com.asu.seatr.predictors;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.KnowledgeComponentHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.StudentTask;
import com.asu.seatr.models.analyzers.studenttask.StudentTask_N_In_A_Row;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_N_In_A_Row;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.SessionFactoryUtil;
import com.asu.seatr.utils.Utilities;

import au.com.bytecode.opencsv.CSVWriter;

public class AnalyzerAccuracyPredictor {

	public static void main(String args[]) throws IOException {
		try {
			int threshold = 3;
			String external_course_id = "37";
			Scanner sc = new Scanner(System.in);
			System.out.println("enter the path");
			String path = sc.nextLine();
			FileOutputStream fos = new FileOutputStream(path);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			CSVWriter writer = new CSVWriter(osw);

			Course course = CourseHandler.getByExternalId(external_course_id);
			List<Student> studentListForCourse = StudentHandler.getByCourse(course);
			List<KnowledgeComponent> kcListForCourse = KnowledgeComponentHandler.getByCourse(course);

			String firstLine[] = new String[5 + kcListForCourse.size()];
			firstLine[0] = "Student id";
			firstLine[1] = "Task id";
			firstLine[2] = "Status";
			firstLine[3] = "Predicted Correctness";
			firstLine[4] = "TimeStamp";
			int tempIndex = 5;
			ListIterator<KnowledgeComponent> ki = kcListForCourse.listIterator();
			while (ki.hasNext()) {
				firstLine[tempIndex] = "K_" + ki.next().getExternal_id();
				tempIndex++;
			}
			writer.writeNext(firstLine);

			HashMap<Integer, HashMap<Integer, KCData>> studentKcMastery = new HashMap<Integer, HashMap<Integer, KCData>>();
			ListIterator<Student> studentListIterator = studentListForCourse.listIterator();
			while (studentListIterator.hasNext()) {
				HashMap<Integer, KCData> kcMastery = new HashMap<Integer, KCData>();
				ListIterator<KnowledgeComponent> kcListIterator = kcListForCourse.listIterator();
				while (kcListIterator.hasNext()) {
					KCData kd = new KCData();
					kd.setMasteryLevel(0);
					kcMastery.put(kcListIterator.next().getId(), kd);
				}

				studentKcMastery.put(studentListIterator.next().getId(), kcMastery);
			}

			SessionFactory sf;
			if (Utilities.isJUnitTest()) {
				sf = SessionFactoryUtil.getSessionFactory();
			} else {
				sf = HibernateUtil.getSessionFactory();
			}
			Session session = sf.openSession();
			session.beginTransaction();

			String studentTasksQuery = "from StudentTask_N_In_A_Row where studentTask in (Select id from StudentTask where task in (Select id from Task where course = :course))";
			Query query = session.createQuery(studentTasksQuery);
			query.setParameter("course", course);
			@SuppressWarnings("unchecked")
			List<StudentTask_N_In_A_Row> studentTaskList = (List<StudentTask_N_In_A_Row>) query.list();

			ListIterator<StudentTask_N_In_A_Row> studentTaskListIterator = studentTaskList.listIterator();
			while (studentTaskListIterator.hasNext()) {
				int predictedCorrectness = -1;
				StudentTask_N_In_A_Row st_a2 = studentTaskListIterator.next();
				StudentTask studentTask = st_a2.getStudentTask();
				List<TaskKC_N_In_A_Row> tkList = studentTask.getTask().getTK_A2();
				ListIterator<TaskKC_N_In_A_Row> tkListIterator = tkList.listIterator();
				HashMap<Integer, KCData> kcMastery = studentKcMastery.get(studentTask.getStudent().getId());

				while (tkListIterator.hasNext()) {
					Integer kcId = tkListIterator.next().getKc().getId();
					KCData kd = kcMastery.get(kcId);
					 int masteryLevel = kd.getMasteryLevel();
					 if (st_a2.getD_status().equals("correct")) {
						++masteryLevel;
					} else {
						if (masteryLevel < 1) {
							masteryLevel = 0;
						} else {
							--masteryLevel;
						}
					}
					if (masteryLevel < threshold) {
						predictedCorrectness = 0;
					} else {
						if (predictedCorrectness != 0) {
							predictedCorrectness = 1;
						}
					}
					kd.setMasteryLevel(masteryLevel);
					kd.setExists(true);
					kcMastery.put(kcId, kd);
				}
				if (predictedCorrectness == -1) {
					predictedCorrectness = 0;
				}
				studentKcMastery.put(studentTask.getStudent().getId(), kcMastery);

				String output[] = new String[5 + kcListForCourse.size()];
				output[0] = studentTask.getStudent().getExternal_id();
				output[1] = studentTask.getTask().getExternal_id();
				output[2] = st_a2.getD_status();
				output[3] = String.valueOf(predictedCorrectness);
				output[4] = String.valueOf(studentTask.getTimestamp());
				int index = 5;
				ListIterator<KnowledgeComponent> kcIterator = kcListForCourse.listIterator();
				while (kcIterator.hasNext()) {
					KCData kd = kcMastery.get(kcIterator.next().getId());
					
					if (kd == null) {
						output[index] = "0";
					} else {
						boolean temp1 = kd.isExists();
						if(temp1)
						{
							output[index] = String.valueOf(1);
						}
						else
						{
							output[index] = String.valueOf(0);
						}
						//output[index] = String.valueOf(temp1);
					}
					index++;
				}
				writer.writeNext(output);
			}
			writer.close();
			osw.close();
			fos.close();
			sc.close();
		} catch (CourseException e) {
			e.printStackTrace();
		}
		System.out.println("execution done");
	}
}
