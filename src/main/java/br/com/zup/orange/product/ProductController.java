package br.com.zup.orange.product;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.orange.repository.UserRepository;
import br.com.zup.orange.user.User;
import br.com.zup.orange.user.UserFormInDto;

@RestController
@Validated
@RequestMapping("/products")
public class ProductController {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@PostMapping
	@Transactional
	public ResponseEntity<Object> addProduct(@AuthenticationPrincipal UserDetails loggedUser,
			@Valid @RequestBody ProductFormInDto productFormInDto) {

		System.out.println(loggedUser.getUsername() + " -- " + productFormInDto.toString());

		Optional<User> userFromDb = userRepository.findByEmail(loggedUser.getUsername());

		if (userFromDb.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Product newProduct = productFormInDto.toModel(entityManager, userFromDb.get());

		entityManager.persist(newProduct);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping(value = "/{product_id}/images")
	@Transactional
	public ResponseEntity<Object> addImageToProduct(@PathVariable Long product_id,
			@Valid @NotBlank @RequestBody String imageLink, @AuthenticationPrincipal UserDetails loggedUser) {

		System.out.println("Product ID: " + product_id + " -- " + loggedUser.getUsername() + " -- " + imageLink);

		Product product = entityManager.find(Product.class, product_id);

		if (product == null) {
			// Product does not exists
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		// Verify if product belongs to User
		if (!product.getOwner().getUsername().equals(loggedUser.getUsername())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}

		Image newImage = new Image(product.getOwner(), product, imageLink);
		entityManager.persist(newImage);

		Image imageFromDb = entityManager.find(Image.class, 1L);

		if (imageFromDb == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping(value = "/{product_id}")
	@Transactional
	public ResponseEntity<Object> detailsProduct(@PathVariable @NotNull Long product_id) {
		

		Product productFromDb = entityManager.find(Product.class, product_id);
		
		if (productFromDb == null) {
			// Product does not exists
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		ProductDetailsResponse productDetailsResponse = new ProductDetailsResponse(product_id, entityManager);
		
		return ResponseEntity.ok().body(productDetailsResponse);
		
	}

}
