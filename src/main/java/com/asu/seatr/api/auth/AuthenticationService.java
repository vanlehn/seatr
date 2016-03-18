package com.asu.seatr.api.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.StringTokenizer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.asu.seatr.exceptions.UserException;
import com.asu.seatr.handlers.UserHandler;
import com.asu.seatr.models.User;

public class AuthenticationService {
	
	private static final String UNICODE_FORMAT = "ISO-8859-1";

    
    private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x13, (byte) 0x19, (byte) 0x14,
        (byte) 0xde, (byte) 0x23, (byte) 0x15, (byte) 0x15,
    };
    
    private static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
    }
    
    private static String base64Encode(byte[] bytes) {
        // NB: This class is internal, and you probably should use another impl
        return DatatypeConverter.printBase64Binary(bytes); 
    }

    private static String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal, and you probably should use another impl
        return DatatypeConverter.parseBase64Binary(property);
    }



	public boolean authenticate(String authCredentials) {

		if (null == authCredentials)
			return false;
		// header value format will be "Basic encodedstring" for Basic
		// authentication. Example "Basic YWRtaW46YWRtaW4="
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
		final String username = tokenizer.nextToken();
		final String password = tokenizer.nextToken();
		String encryptedPassword = "";
		try {
			encryptedPassword = encrypt(password);
		} catch (UnsupportedEncodingException | GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// we have fixed the userid and password as admin
		// call some UserService/LDAP here
		boolean authenticationStatus = false;
		try {
			User user = UserHandler.read(username);
			authenticationStatus = user.getPassword().equals(encryptedPassword);			
			
		} catch (UserException e) {
			//e.printStackTrace();
			authenticationStatus = false;
		}		
		
		return authenticationStatus;
	}

	public static void main(String[] args) {
		System.out.println("hi...\n");
		try {
			System.out.println(encrypt("hello"));
			String encrypted = encrypt("hello");
			System.out.println(decrypt(encrypted));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}