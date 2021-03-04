package br.com.zup.orange.order;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.orange.order.enums.PaymentStatus;

@Entity
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private PaymentStatus paymentStatus;

	@NotBlank
	private String gatewayTransactionId;

	@NotNull
	private LocalDateTime operationTime;

	@NotNull
	@Valid
	@ManyToOne
	private OrderEntity order;

	public Payment(@NotNull PaymentStatus paymentStatus, @NotBlank String gatewayTransactionId,
			@NotNull @Valid OrderEntity order) {
		this.paymentStatus = paymentStatus;
		this.gatewayTransactionId = gatewayTransactionId;
		this.operationTime = LocalDateTime.now();
		this.order = order;
	}

}
