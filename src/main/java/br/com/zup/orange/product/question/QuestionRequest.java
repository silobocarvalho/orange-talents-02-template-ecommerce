package br.com.zup.orange.product.question;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.orange.product.Product;
import br.com.zup.orange.product.evaluation.ProductEvaluation;
import br.com.zup.orange.user.User;

public class QuestionRequest {

	@NotBlank
	private String title;

	@NotNull
	private long productId;

	public QuestionRequest(@NotBlank String title, @NotNull long productId) {
		this.title = title;
		this.productId = productId;
	}

	public String getTitle() {
		return title;
	}

	public long getProductId() {
		return productId;
	}

	@Override
	public String toString() {
		return "QuestionRequest [title=" + title + ", productId=" + productId + "]";
	}

	public Question toModel(EntityManager entityManager, User user) {

		Product productFromDb = entityManager.find(Product.class, this.productId);

		if (productFromDb == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		Question question = new Question(this.title, productFromDb, user);

		return question;
	}

}
