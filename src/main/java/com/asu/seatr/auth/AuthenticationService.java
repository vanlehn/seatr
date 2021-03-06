package com.asu.seatr.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.DatatypeConverter;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.UserException;
import com.asu.seatr.handlers.UserCourseHandler;
import com.asu.seatr.handlers.UserHandler;
import com.asu.seatr.models.User;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class AuthenticationService {


	private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
	private static final byte[] SALT = {
			(byte) 0xde, (byte) 0x13, (byte) 0x19, (byte) 0x14,
			(byte) 0xde, (byte) 0x23, (byte) 0x15, (byte) 0x15,
	};

	public static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
	}

	public static String base64Encode(byte[] bytes) {
		// NB: This class is internal, and you probably should use another impl
		return DatatypeConverter.printBase64Binary(bytes); 
	}

	public static String decrypt(String property) throws GeneralSecurityException, IOException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
	}

	public static byte[] base64Decode(String property) throws IOException {
		// NB: This class is internal, and you probably should use another impl
		return DatatypeConverter.parseBase64Binary(property);
	}


	public static StringTokenizer getUsernameAndPassword(String authCredentials) {
		final String encodedUserPassword = authCredentials.replaceFirst("Basic"
				+ " ", "");
		String usernameAndPassword = null;
		try {
			byte[] decodedBytes = DatatypeConverter.parseBase64Binary(
					encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		final StringTokenizer tokenizer = new StringTokenizer(
				usernameAndPassword, ":");
		return tokenizer;
	}

	public boolean authenticate(String authCredentials, String external_course_id, boolean isCourseCreate, boolean isSuperAdminReq) throws UserException, CourseException, 
	UnsupportedEncodingException, GeneralSecurityException{

		if (null == authCredentials)
			return false;
		// header value format will be "Basic encodedstring" for Basic
		// authentication. Example "Basic YWRtaW46YWRtaW4="

		final StringTokenizer tokenizer = getUsernameAndPassword(authCredentials);

		final String username = tokenizer.nextToken();
		final String password = tokenizer.nextToken();		

		boolean authenticationStatus = false;

		// if username or password is empty, fail 
		if(username == null || username.equals("") || password == null || password.equals("")) {
			throw new UserException(MyStatus.ERROR, MyMessage.AUTHENTICATION_FAILED);
		}

		// if it is a superadmin request and username is not superadmin, fail
		if(isSuperAdminReq && !username.equals("superadmin")) {
			authenticationStatus = false;
			return authenticationStatus;
		}

		User user = UserHandler.read(username);
		authenticationStatus = user.getPassword().equals(encrypt(password));

		// only when the user/pass combo match and its not a CourseCreate or SuperAdmin request, check 
		// UserCourseMap whether the user has access to the course
		if(authenticationStatus && !isCourseCreate && !isSuperAdminReq) {
			UserCourseHandler.read(username, external_course_id);										
		}


		return authenticationStatus;
	}

	// Use this to manually enter passwords into the database while creating a record for superadmin
	public static void main(String args[]) {
		try {
			System.out.println(encrypt("super123duper432seatr"));
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
}