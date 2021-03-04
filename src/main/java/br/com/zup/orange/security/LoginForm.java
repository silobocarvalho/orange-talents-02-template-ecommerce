package br.com.zup.orange.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class LoginForm {

	private String email;
	private String password;

	
	
	public LoginForm(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public UsernamePasswordAuthenticationToken convertToToken() {
		return new UsernamePasswordAuthenticationToken(email, password);
	}

}
