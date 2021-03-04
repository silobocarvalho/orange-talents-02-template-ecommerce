package br.com.zup.orange.user;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.orange.util.SecurityTools;

@RestController
@Validated
@RequestMapping("/users")
public class UserController {

	@PersistenceContext
	private EntityManager entityManager;

	@PostMapping
	@Transactional
	public ResponseEntity<Object> addBook(@Valid @RequestBody UserFormInDto userFormInDto) {

		User user = userFormInDto.toModel();
		entityManager.persist(user);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
