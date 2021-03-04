package br.com.zup.orange.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.zup.orange.order.OrderEntity;
import br.com.zup.orange.user.User;

public interface OrderRepository extends JpaRepository<OrderEntity, Long>{

	Optional<OrderEntity> findByTransactionId(String transactionId);
}
