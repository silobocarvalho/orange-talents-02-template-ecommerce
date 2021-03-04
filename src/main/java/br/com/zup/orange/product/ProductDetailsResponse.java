package br.com.zup.orange.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.orange.product.evaluation.ProductEvaluation;
import br.com.zup.orange.product.question.Question;

public class ProductDetailsResponse {

	@NotNull
	private long productId;

	@NotBlank
	private String name;

	@NotNull
	@Positive
	private BigDecimal price;

	@NotNull
	@PositiveOrZero
	private int quantity;

	@NotBlank
	@Size(max = 1000)
	private String description;

	@NotNull
	private long categoryId;

	@NotBlank
	private String categoryName;

	@NotNull
	@Size(min = 3)
	Map<String, String> characteristics;

	@NotNull
	double ratingMean = 0.0;

	@NotNull
	int totalNumberOfRatings = 0;

	@NotNull
	List<String> productEvaluations = new ArrayList<>();

	@NotNull
	List<String> questions = new ArrayList<>();

	@NotNull
	List<String> imagesLinks = new ArrayList<>();

	@NotNull
	EntityManager entityManager;

	public ProductDetailsResponse(@NotNull @Valid long productId, @NotNull @Valid EntityManager entityManager) {
		this.productId = productId;
		this.entityManager = entityManager;

		// Get Product from Database
		Product product = entityManager.find(Product.class, productId);

		if (product == null) {
			// Product does not exists
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		// Set product attributes
		this.name = product.getName();
		this.price = product.getPrice();
		this.quantity = product.getQuantity();
		this.description = product.getDescription();
		this.categoryId = product.getCategory().getId();
		this.categoryName = product.getCategory().getName();
		this.characteristics = product.getCharacteristics();

		calculateRatingsAndProductEvaluations();

		getAllQuestionsTitles();

		getAllImagesLinks();

	}

	private void calculateRatingsAndProductEvaluations() {
		Query query = entityManager
				.createQuery("select pe from ProductEvaluation pe where pe.product.id = :product_id");
		query.setParameter("product_id", this.productId);

		List<ProductEvaluation> productEvaluationsListFromDatabase = query.getResultList();

		for (ProductEvaluation productEvaluation : productEvaluationsListFromDatabase) {
			this.ratingMean += productEvaluation.getRating();

			productEvaluations.add(productEvaluation.getTitle());
		}

		if (this.ratingMean > 0) {
			this.ratingMean = this.ratingMean / productEvaluationsListFromDatabase.size();
		}

		this.totalNumberOfRatings = productEvaluationsListFromDatabase.size();
	}

	private void getAllQuestionsTitles() {

		Query query = entityManager.createQuery("select q from Question q where q.product.id = :product_id");
		query.setParameter("product_id", this.productId);

		List<Question> questionsListFromDatabase = query.getResultList();

		for (Question question : questionsListFromDatabase) {
			this.questions.add(question.getTitle());
		}
	}

	private void getAllImagesLinks() {
		Query query = entityManager.createQuery("select i from Image i where i.product.id = :product_id");
		query.setParameter("product_id", this.productId);
		List<Image> imagesListFromDatabase = query.getResultList();
		for (Image image : imagesListFromDatabase) {
			this.imagesLinks.add(image.getLink());
		}
	}

	public long getProductId() {
		return productId;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getDescription() {
		return description;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public Map<String, String> getCharacteristics() {
		return characteristics;
	}

	public double getRatingMean() {
		return ratingMean;
	}

	public int getTotalNumberOfRatings() {
		return totalNumberOfRatings;
	}

	public List<String> getProductEvaluations() {
		return productEvaluations;
	}

	public List<String> getQuestions() {
		return questions;
	}

	public List<String> getImagesLinks() {
		return imagesLinks;
	}

}
