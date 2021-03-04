package br.com.zup.orange.product.evaluation;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.orange.repository.UserRepository;
import br.com.zup.orange.user.User;
import br.com.zup.orange.util.SecurityTools;

@RestController
@Validated
@RequestMapping("/products")
public class ProductEvalutationController {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@PostMapping(value = "/{product_id}/evaluation")
	@Transactional
	public ResponseEntity<Object> addEvaluation(@AuthenticationPrincipal UserDetails loggedUser,
			@Valid @RequestBody ProductEvaluationRequest productEvaluationRequest) {

		System.out.println(productEvaluationRequest.toString());

		Optional<User> userFromDb = userRepository.findByEmail(loggedUser.getUsername());

		/*
		// Verify username and crypted password. The generated hash is different each time, dont know why.
		String encryptedPassword = SecurityTools.encodePassword(loggedUser.getPassword());
		
		System.out.println("Logged user: " + loggedUser.getPassword());
		System.out.println("User from DB: " + userFromDb.get().getPassword());
		System.out.println(encryptedPassword);
		System.out.println(userFromDb.get().getPassword());
		*/
		
		if (userFromDb.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		/*
		 else {
			if (!userFromDb.get().getPassword().equals(encryptedPassword)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		}
		*/

		ProductEvaluation productEvaluation = productEvaluationRequest.toModel(entityManager, userFromDb.get());

		entityManager.persist(productEvaluation);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
