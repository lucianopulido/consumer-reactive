package com.luciano.springboot.ms.consumer.app.repositories;

import com.luciano.springboot.ms.consumer.app.models.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order, String> {
}
