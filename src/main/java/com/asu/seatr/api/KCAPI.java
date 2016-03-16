package com.asu.seatr.api;


import java.util.HashSet;
import java.util.List;
import java.util.Set;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;


import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.CourseHandler;


import com.asu.seatr.handlers.KCAnalyzerHandler;
import com.asu.seatr.handlers.KnowledgeComponentHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.TaskKCAnalyzerHandler;
import com.asu.seatr.models.Course;

import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.kc.K_A1;
import com.asu.seatr.models.analyzers.task_kc.TK_A1;

import com.asu.seatr.rest.models.KAReader1;
import com.asu.seatr.rest.models.TKAReader1;

import com.asu.seatr.rest.models.TKReader1;

		
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;


@Path("analyzer/1/kc")
public class KCAPI {

	
	@Path("/createkc")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createKC1(KAReader1 kaReader)
	{
		
		K_A1 ka1 = new K_A1();
		try {
			ka1.createKC(kaReader.getExternal_kc_id(), kaReader.getExternal_course_id());
			ka1.setS_unit(kaReader.getS_unit());
			KCAnalyzerHandler.save(ka1);
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_CREATED)).build();
		} catch (CourseException e) {
			// TODO Auto-generated catch block
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch (KCException e) {
			// TODO Auto-generated catch block
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	
	/**
	 * Created KC_TASK mapping entry in tk_a1 table
	 * If replace is true, first all records are truncated and then mappings inserted
	 * If replace is false, records are directly inserted in the table. Duplicate records not allowed.
	 * @param tkReader
	 * @return
	 */
	@Path("/mapkctask")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response mapKcToTask(TKReader1 tkReader)
	{
		Session session = null;
		try {
			boolean replace = tkReader.getReplace();
			/*if(replace)
			{
				Handler.hqlTruncate("TK_A1");
			}*/
			TKAReader1 tkReaderArray[] = tkReader.getTkaReader();
			
			if(replace)
			{
				Set<String> externalCourseSet = new HashSet<String>();
				for(int i=0; i < tkReaderArray.length; i++)
				{
					externalCourseSet.add(tkReaderArray[i].getExternal_course_id());
				}
				List<Course> courseList = CourseHandler.getCourseList(externalCourseSet);
				session = KCAnalyzerHandler.hqlBatchDeleteByCourse("A1", courseList,false);
			}
			TK_A1 tk1Array[] = new TK_A1[tkReaderArray.length];
			for(int i = 0; i<tkReaderArray.length;i++)
			{
				TK_A1 tk1 = new TK_A1();
				TKAReader1 tkReader1 = tkReaderArray[i];
				KnowledgeComponent kc = KnowledgeComponentHandler.readByExtId(tkReader1.getExternal_kc_id(), tkReader1.getExternal_course_id());
				Task task;
				task = TaskHandler.readByExtId(tkReader1.getExternal_task_id(), tkReader1.getExternal_course_id());
				tk1.setKc(kc);
				tk1.setTask(task);
				tk1.setS_min_mastery_level(tkReader1.getMin_mastery_level());
				tk1Array[i] = tk1;
			}
			session = TaskKCAnalyzerHandler.batchSave(tk1Array,false,session);
			if(session != null)
			
				{
				session.getTransaction().commit();
				session.close();
				}

			
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_TASK_CREATED)).build();
			} catch (CourseException e) {
			// TODO Auto-generated catch block
				if(session != null)
				{
					session.getTransaction().rollback();
					session.close();
				}
				
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch (TaskException e) {
			// TODO Auto-generated catch block
			if(session != null)
			{
				session.getTransaction().rollback();
				session.close();
			}
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		}
		
		  catch(KCException e)
		{
				// TODO Auto-generated catch block
				if(session != null)
				{
					session.getTransaction().rollback();
					session.close();
				}
				Response rb = Response.status(Status.OK)
						.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
				throw new WebApplicationException(rb);
			}
		catch(ConstraintViolationException cve) {
			//kc = KnowledgeComponentHandler.readByExtId(external_kc_id, external_course_id);
			if(session != null)
			{
				session.getTransaction().rollback();
				session.close();
			}
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.KC_TASK_MAP_ALREADY_PRESENT)).build();
			throw new WebApplicationException(rb);
		} 
		catch(Exception e){

			if(session != null)
			{
				session.getTransaction().rollback();
				session.close();
			}
			System.out.println(e.getMessage());
			
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		
	}

	
	

}
