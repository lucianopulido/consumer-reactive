package com.luciano.springboot.ms.consumer.app.services;

import com.luciano.springboot.ms.consumer.app.models.ClientDto;
import reactor.core.publisher.Mono;

public interface IClientService {
    Mono<ClientDto> getClient(String clientId);
}
