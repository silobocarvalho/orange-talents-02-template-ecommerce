package br.com.zup.orange.product.evaluation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.zup.orange.product.Product;
import br.com.zup.orange.user.User;

@Entity
public class ProductEvaluation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
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
	@ManyToOne
	private Product product;
	
	@NotNull
	@ManyToOne
	private User user;

	@Deprecated
	public ProductEvaluation() {}
	
	public ProductEvaluation(@NotBlank String title, @NotNull @Size(min = 1, max = 5) int rating,
			@NotBlank @Size(max = 500) String description, @NotNull Product product, @NotNull User user) {
		this.title = title;
		this.rating = rating;
		this.description = description;
		this.product = product;
		this.user = user;
	}

	public Long getId() {
		return id;
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

	public Product getProduct() {
		return product;
	}

	public User getUser() {
		return user;
	}
	
	
	
	
}
