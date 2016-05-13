package com.asu.seatr.api;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.asu.seatr.auth.AuthenticationService;
import com.asu.seatr.exceptions.UserException;
import com.asu.seatr.handlers.UserHandler;
import com.asu.seatr.models.User;
import com.asu.seatr.rest.models.UserReader;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

// Route for user creation
@Path("/superadmin")
public class AdminAPI {

	static Logger logger = Logger.getLogger(AdminAPI.class);

	@Path("/users")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUsers(UserReader userReader) {
		Long requestTimestamp = System.currentTimeMillis();
		try {
			if (!Utilities.checkExists(userReader.getUsername())) {
				throw new UserException(MyStatus.ERROR, MyMessage.USERNAME_MISSING);
			}
			if (!Utilities.checkExists(userReader.getPassword())) {
				throw new UserException(MyStatus.ERROR, MyMessage.PASSWORD_MISSING);
			}

			User user = new User();
			user.setUsername(userReader.getUsername());			
			user.setPassword(AuthenticationService.encrypt(userReader.getPassword()));
			UserHandler.save(user);
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.USER_CREATED)).build();
		} catch (UserException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		} catch (Exception e) {
			logger.error("Exception while creating users", e);
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
