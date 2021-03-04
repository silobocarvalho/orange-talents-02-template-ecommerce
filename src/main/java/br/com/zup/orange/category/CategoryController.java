package br.com.zup.orange.category;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/categories")
public class CategoryController {

	@PersistenceContext
	private EntityManager entityManager;
	
	@PostMapping
	@Transactional
	public ResponseEntity<Object> addCategory(@Valid @RequestBody CategoryFormInDto categoryFormInDto) {

		Category category = categoryFormInDto.toModel();
		entityManager.persist(category);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
}
