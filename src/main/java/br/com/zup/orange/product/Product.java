package br.com.zup.orange.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
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

import br.com.zup.orange.category.Category;
import br.com.zup.orange.product.question.Question;
import br.com.zup.orange.user.User;

@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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
	@Valid
	@ManyToOne
	private Category category;

	@NotNull
	@Valid
	@ManyToOne
	private User owner;

	@NotNull
	@Size(min = 3)
	@ElementCollection
	@MapKeyColumn(name = "characteristic_name")
	@Column(name = "characteristic_value")
	@CollectionTable(name = "characteristics")
	private Map<String, String> characteristics;

	private LocalDateTime createdAt = LocalDateTime.now();

	@Deprecated
	public Product() {
	}

	public Product(@NotBlank String name, @NotNull @Positive BigDecimal price, @NotNull @PositiveOrZero int quantity,
			@NotBlank @Size(max = 1000) String description, @NotNull @Valid Category category,
			@NotNull @Valid User owner, @NotNull @Size(min = 3) Map<String, String> characteristics) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.description = description;
		this.category = category;
		this.owner = owner;
		this.characteristics = characteristics;
	}

	public User getOwner() {
		return owner;
	}

	public Long getId() {
		return id;
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

	public Category getCategory() {
		return category;
	}

	public Map<String, String> getCharacteristics() {
		return characteristics;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", price=" + price + ", quantity=" + quantity + ", description="
				+ description + ", category=" + category + ", Owner=" + owner + ", characteristics=" + characteristics
				+ ", createdAt=" + createdAt + "]";
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllQuestions(EntityManager entityManager) {

		// Never return the Questions from database, it contains user sensitive data as
		// email and password.

		Query query = entityManager.createQuery("select q from Question q where q.product.id = :product_id");
		query.setParameter("product_id", this.id);
		List<Question> questionsListFromDatabase = query.getResultList();

		List<String> questionsTitles = new ArrayList<>();

		for (Question question : questionsListFromDatabase) {
			questionsTitles.add(question.getTitle());
		}

		return questionsTitles;
	}

	public Product sell(@NotNull @Positive int quantityToBuy) {
		this.quantity = this.quantity - quantityToBuy;
		return this;
		
	}

}
