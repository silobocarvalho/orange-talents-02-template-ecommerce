package br.com.zup.orange.order.requests;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.orange.order.enums.PaymentStatus;

public class PaymentGatewayPaypalReturnRequest {

	@NotBlank
	private String transactionId;
	
	@NotBlank
	private String paymentGatewayId;
	
	@NotNull
	@Min(value = 0)
	@Max(value = 1)
	int paymentStatus;

	public PaymentGatewayPaypalReturnRequest(@NotBlank String transactionId, @NotBlank String paymentGatewayId,
			@NotNull int paymentStatus) {
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

	public int getPaymentStatus() {
		return paymentStatus;
	}
	
	
}
