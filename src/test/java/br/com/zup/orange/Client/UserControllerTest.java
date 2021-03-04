package br.com.zup.orange.Client;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.orange.user.User;
import br.com.zup.orange.user.UserFormInDto;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Transactional
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	AuthenticationManager authManager;

	@Autowired
	br.com.zup.orange.security.TokenService tokenService;

	String urlHost = "http://localhost:8080";

	/*
	@BeforeEach
	@DisplayName("Should authenticate the user before run other tests.")
	void authUser() {

		LoginForm formLogin = new LoginForm("sid@zup.com.br", "123456");

		UsernamePasswordAuthenticationToken loginData = formLogin.convertToToken();

		Authentication auth = authManager.authenticate(loginData);

		String token = tokenService.generateToken(auth);

		System.out.println("token: " + token);

		// ResponseEntity.ok(new TokenDto(token, "Bearer"));
	}
	*/
	

	//#hash password for "123456" = $2a$10$ElqkamBRdlwYGSHCOYFJt.WqsCLc6ouiDEPx9nYZEyADRkun6OEUK');
	@Test
	@DisplayName("Should create a new Client and return 200")
	@WithMockUser(username = "user@email.com", password = "123456", roles = "MODERATOR")
	void createNewClient() throws JsonProcessingException, Exception {

		UserFormInDto newClientFormIn = new UserFormInDto("sid@zup.com.br", "123456");

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newClientFormIn)))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Should try to create new Client with null email")
	void createNewClientWithNullEmail() throws JsonProcessingException, Exception {

		UserFormInDto newClientFormIn = new UserFormInDto(null, "123456");

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newClientFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should try to create new Client with null password")
	void createNewClientWithNullPassword() throws JsonProcessingException, Exception {

		UserFormInDto newClientFormIn = new UserFormInDto("sid@.com.br", null);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newClientFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should try to create new Client with invalid email")
	void createNewClientWithInvalidEmail() throws JsonProcessingException, Exception {

		UserFormInDto newClientFormIn = new UserFormInDto("sid@.com.br", "123456");

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newClientFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should try to create new Client with empty email")
	void createNewClientWithEmptyEmail() throws JsonProcessingException, Exception {

		UserFormInDto newClientFormIn = new UserFormInDto("", "123456");

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newClientFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should try to create new Client with empty password")
	void createNewClientWithEmptyPassword() throws JsonProcessingException, Exception {

		UserFormInDto newClientFormIn = new UserFormInDto("sid@.com.br", "");

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newClientFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should try to create new Client with duplicated email without authorization")
	void createNewClientWithDuplicatedEmailWithoutAuth() throws JsonProcessingException, Exception {

		UserFormInDto newClientFormIn = new UserFormInDto("sid@zup.com.br", "123456");

		entityManager.persist(newClientFormIn.toModel());

		// 0 = no user registered
		List<User> users = entityManager.createQuery("select u from User u where u.email = :email", User.class)
				.setParameter("email", newClientFormIn.getEmail()).getResultList();

		assertTrue(users.size() == 1);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newClientFormIn)))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

	}
	
	@Test
	@DisplayName("Should try to create new Client with duplicated email")
	@WithMockUser(username = "user@email.com", password = "123456", roles = "MODERATOR")
	void createNewClientWithDuplicatedEmailWithAuth() throws JsonProcessingException, Exception {

		UserFormInDto newClientFormIn = new UserFormInDto("sid@zup.com.br", "123456");

		entityManager.persist(newClientFormIn.toModel());

		// 0 = no user registered
		List<User> users = entityManager.createQuery("select u from User u where u.email = :email", User.class)
				.setParameter("email", newClientFormIn.getEmail()).getResultList();

		assertTrue(users.size() == 1);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newClientFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());

	}

}
