package br.com.zup.orange.product.evaluation;

import javax.persistence.EntityManager;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.orange.product.Product;
import br.com.zup.orange.user.User;
import net.bytebuddy.asm.Advice.This;

public class ProductEvaluationRequest {

	@NotBlank
	private String title;

	@NotNull
	@Min(value = 1)
	@Max(value = 5)
	private int rating;

	@NotBlank
	@Size(max = 500)
	private String description;

	@NotNull
	private long productId;

	public ProductEvaluationRequest(@NotBlank String title, @NotNull @Size(min = 1, max = 5) int rating,
			@NotBlank @Size(max = 500) String description, @NotNull long productId) {
		this.title = title;
		this.rating = rating;
		this.description = description;
		this.productId = productId;
	}

	public String getTitle() {
		return title;
	}

	public int getRating() {
		return rating;
	}

	public String getDescription() {
		return description;
	}

	public long getProductId() {
		return productId;
	}

	@Override
	public String toString() {
		return "ProductEvaluationRequest [title=" + title + ", rating=" + rating + ", description=" + description
				+ ", productId=" + productId + "]";
	}

	public ProductEvaluation toModel(EntityManager entityManager, User user) {

		Product productFromDb = entityManager.find(Product.class, this.productId);

		if (productFromDb == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		ProductEvaluation productEvaluation = new ProductEvaluation(this.title, this.rating, this.description,
				productFromDb, user);

		return productEvaluation;
	}

}
