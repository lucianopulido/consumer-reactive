package com.luciano.springboot.ms.consumer.app.services;

import com.luciano.springboot.ms.consumer.app.models.ClientDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ClientService implements IClientService {

    private final WebClient webClient;

    @Value("${url.api.client}")
    private String url;

    public ClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<ClientDto> getClient(String clientId) {
        String requestUrl = this.url + "/" + clientId;
        return webClient.get()
                .uri(requestUrl)
                .retrieve()
                .bodyToMono(ClientDto.class);
    }
}
