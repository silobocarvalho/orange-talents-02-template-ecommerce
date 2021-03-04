package br.com.zup.orange.Client;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.mapping.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.orange.category.Category;
import br.com.zup.orange.category.CategoryFormInDto;
import br.com.zup.orange.product.Product;
import br.com.zup.orange.product.ProductFormInDto;
import br.com.zup.orange.user.User;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Transactional
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@PersistenceContext
	EntityManager entityManager;

	String urlHost = "http://localhost:8080";

	@Test
	@DisplayName("Should create a new Product and return 200")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	// @WithUserDetails("user1@email.com")
	void createNewProduct() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize3 = new HashMap<String, String>();
		characteristicsSize3.put("velocidade", "1TB per second");
		characteristicsSize3.put("battery autonomy", "365 days strong use");
		characteristicsSize3.put("memory", "16GB");

		ProductFormInDto newProductFormIn = new ProductFormInDto("smartphone 1", new BigDecimal(1500.99), 4,
				"good smartphone, used to something.", 1, characteristicsSize3);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newProductFormIn)))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Should create a new Product and verify database")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void createNewProductAndVerifyDatabase() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize3 = new HashMap<String, String>();
		characteristicsSize3.put("velocidade", "1TB per second");
		characteristicsSize3.put("battery autonomy", "365 days strong use");
		characteristicsSize3.put("memory", "16GB");

		ProductFormInDto newProductFormIn = new ProductFormInDto("smartphone 1", new BigDecimal(1500.99), 4,
				"good smartphone, used to something.", 1, characteristicsSize3);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newProductFormIn)));

		// 0 = no product registered with that name
		List<Product> products = entityManager
				.createQuery("select p from Product p where p.name = :name", Product.class)
				.setParameter("name", newProductFormIn.getName()).getResultList();

		assertTrue(products.size() == 1);

	}

	@Test
	@DisplayName("Should create a new Product with user email not registered.")
	@WithMockUser(username = "not.exists.email@email.com", password = "123456", roles = "MODERATOR")
	void createNewProductInvalidUserEmail() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize3 = new HashMap<String, String>();
		characteristicsSize3.put("velocidade", "1TB per second");
		characteristicsSize3.put("battery autonomy", "365 days strong use");
		characteristicsSize3.put("memory", "16GB");

		ProductFormInDto newProductFormIn = new ProductFormInDto("smartphone 1", new BigDecimal(1500.99), 4,
				"good smartphone.", 1, characteristicsSize3);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newProductFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should create a new Product with empty name.")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void createNewProductWithEmptyName() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize3 = new HashMap<String, String>();
		characteristicsSize3.put("velocidade", "1TB per second");
		characteristicsSize3.put("battery autonomy", "365 days strong use");
		characteristicsSize3.put("memory", "16GB");

		ProductFormInDto newProductFormIn = new ProductFormInDto("", new BigDecimal(1500.99), 4, "good smartphone.", 1,
				characteristicsSize3);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newProductFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should create a new Product with price = 0.")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void createNewProductWithPriceZero() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize3 = new HashMap<String, String>();
		characteristicsSize3.put("velocidade", "1TB per second");
		characteristicsSize3.put("battery autonomy", "365 days strong use");
		characteristicsSize3.put("memory", "16GB");

		ProductFormInDto newProductFormIn = new ProductFormInDto("smartphone 1", new BigDecimal(0), 4,
				"good smartphone.", 1, characteristicsSize3);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newProductFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should create a new Product with quantity = -1.")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void createNewProductWithNegativeQuantity() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize3 = new HashMap<String, String>();
		characteristicsSize3.put("velocidade", "1TB per second");
		characteristicsSize3.put("battery autonomy", "365 days strong use");
		characteristicsSize3.put("memory", "16GB");

		ProductFormInDto newProductFormIn = new ProductFormInDto("smartphone 1", new BigDecimal(1500.99), -1,
				"good smartphone.", 1, characteristicsSize3);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newProductFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should create a new Product with invalid category id.")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void createNewProductWithoutCategoryId() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize3 = new HashMap<String, String>();
		characteristicsSize3.put("velocidade", "1TB per second");
		characteristicsSize3.put("battery autonomy", "365 days strong use");
		characteristicsSize3.put("memory", "16GB");

		Assertions.assertThrows(NestedServletException.class, () -> {

			ProductFormInDto newProductFormIn = new ProductFormInDto("smartphone 1", new BigDecimal(1500.99), 4,
					"good smartphone.", -1, characteristicsSize3);

			mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(newProductFormIn)));
		});

	}

	@Test
	@DisplayName("Should create a new Product with invalid number of characteristics.")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void createNewProductWithInvalidNumberOfCharacteristics() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize2 = new HashMap<String, String>();
		characteristicsSize2.put("velocidade", "1TB per second");
		characteristicsSize2.put("battery autonomy", "365 days strong use");

		ProductFormInDto newProductFormIn = new ProductFormInDto("smartphone 1", new BigDecimal(1500.99), 4,
				"good smartphone.", 1, characteristicsSize2);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newProductFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@DisplayName("Should create a new Product with duplicated characteristic name.")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void createNewProductWithCharacteristicNameDuplicated() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize3 = new HashMap<String, String>();
		characteristicsSize3.put("velocity", "1TB per second");
		characteristicsSize3.put("battery autonomy", "365 days strong use");
		characteristicsSize3.put("velocity", "1TB per second");

		ProductFormInDto newProductFormIn = new ProductFormInDto("smartphone 1", new BigDecimal(1500.99), 4,
				"good smartphone.", 1, characteristicsSize3);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newProductFormIn)))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	// IMAGES

	@Test
	@DisplayName("Should add a image to a registered Product from a logged User and return 200")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addImageToProductFromLoggedUser() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize3 = new HashMap<String, String>();
		characteristicsSize3.put("velocidade", "1TB per second");
		characteristicsSize3.put("battery autonomy", "365 days strong use");
		characteristicsSize3.put("memory", "16GB");

		String imageLink = "http://www.zup.com.br/image.png";
		// Add image
		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products/1/images")
				.contentType(MediaType.APPLICATION_JSON).content(imageLink))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Should add a image to a registered Product using not owner User")
	@WithMockUser(username = "moderator@email.com", password = "123456", roles = "MODERATOR")
	void addImageToProductUsingNotOwnerUser() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize3 = new HashMap<String, String>();
		characteristicsSize3.put("velocidade", "1TB per second");
		characteristicsSize3.put("battery autonomy", "365 days strong use");
		characteristicsSize3.put("memory", "16GB");

		String imageLink = "http://www.zup.com.br/image.png";
		// Add image
		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products/1/images")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(imageLink)))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Should add a image to a registered Product from a logged User with invalid image format")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addImageToProductFromLoggedUserWithInvalidImageFormat() throws JsonProcessingException, Exception {

		HashMap<String, String> characteristicsSize3 = new HashMap<String, String>();
		characteristicsSize3.put("velocidade", "1TB per second");
		characteristicsSize3.put("battery autonomy", "365 days strong use");
		characteristicsSize3.put("memory", "16GB");

		String imageLink = "http://www.zup.com.br/image.doc";

		Assertions.assertThrows(NestedServletException.class, () -> {
			mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/products/1/images")
					.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(imageLink)))
					.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		});

	}

	// PRODUCT DETAILS

	@Test
	@DisplayName("Should get all details about a Product")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void getDetailsProduct() throws JsonProcessingException, Exception {

		ResultActions results = mockMvc
				.perform(MockMvcRequestBuilders.get(urlHost + "/products/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());

		String jsonResult = results.andReturn().getResponse().getContentAsString();
		
		System.out.println(jsonResult);

	}

}
