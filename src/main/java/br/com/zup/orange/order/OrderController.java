package br.com.zup.orange.order;

import java.net.URI;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zup.orange.order.enums.PaymentType;
import br.com.zup.orange.order.requests.PaymentGatewayPagseguroReturnRequest;
import br.com.zup.orange.order.requests.PaymentGatewayPaypalReturnRequest;
import br.com.zup.orange.order.enums.OrderStatus;
import br.com.zup.orange.order.enums.PaymentStatus;
import br.com.zup.orange.product.Product;
import br.com.zup.orange.repository.OrderRepository;
import br.com.zup.orange.repository.UserRepository;
import br.com.zup.orange.user.User;
import br.com.zup.orange.util.Email;

@RestController
@Validated
@RequestMapping("/order")
public class OrderController {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;

	@PostMapping
	@Transactional
	public ResponseEntity<Object> createOrder(@AuthenticationPrincipal UserDetails loggedUser,
			@RequestBody @Valid OrderFormIn orderFormIn, UriComponentsBuilder uriComponentsBuilder) {

		Product product = entityManager.find(Product.class, orderFormIn.productId);
		if (product == null || orderFormIn.quantityToBuy > product.getQuantity()) {
			// Product does not exists or Not have enough products
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		User userFromDb = isValidUser(loggedUser);

		if (userFromDb == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		OrderEntity order = createOrder(product, orderFormIn.quantityToBuy, orderFormIn.paymentType, userFromDb);

		entityManager.persist(order);

		Email.sendEmail(loggedUser.getUsername(), product.getOwner().getUsername(), "Your product was sold to XXX ...");

		// Can improve this code putting PaymentURLProcessing inside your Request and
		// calling processURL inside your Request class.
		if (orderFormIn.getPaymentType().equals(PaymentType.Paypal)) {
			String newURL = PaymentURLProcessing.paypal.createURLToPayment(order, uriComponentsBuilder);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(URI.create(newURL));
			return new ResponseEntity<>(headers, HttpStatus.FOUND);

		} else if (orderFormIn.getPaymentType().equals(PaymentType.Pagseguro)) {
			String newURL = PaymentURLProcessing.pagseguro.createURLToPayment(order, uriComponentsBuilder);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(URI.create(newURL));
			return new ResponseEntity<>(headers, HttpStatus.FOUND);
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@Transactional
	private OrderEntity createOrder(Product product, @NotNull @Positive int quantityToBuy, @NotNull PaymentType paymentType,
			User user) {

		OrderEntity order = new OrderEntity(product, quantityToBuy, paymentType, user, OrderStatus.INITIATED);

		Product newProduct = product.sell(quantityToBuy);

		entityManager.persist(newProduct);

		return order;
	}

	@PostMapping(value = "/pagseguro-return")
	@Transactional
	public ResponseEntity<Object> finishOrder(@AuthenticationPrincipal UserDetails loggedUser,
			@RequestBody @Valid PaymentGatewayPagseguroReturnRequest paymentReturn) {

		User userFromDb = isValidUser(loggedUser);
		if (userFromDb == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Optional<OrderEntity> orderFromDb = orderRepository.findByTransactionId(paymentReturn.getTransactionId());
		if (orderFromDb.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		if (paymentReturn.getPaymentStatus().equals(PaymentStatus.FAILED)) {
			String message = "Your payment was not sucessful, try again";
			Email.sendEmail(orderFromDb.get().getProduct().getOwner().getEmail(), loggedUser.getUsername(), message);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} else if (paymentReturn.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {

			Payment payment = new Payment(paymentReturn.getPaymentStatus(), paymentReturn.getPaymentGatewayId(),
					orderFromDb.get());

			entityManager.persist(payment);
			
			generateInvoice(orderFromDb.get().getId(), userFromDb.getUsername());

			updateSellersRanking(orderFromDb.get().getId(), orderFromDb.get().getProduct().getOwner().getId());

			sendConfirmationEmail(orderFromDb.get(), userFromDb);
			
			return ResponseEntity.ok().body(PaymentStatus.SUCCESS);
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		
	}

	@PostMapping(value = "/paypal-return")
	@Transactional
	public ResponseEntity<Object> finishOrder(@AuthenticationPrincipal UserDetails loggedUser,
			@RequestBody @Valid PaymentGatewayPaypalReturnRequest paymentReturn) {

		User userFromDb = isValidUser(loggedUser);
		if (userFromDb == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Optional<OrderEntity> orderFromDb = orderRepository.findByTransactionId(paymentReturn.getTransactionId());
		if (orderFromDb.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		if (paymentReturn.getPaymentStatus() == 0) {
			String message = "Your payment was not sucessful, try again";
			Email.sendEmail(orderFromDb.get().getProduct().getOwner().getEmail(), loggedUser.getUsername(), message);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} else if (paymentReturn.getPaymentStatus() == 1) {

			Payment payment = new Payment(PaymentStatus.SUCCESS, paymentReturn.getPaymentGatewayId(),
					orderFromDb.get());

			entityManager.persist(payment);
			
			generateInvoice(orderFromDb.get().getId(), userFromDb.getUsername());

			updateSellersRanking(orderFromDb.get().getId(), orderFromDb.get().getProduct().getOwner().getId());

			sendConfirmationEmail(orderFromDb.get(), userFromDb);
			
			return ResponseEntity.ok().body(PaymentStatus.SUCCESS);
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	private User isValidUser(UserDetails loggedUser) {

		Optional<User> userFromDb = userRepository.findByEmail(loggedUser.getUsername());

		if (userFromDb.isEmpty()) {
			return null;
		} else {
			return userFromDb.get();
		}
	}
	
	private void sendConfirmationEmail(OrderEntity order, User userFromDb) {

		StringBuilder sb = new StringBuilder();
		sb.append("Buyer: ").append(userFromDb.getUsername());
		sb.append("Order Id: ").append(order.getId());
		sb.append("Payment Type: ").append(order.getPaymentType().toString());
		// Put more information...

		Email.sendEmail(order.getProduct().getOwner().getEmail(), userFromDb.getUsername(), sb.toString());

	}

	private void updateSellersRanking(Long orderId, Long selledId) {
		// Do what you have to do...

	}

	private void generateInvoice(Long orderId, String userEmail) {
		// Do what you have to do... You can create a new route or send a email..
	}
}
