package br.com.zup.orange.product;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.orange.category.Category;
import br.com.zup.orange.user.User;
import br.com.zup.orange.validator.IsValidImage;
import br.com.zup.orange.validator.UniqueValue;

@Entity
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Valid
	@ManyToOne
	private User owner;

	@NotNull
	@Valid
	@ManyToOne
	private Product product;
	
	@NotBlank
	@IsValidImage(domainClass = Image.class)
	String link;

	@Deprecated
	public Image() {
	}

	public Image(@NotNull @Valid User owner, @NotNull @Valid Product product, @NotBlank @Valid String link) {
		this.owner = owner;
		this.product = product;
		this.link = link;
	}

	public Long getId() {
		return id;
	}

	public User getOwner() {
		return owner;
	}

	public Product getProduct() {
		return product;
	}
	
	public String getLink() {
		return link;
	}

	@Override
	public String toString() {
		return "Image [id=" + id + ", owner=" + owner.getEmail() + ", product=" + product.toString() + "]";
	}

	
}
