package com.luciano.springboot.ms.consumer.app.services;

import com.luciano.springboot.ms.consumer.app.models.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class ProductService implements IProductService {
    private final WebClient webClient;

    @Value("${url.api.products}")
    private String url;

    public ProductService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<List<ProductDto>> getProducts(List<String> productIds) {
        String joinedIds = String.join(",", productIds);
        String requestUrl = url + "?products=" + joinedIds;
        return webClient.get()
                .uri(requestUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductDto>>() {
                })
                .doOnError(e -> log.error("Error al consultar productos: {}", e.getMessage()))
                .onErrorResume(e -> {
                    log.error("Error en la llamada a la API: {}", e.getMessage());
                    return Mono.empty();
                });
    }
}
