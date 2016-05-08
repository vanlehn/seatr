package com.asu.seatr.api.analyzer2;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
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
import com.asu.seatr.models.analyzers.kc.K_A2;
import com.asu.seatr.models.analyzers.task_kc.TK_A2;
import com.asu.seatr.rest.models.analyzer2.KAReader2;
import com.asu.seatr.rest.models.analyzer2.TKAReader2;
import com.asu.seatr.rest.models.analyzer2.TKReader2;
import com.asu.seatr.rest.models.interfaces.TKAReaderI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;


@Path("analyzer/2/kc")
public class KCAPI {

	static Logger logger = Logger.getLogger(KCAPI.class);
	
	@Path("/createkc")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createKC2(KAReader2 kaReader)
	{
		
		K_A2 ka2 = new K_A2();
		try {
			ka2.createKC(kaReader.getExternal_kc_id(), kaReader.getExternal_course_id());
			KCAnalyzerHandler.save(ka2);
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_CREATED)).build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch (KCException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			logger.error(e.getStackTrace());
			System.out.println(e.getMessage());
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	
	/**
	 * Created KC_TASK mapping entry in tk_a2 table
	 * If replace is true, first all records are truncated and then mappings inserted
	 * If replace is false, records are directly inserted in the table. Duplicate records not allowed.
	 * @param tkReader
	 * @return
	 */
	@Path("/mapkctask")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response mapKcToTask(TKReader2 tkReader)
	{
		Session session = null;
		try {
			boolean replace = tkReader.getReplace();
			String external_course_id  = tkReader.getExternal_course_id();
			TKAReaderI[] tkReaderArray = tkReader.getTkaReader();
			
			if(replace)
			{
				Course course = CourseHandler.getByExternalId(external_course_id);
				session = KCAnalyzerHandler.hqlDeleteByCourse("A2", course,false);
			}
			TK_A2 tk2Array[] = new TK_A2[tkReaderArray.length];
			for(int i = 0; i<tkReaderArray.length;i++)
			{
				TK_A2 tk2 = new TK_A2();
				TKAReader2 tkReader2 = (TKAReader2) tkReaderArray[i];
				KnowledgeComponent kc = KnowledgeComponentHandler.readByExtId(tkReader2.getExternal_kc_id(), external_course_id);
				Task task;
				task = TaskHandler.readByExtId(tkReader2.getExternal_task_id(), external_course_id);
				tk2.setKc(kc);
				tk2.setTask(task);
				tk2Array[i] = tk2;
			}
			session = TaskKCAnalyzerHandler.batchSave(tk2Array,false,session);
			if(session != null)
			
				{
				session.getTransaction().commit();
				session.close();
				}

			
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_TASK_CREATED)).build();
			} catch (CourseException e) {
				if(session != null)
				{
					session.getTransaction().rollback();
					session.close();
				}
				
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch (TaskException e) {
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
			logger.error(e.getStackTrace());
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		
	}

	
	

}
