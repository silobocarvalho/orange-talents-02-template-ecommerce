package br.com.zup.orange.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityTools {

	public static String encodePassword(String password) {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		return bcrypt.encode(password);

	}
	
}
