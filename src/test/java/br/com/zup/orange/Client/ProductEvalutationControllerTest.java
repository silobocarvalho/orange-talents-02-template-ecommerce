package br.com.zup.orange.Client;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.orange.category.Category;
import br.com.zup.orange.product.evaluation.ProductEvaluationRequest;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Transactional
@ExtendWith(MockitoExtension.class)
class ProductEvalutationControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@PersistenceContext
	EntityManager entityManager;

	String urlHost = "http://localhost:8080";

	@Test
	@DisplayName("Should add a rating to a registered Product from a logged User and return 200")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addRatingToProductFromLoggedUser() throws JsonProcessingException, Exception {

		ProductEvaluationRequest request = new ProductEvaluationRequest("Title 1", 5, "This is a description.", 1L);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products/1/evaluation")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	@DisplayName("Should add a opinition to a registered Product with Invalid Rating number (negative)")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addRatingToProductFromLoggedUserUsingInvalidNegativeRating() throws JsonProcessingException, Exception {

		ProductEvaluationRequest request = new ProductEvaluationRequest("Title 1", -1, "This is a description.", 1);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products/1/evaluation")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}
	
	@Test
	@DisplayName("Should add a opinition to a registered Product with Invalid Rating number (>5)")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addRatingToProductFromLoggedUserUsingInvalidBiggerRating() throws JsonProcessingException, Exception {

		ProductEvaluationRequest request = new ProductEvaluationRequest("Title 1", 6, "This is a description.", 1);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products/1/evaluation")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}
	
	@Test
	@DisplayName("Should add a opinition to a registered Product with Invalid title (empty)")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addRatingToProductFromLoggedUserUsingInvalidEmptyTitle() throws JsonProcessingException, Exception {

		ProductEvaluationRequest request = new ProductEvaluationRequest("", 3, "This is a description.", 1);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products/1/evaluation")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}
	
}
