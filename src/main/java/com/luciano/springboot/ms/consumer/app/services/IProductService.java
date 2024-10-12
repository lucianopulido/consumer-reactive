package com.luciano.springboot.ms.consumer.app.services;

import com.luciano.springboot.ms.consumer.app.models.ProductDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IProductService {
    Mono<List<ProductDto>> getProducts(List<String> products);
}
