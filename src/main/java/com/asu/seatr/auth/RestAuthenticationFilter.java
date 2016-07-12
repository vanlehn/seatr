package com.asu.seatr.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.UserException;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;


public class RestAuthenticationFilter implements javax.servlet.Filter {
	public static final String AUTHENTICATION_HEADER = "Authorization";

	public JSONObject requestParamsToJSON(MultiReadHttpServletRequest request) throws Exception {
		StringBuffer jb = new StringBuffer();
		String line = null;
		JSONObject jsonObject = new JSONObject();

		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) { throw e; }

		jsonObject = new JSONObject(jb.toString());

		return jsonObject;

	}

	// checks if it is a course create request
	// checking directly with the URL, fails when the URL routes are changed
	private boolean isCourseCreateRequest(ServletRequest request) {
		String path = ((HttpServletRequest) request).getPathInfo();
		String re1="(\\/analyzer\\/\\w+\\/courses)";
		Pattern p = Pattern.compile(re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(path);
		return m.find() && ((HttpServletRequest) request).getMethod().equals("POST");
	}

	// checks if its a superadmin request
	// checking directly with the URL, fails when the URL routes are changed
	private boolean isSuperAdminRequest(ServletRequest request) {
		String path = ((HttpServletRequest) request).getPathInfo();
		String re1="(\\/superadmin\\/users)";
		Pattern p = Pattern.compile(re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(path);
		return m.find();

	}
	private boolean isHelloRequest(ServletRequest request){
		String path = ((HttpServletRequest) request).getPathInfo();
		String re1="(\\/hello)";
		Pattern p = Pattern.compile(re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(path);
		return m.find();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filter) throws IOException, ServletException {
		response.setContentType(MediaType.APPLICATION_JSON);		

		boolean isCourseCreate = isCourseCreateRequest(request) && ((HttpServletRequest) request).getMethod().equals("POST"); 
		boolean isSuperAdmin = isSuperAdminRequest(request);		
		boolean isHello = isHelloRequest(request);
		if (request instanceof HttpServletRequest) {
			//HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			MultiReadHttpServletRequest multiReadHttpServletRequest = new MultiReadHttpServletRequest((HttpServletRequest)request);
			String authCredentials = multiReadHttpServletRequest
					.getHeader(AUTHENTICATION_HEADER);


			String external_course_id = null;			
			boolean authenticationStatus = false, exception = false;
			try {
				if (isSuperAdmin) {
					external_course_id = null;
				}
				else if(((HttpServletRequest) request).getMethod().equals("GET") || ((HttpServletRequest) request).getMethod().equals("DELETE")) {
					// if its a get or a delete request, get the course_id from URL paramters
					external_course_id = request.getParameter("external_course_id");
				} else if(!isHello){
					// if its a POST or a PUT, get the course_id from request body
					JSONObject requestParams = requestParamsToJSON(multiReadHttpServletRequest);
					external_course_id = requestParams.get("external_course_id").toString();
				}
				if(isHello)
				{
					authenticationStatus = true;
				}
				else
				{
				AuthenticationService authenticationService = new AuthenticationService();

				authenticationStatus = authenticationService
						.authenticate(authCredentials, external_course_id, isCourseCreate, isSuperAdmin);
				}

			} catch (UserException e) {
				exception = true;
				authenticationStatus = false;
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;
				httpServletResponse.setContentType(MediaType.APPLICATION_JSON);
				httpServletResponse													
				.setStatus(Status.UNAUTHORIZED.getStatusCode());
				httpServletResponse.getWriter().write(MyResponse.build(e.getMyStatus(), e.getMyMessage()));
			} catch (CourseException e) {
				exception = true;
				authenticationStatus = false;
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;
				httpServletResponse.setContentType(MediaType.APPLICATION_JSON);
				httpServletResponse							
				.setStatus(Status.FORBIDDEN.getStatusCode());
				httpServletResponse.getWriter().write(MyResponse.build(e.getMyStatus(), e.getMyMessage()));
			} catch (Exception e) {
				exception = true;
				authenticationStatus = false;
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;
				httpServletResponse.setContentType(MediaType.APPLICATION_JSON);
				httpServletResponse													
				.setStatus(Status.BAD_REQUEST.getStatusCode());
				httpServletResponse.getWriter().write(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST));
			} 	

			if (authenticationStatus) {
				// if the auth is a success
				filter.doFilter(multiReadHttpServletRequest, response);
			} else if (!exception){
				if (response instanceof HttpServletResponse) {
					HttpServletResponse httpServletResponse = (HttpServletResponse) response;
					httpServletResponse.setContentType(MediaType.APPLICATION_JSON);
					httpServletResponse													
					.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					httpServletResponse.getWriter().write(MyResponse.build(MyStatus.ERROR, MyMessage.AUTHENTICATION_FAILED));
				}
			}

		}


	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}