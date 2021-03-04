package br.com.zup.orange.product;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.orange.category.Category;
import br.com.zup.orange.repository.UserRepository;
import br.com.zup.orange.user.User;

public class ProductFormInDto {

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
	private int categoryId;

	@NotNull
	@Size(min = 3)
	Map<String, String> characteristics;

	public ProductFormInDto(@NotBlank String name, @Positive BigDecimal price, @PositiveOrZero int quantity,
			@NotBlank @Size(max = 1000) String description, int categoryId,
			@NotNull @Size(min = 3) Map<String, String> characteristics) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.description = description;
		this.categoryId = categoryId;
		this.characteristics = characteristics;
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

	public int getCategoryId() {
		return categoryId;
	}

	public Map<String, String> getCharacteristics() {
		return characteristics;
	}

	@Override
	public String toString() {
		return "ProductFormInDto [name=" + name + ", price=" + price + ", quantity=" + quantity + ", description="
				+ description + ", categoryId=" + categoryId + ", characteristics=" + characteristics + "]";
	}

	public Product toModel(EntityManager entityManager, User user) {

		Category category = entityManager.find(Category.class, new Long(this.categoryId));

		if(category == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		return new Product(this.name, this.price, this.quantity, this.description, category, user,
				this.characteristics);
	}

}
