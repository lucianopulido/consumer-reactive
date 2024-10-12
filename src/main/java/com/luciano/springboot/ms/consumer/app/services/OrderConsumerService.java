package com.luciano.springboot.ms.consumer.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luciano.springboot.ms.consumer.app.models.ClientDto;
import com.luciano.springboot.ms.consumer.app.models.Order;
import com.luciano.springboot.ms.consumer.app.models.OrderDto;
import com.luciano.springboot.ms.consumer.app.models.ProductDto;
import com.luciano.springboot.ms.consumer.app.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class OrderConsumerService {

    private final OrderRepository orderRepository;
    private final IClientService clientService;
    private final IProductService productService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final LockService lockService;
    private final int maxRetries = 3;
    private final long waitTime = 2000;

    public OrderConsumerService(OrderRepository orderRepository, IClientService clientService,
                                IProductService productService, LockService lockService) {
        this.orderRepository = orderRepository;
        this.clientService = clientService;
        this.productService = productService;
        this.lockService = lockService;
    }

    @KafkaListener(topics = "orders", groupId = "order-group")
    public void consumeOrder(ConsumerRecord<String, String> record) {
        Mono.fromCallable(() -> mapper.readValue(record.value(), OrderDto.class))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(orderDto -> {
                    if (lockService.acquireLock(orderDto.getCustomerId())) {
                        return processOrder(orderDto)
                                .doFinally(signalType -> lockService.releaseLock(orderDto.getCustomerId()));
                    } else {
                        log.error("Maxima cantidad de reintentos por orden alcanzados: {}", orderDto.getOrderId());
                        return Mono.empty();
                    }
                })
                .subscribe(result -> log.info("Orden procesada correctamente"),
                        error -> log.error("Error procesando orden", error)
                );
    }

    private Mono<Void> processOrder(OrderDto orderDTO) {
        return Mono.zip(
                        this.productService.getProducts(orderDTO.getProducts()),
                        this.clientService.getClient(orderDTO.getCustomerId())
                ).flatMap(tuple -> saveOrder(orderDTO, tuple.getT1(), tuple.getT2()))
                .onErrorResume(ex -> retryOrder(orderDTO, 0));
    }

    private Mono<Void> saveOrder(OrderDto orderDTO, List<ProductDto> products, ClientDto client) {
        Order order = new Order(orderDTO.getOrderId(), orderDTO.getCustomerId(), client.getName(), products);
        return orderRepository.save(order).then();
    }

    private Mono<Void> retryOrder(OrderDto orderDTO, int attempt) {
        if (attempt >= maxRetries) {
            log.error("Maxima cantidad de reintentos por orden alcanzados: {}", orderDTO.getOrderId());
            return Mono.empty();
        }

        String redisKey = "failed_order:" + orderDTO.getOrderId();
        return Mono.delay(Duration.ofMillis(waitTime * (long) Math.pow(2, attempt)))
                .flatMap(aLong -> {
                    log.info("Reintentando orden: {} (intento {})", orderDTO.getOrderId(), attempt + 1);
                    return processOrder(orderDTO)
                            .onErrorResume(ex -> retryOrder(orderDTO, attempt + 1));
                });
    }
}
