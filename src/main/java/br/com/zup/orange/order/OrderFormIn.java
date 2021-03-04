package br.com.zup.orange.order;

import java.math.BigDecimal;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import br.com.zup.orange.order.enums.PaymentType;

public class OrderFormIn {

	@NotNull
	long productId;
	
	@NotNull
	@Positive
	int quantityToBuy;
	
	@NotNull
	PaymentType paymentType;

	public OrderFormIn(@NotNull long productId, @NotNull @Positive int quantityToBuy,
			@NotNull PaymentType paymentType) {
		this.productId = productId;
		this.quantityToBuy = quantityToBuy;
		this.paymentType = paymentType;
	}

	public long getProductId() {
		return productId;
	}

	public int getQuantityToBuy() {
		return quantityToBuy;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	@Override
	public String toString() {
		return "OrderFormIn [productId=" + productId + ", quantityToBuy=" + quantityToBuy + ", paymentType="
				+ paymentType + "]";
	}

	
	
}
