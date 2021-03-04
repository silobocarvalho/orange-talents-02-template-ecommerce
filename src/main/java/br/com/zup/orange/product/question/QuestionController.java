package br.com.zup.orange.product.question;

import java.net.URI;
import java.util.List;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zup.orange.repository.UserRepository;
import br.com.zup.orange.user.User;

@RestController
@Validated
@RequestMapping("/products")
public class QuestionController {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@PostMapping(value = "/{product_id}/question")
	@Transactional
	public ResponseEntity<Object> addQuestion(@AuthenticationPrincipal UserDetails loggedUser,
			@Valid @RequestBody QuestionRequest questionRequest, UriComponentsBuilder uriBuilder) {

		System.out.println(questionRequest.toString());
		
		Optional<User> userFromDb = userRepository.findByEmail(loggedUser.getUsername());

		if (userFromDb.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Question question = questionRequest.toModel(entityManager, userFromDb.get());
		entityManager.persist(question);

		sendEmailToSeller(question, userFromDb);
		
		List<String> questionsFromProduct = question.getProduct().getAllQuestions(entityManager);
		
		return ResponseEntity.ok().body(questionsFromProduct);
		
	}

	private void sendEmailToSeller(Question question, Optional<User> userFromDb) {

		//HERE IS THE CODE TO SEND EMAIL TO SELLER.
		System.out.println("Send email features ---- " + question.getTitle() + " --- "  + question.getCreatedAt());
		
	}

}
