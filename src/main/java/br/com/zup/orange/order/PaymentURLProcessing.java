package br.com.zup.orange.order;

import org.springframework.web.util.UriComponentsBuilder;

public enum PaymentURLProcessing {

	pagseguro {
		@Override
		public String createURLToPayment(OrderEntity order, UriComponentsBuilder uriComponentsBuilder) {
			/*
			 * Caso a pessoa escolha o pagseguro o seu endpoint deve gerar o seguinte
			 * redirect(302): Retorne o endereço da seguinte maneira:
			 * pagseguro.com?returnId={idGeradoDaCompra}&redirectUrl={
			 * urlRetornoAppPosPagamento}
			 */

			String pagSeguroReturnURL = uriComponentsBuilder.path("/pagseguro-return/{id}")
					.buildAndExpand(order.getTransactionId()).toString();

			return "pagseguro.com/" + order.getTransactionId() + "?redirectUrl=" + pagSeguroReturnURL;
		}
	},
	paypal {
		@Override
		public String createURLToPayment(OrderEntity order, UriComponentsBuilder uriComponentsBuilder) {
			/*
			 * Caso a pessoa escolha o paypal seu endpoint deve gerar o seguinte
			 * redirect(302): Retorne o endereço da seguinte maneira:
			 * paypal.com/{idGeradoDaCompra}?redirectUrl={urlRetornoAppPosPagamento}
			 */

			String pagSeguroReturnURL = uriComponentsBuilder.path("/paypal-return/{id}")
					.buildAndExpand(order.getTransactionId()).toString();

			return "paypal.com/" + order.getTransactionId() + "?redirectUrl=" + pagSeguroReturnURL;
		}
	};

	abstract String createURLToPayment(OrderEntity order, UriComponentsBuilder uriComponentsBuilder);

}
