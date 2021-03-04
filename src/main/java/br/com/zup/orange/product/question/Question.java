package br.com.zup.orange.product.question;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.orange.product.Product;
import br.com.zup.orange.user.User;

@Entity
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@NotBlank
	private String title;

	@NotNull
	@ManyToOne
	private Product product;
	
	private LocalDateTime createdAt = LocalDateTime.now();
	
	@NotNull
	@ManyToOne
	private User user;

	@Deprecated
	public Question() {}
	
	public Question(@NotBlank String title, @NotNull Product product, @NotNull User user) {
		this.title = title;
		this.product = product;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Product getProduct() {
		return product;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}
	
	


}
