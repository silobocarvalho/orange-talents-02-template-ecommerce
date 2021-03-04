package br.com.zup.orange.user;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import br.com.zup.orange.validator.UniqueValue;

public class UserFormInDto {

	@NotBlank
	@Email
	@UniqueValue(domainClass = User.class, fieldName = "email")
	String email;

	@NotBlank
	@Size(min = 6)
	String password;

	public UserFormInDto(@NotBlank @Email String email, @NotBlank @Size(min = 6) String password) {
		this.email = email;
		this.password = password;
	}

	@Override
	public String toString() {
		return "UserFormInDto [email=" + email + ", password=" + password + "]";
	}

	public User toModel() {
		return new User(this.email, this.password);
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

}
