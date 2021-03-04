package br.com.zup.orange.Client;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.orange.category.Category;
import br.com.zup.orange.category.CategoryFormInDto;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Transactional
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@PersistenceContext
	EntityManager entityManager;

	String urlHost = "http://localhost:8080";

	@Test
	@DisplayName("Should create a new Category and return 200")
	@WithMockUser(username = "user@email.com", password = "123456", roles = "MODERATOR")
	void createNewCategory() throws JsonProcessingException, Exception {

		CategoryFormInDto newCategoryFormIn = new CategoryFormInDto("Category Test Name", new Category("Sub Category"));

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/categories").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCategoryFormIn)))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Should try to create new Category with null name")
	void createNewCategoryWithNullName() throws JsonProcessingException, Exception {

		CategoryFormInDto newCategoryFormIn = new CategoryFormInDto(null, new Category("Sub Category"));

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/categories").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCategoryFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should try to create new Category with null subcategory name")
	@WithMockUser(username = "user@email.com", password = "123456", roles = "MODERATOR")
	void createNewCategoryWithNullSubcategoryName() throws JsonProcessingException, Exception {

		CategoryFormInDto newCategoryFormIn = new CategoryFormInDto("Category", null);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/categories").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCategoryFormIn)))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	}
	
	@Test
	@DisplayName("Should try to create new Category with non authorized user")
	@WithMockUser(username = "user@email.com", password = "123456", roles = "USER")
	void createNewCategoryWithNonAuthorizedUser() throws JsonProcessingException, Exception {

		CategoryFormInDto newCategoryFormIn = new CategoryFormInDto("Category", new Category("Sub Category"));

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/categories").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCategoryFormIn)))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Should try to create new Category with empty name")
	void createNewCategorytWithEmptyName() throws JsonProcessingException, Exception {

		CategoryFormInDto newCategoryFormIn = new CategoryFormInDto("", new Category("Sub Category"));

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/categories").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCategoryFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should try to create new Category with duplicated name")
	void createNewCategoryWithDuplicatedName() throws JsonProcessingException, Exception {

		Category subCategory = new Category("Sub Category");
		
		CategoryFormInDto newCategoryFormIn = new CategoryFormInDto("Category", subCategory);

		//It is necessary to persist subcategory name first to not invalidate unique name value constraint.
		entityManager.persist(subCategory);
		
		entityManager.persist(newCategoryFormIn.toModel());

		// 0 = no category registered
		List<Category> categories = entityManager
				.createQuery("select c from Category c where c.name = :catName", Category.class)
				.setParameter("catName", newCategoryFormIn.getName())
				.getResultList();

		assertTrue(categories.size() == 1);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/categories").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCategoryFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

}
