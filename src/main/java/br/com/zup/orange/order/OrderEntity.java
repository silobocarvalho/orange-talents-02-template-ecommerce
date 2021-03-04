package br.com.zup.orange.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.zup.orange.category.Category;
import br.com.zup.orange.order.enums.PaymentType;
import br.com.zup.orange.order.enums.OrderStatus;
import br.com.zup.orange.product.Product;
import br.com.zup.orange.product.question.Question;
import br.com.zup.orange.user.User;
import br.com.zup.orange.util.SecurityTools;

@Entity
public class OrderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//Example of generation of transaction ID
	//Should generate the transaction Id to work properly
	@NotBlank
	private String transactionId = "transaction-id-123";
	//SecurityTools.encodePassword(String.valueOf(new Random().nextDouble()));

	
	@NotNull
	@ManyToOne
	private Product product;
	
	@NotNull
	@Positive
	int quantityToBuy;
	
	@NotNull
	PaymentType paymentType;

	@NotNull
	@Valid
	@ManyToOne
	private User buyer;
	
	private OrderStatus status;
	
	@Deprecated
	public OrderEntity() {};

	public OrderEntity(@NotNull Product product, @NotNull @Positive int quantityToBuy, @NotNull PaymentType paymentType,
			@NotNull @Valid User buyer, OrderStatus status) {
		this.product = product;
		this.quantityToBuy = quantityToBuy;
		this.paymentType = paymentType;
		this.buyer = buyer;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public Product getProduct() {
		return product;
	}

	public int getQuantityToBuy() {
		return quantityToBuy;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public User getBuyer() {
		return buyer;
	}

	public OrderStatus getStatus() {
		return status;
	}
	
}
