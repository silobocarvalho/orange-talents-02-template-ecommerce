package br.com.zup.orange.order.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.orange.order.enums.PaymentStatus;

public class PaymentGatewayPagseguroReturnRequest {

	@NotBlank
	private String transactionId;
	
	@NotBlank
	private String paymentGatewayId;
	
	@NotNull
	PaymentStatus paymentStatus;

	public PaymentGatewayPagseguroReturnRequest(@NotBlank String transactionId, @NotBlank String paymentGatewayId,
			@NotBlank PaymentStatus paymentStatus) {
		this.transactionId = transactionId;
		this.paymentGatewayId = paymentGatewayId;
		this.paymentStatus = paymentStatus;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getPaymentGatewayId() {
		return paymentGatewayId;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}
	
	
	
}
