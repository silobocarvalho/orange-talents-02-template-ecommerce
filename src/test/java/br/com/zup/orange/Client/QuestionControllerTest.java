package br.com.zup.orange.Client;

import static org.junit.jupiter.api.Assertions.*;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.orange.product.evaluation.ProductEvaluationRequest;
import br.com.zup.orange.product.question.QuestionRequest;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Transactional
@ExtendWith(MockitoExtension.class)
class QuestionControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@PersistenceContext
	EntityManager entityManager;

	String urlHost = "http://localhost:8080";

	@Test
	@DisplayName("Should add a question to a registered Product from a logged User and return 200 with list of all questions")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addQuestionToProductFromLoggedUser() throws JsonProcessingException, Exception {

		QuestionRequest request = new QuestionRequest("New title to a question - Test?", 1);

		ResultActions results = mockMvc
				.perform(MockMvcRequestBuilders.post(urlHost + "/products/1/question")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
				.andExpect(MockMvcResultMatchers.status().isOk());

		String jsonResult = results.andReturn().getResponse().getContentAsString();

		//verify if returned json contains the new added question.
		assertTrue(jsonResult.contains(request.getTitle()));
	}
	
	
	@Test
	@DisplayName("Should add a question to a registered Product from a logged User using Empty Question Title")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addQuestionToProductFromLoggedUserWithEmptyTitle() throws JsonProcessingException, Exception {

		QuestionRequest request = new QuestionRequest("", 1);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products/1/question")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}
	
	@Test
	@DisplayName("Should add a question to a registered Product from a logged User using Invalid Product Id")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addQuestionToProductFromLoggedUserWithInvalidProductId() throws JsonProcessingException, Exception {

		QuestionRequest request = new QuestionRequest("New title to a question - Test?", -1);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products/1/question")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}
	
	@Test
	@DisplayName("Should add a question to a registered Product from a logged User using Product Id that does not exists")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addQuestionToProductFromLoggedUserWithProductIdDoesNotExists() throws JsonProcessingException, Exception {

		QuestionRequest request = new QuestionRequest("New title to a question - Test?", Long.MAX_VALUE);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products/1/question")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}
	
	
}
