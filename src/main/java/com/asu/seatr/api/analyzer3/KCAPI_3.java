package com.asu.seatr.api.analyzer3;


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
import com.asu.seatr.models.analyzers.kc.K_A3;
import com.asu.seatr.models.analyzers.task_kc.TK_A3;
import com.asu.seatr.rest.models.analyzer3.KAReader3;
import com.asu.seatr.rest.models.analyzer3.TKAReader3;
import com.asu.seatr.rest.models.analyzer3.TKReader3;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

//Analyzer 2 specific routes for KC
@Path("analyzer/3/kc")
public class KCAPI_3 {

	static Logger logger = Logger.getLogger(KCAPI_3.class);

	// Route to create a KC s
	@Path("/createkc")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createKC3(KAReader3 kaReader)
	{		
		Long requestTimestamp = System.currentTimeMillis();
		try {
			if(!Utilities.checkExists(kaReader.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if(!Utilities.checkExists(kaReader.getExternal_kc_id())) {
				throw new KCException(MyStatus.ERROR, MyMessage.KC_ID_MISSING);
			}						

			K_A3 ka3 = new K_A3();
			ka3.createKC(kaReader.getExternal_kc_id(), kaReader.getExternal_course_id());
			ka3.setS_unit(kaReader.getS_unit());
			KCAnalyzerHandler.save(ka3);
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
			logger.error("Exception while creating kc", e);			
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		finally
		{
			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp -  requestTimestamp)/1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
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
	public Response mapKcToTask(TKReader3 tkReader)
	{
		Session session = null;
		Long requestTimestamp = System.currentTimeMillis();
		try {
			boolean replace = tkReader.getReplace();
			String external_course_id  = tkReader.getExternal_course_id();
			/*if(replace)
			{
				Handler.hqlTruncate("TK_A1");
			}*/
			TKAReader3 tkReaderArray[] = tkReader.getTkaReader();
			
			if(replace)
			{
				Course course = CourseHandler.getByExternalId(external_course_id);
				session = KCAnalyzerHandler.hqlDeleteByCourse("A3", course,false);
			}
			TK_A3 tk3Array[] = new TK_A3[tkReaderArray.length];
			for(int i = 0; i<tkReaderArray.length;i++)
			{
				TK_A3 tk3 = new TK_A3();
				TKAReader3 tkReader3 = tkReaderArray[i];
				KnowledgeComponent kc = KnowledgeComponentHandler.readByExtId(tkReader3.getExternal_kc_id(), external_course_id);
				Task task;
				task = TaskHandler.readByExtId(tkReader3.getExternal_task_id(), external_course_id);
				tk3.setKc(kc);
				tk3.setTask(task);
				tk3Array[i] = tk3;
			}
			session = TaskKCAnalyzerHandler.batchSave(tk3Array,false,session);
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
		finally
		{
			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp -  requestTimestamp)/1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
		}
		
	}




}
