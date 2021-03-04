package br.com.zup.orange.Client;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

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
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.orange.order.OrderEntity;
import br.com.zup.orange.order.OrderFormIn;
import br.com.zup.orange.order.enums.OrderStatus;
import br.com.zup.orange.order.enums.PaymentStatus;
import br.com.zup.orange.order.enums.PaymentType;
import br.com.zup.orange.order.requests.PaymentGatewayPagseguroReturnRequest;
import br.com.zup.orange.order.requests.PaymentGatewayPaypalReturnRequest;
import br.com.zup.orange.product.Product;
import br.com.zup.orange.user.User;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Transactional
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@PersistenceContext
	EntityManager entityManager;

	String urlHost = "http://localhost:8080";

	@Test
	@DisplayName("Order a product")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addOrder() throws Exception {

		OrderFormIn newOrderFormIn = new OrderFormIn(1L, 1, PaymentType.Paypal);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/order").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newOrderFormIn)))
				.andExpect(MockMvcResultMatchers.status().isFound());
		
		System.out.println("sadadsasd");
	}

	@Test
	@DisplayName("Should try to make a order with quantity > stock")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addOrderWithQuantityHigherThanStock() throws Exception {

		OrderFormIn newOrderFormIn = new OrderFormIn(1L, Integer.MAX_VALUE, PaymentType.Paypal);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/order").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newOrderFormIn)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Order a product with invalid payment method")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void addOrderWithInvalidPaymentMethod() throws Exception {

		OrderFormIn newOrderFormIn = new OrderFormIn(1L, 1, PaymentType.Invalid);

		mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/order").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newOrderFormIn)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Should send a return request from Pagseguro Payment gateway: APPROVED")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void completeOrderUsingPagseguroGateway() throws Exception {

		Product product = entityManager.find(Product.class, 1L);
		User buyer = entityManager.find(User.class, 1L);
		
		
		OrderEntity order = new OrderEntity(product, 1, PaymentType.Pagseguro, buyer, OrderStatus.APPROVED);
		
		
		PaymentGatewayPagseguroReturnRequest payment = new PaymentGatewayPagseguroReturnRequest(order.getTransactionId(), "gateway-id-14741", PaymentStatus.SUCCESS);
		
		ResultActions results = mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/order/pagseguro-return")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(payment)))
		.andExpect(MockMvcResultMatchers.status().isOk());
		
		String jsonResult = results.andReturn().getResponse().getContentAsString();
		
		System.out.println(jsonResult);
	}
	
	@Test
	@DisplayName("Should send a return request from Pagseguro Payment gateway: NOT APPROVED")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void completeOrderUsingPagseguroGatewayNotApproved() throws Exception {

		Product product = entityManager.find(Product.class, 1L);
		User buyer = entityManager.find(User.class, 1L);
		
		
		OrderEntity order = new OrderEntity(product, 1, PaymentType.Pagseguro, buyer, OrderStatus.APPROVED);
		
		
		PaymentGatewayPagseguroReturnRequest payment = new PaymentGatewayPagseguroReturnRequest(order.getTransactionId(), "gateway-id-14741", PaymentStatus.FAILED);
		
		ResultActions results = mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/order/pagseguro-return")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(payment)))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		String jsonResult = results.andReturn().getResponse().getContentAsString();
		
		System.out.println(jsonResult);
	}
	
	@Test
	@DisplayName("Should send a return request from Paypal Payment gateway: APPROVED")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void completeOrderUsingPaypalGateway() throws Exception {

		Product product = entityManager.find(Product.class, 1L);
		User buyer = entityManager.find(User.class, 1L);
		
		
		OrderEntity order = new OrderEntity(product, 1, PaymentType.Paypal, buyer, OrderStatus.APPROVED);
		
		//Sucess = 1
		PaymentGatewayPaypalReturnRequest payment = new PaymentGatewayPaypalReturnRequest(order.getTransactionId(), "gateway-id-14741", 1);
		
		ResultActions results = mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/order/paypal-return")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(payment)))
		.andExpect(MockMvcResultMatchers.status().isOk());
		
		String jsonResult = results.andReturn().getResponse().getContentAsString();
		
		System.out.println(jsonResult);
	}
	
	@Test
	@DisplayName("Should send a return request from Paypal Payment gateway: NOT APPROVED")
	@WithMockUser(username = "user1@email.com", password = "123456", roles = "MODERATOR")
	void completeOrderUsingPaypalGatewayNotApproved() throws Exception {

		Product product = entityManager.find(Product.class, 1L);
		User buyer = entityManager.find(User.class, 1L);
		
		
		OrderEntity order = new OrderEntity(product, 1, PaymentType.Paypal, buyer, OrderStatus.APPROVED);
		
		//Sucess = 1
		PaymentGatewayPaypalReturnRequest payment = new PaymentGatewayPaypalReturnRequest(order.getTransactionId(), "gateway-id-14741", 0);
		
		ResultActions results = mockMvc.perform(MockMvcRequestBuilders.post(urlHost + "/order/paypal-return")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(payment)))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		String jsonResult = results.andReturn().getResponse().getContentAsString();
		
		System.out.println(jsonResult);
	}

	// MAKE MORE TESTS ;-)

}
