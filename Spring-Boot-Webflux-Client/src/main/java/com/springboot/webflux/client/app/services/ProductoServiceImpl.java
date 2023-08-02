package com.springboot.webflux.client.app.services;

import com.springboot.webflux.client.app.models.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductoServiceImpl implements ProductoService{

    @Autowired
    private WebClient client;

    @Override
    public Flux<Producto> findAll() {
        return client.get().accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(Producto.class));
    }

    @Override
    public Mono<Producto> findById(String id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);

        return client.get().uri("/{id}")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Producto.class);
                //.exchangeToFlux(clientResponse ->
                    // clientResponse.bodyToFlux(Producto.class))
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return client.post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(producto))
                .exchangeToMono(response -> response.bodyToMono(Producto.class));
    }

    @Override
    public Mono<Producto> update(Producto producto, String id) {
        return client.put()
                .uri("/{id}", Collections.singletonMap("id", id))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(producto))
                .exchangeToMono(response -> response.bodyToMono(Producto.class));
    }

    @Override
    public Mono<Void> delete(String id) {
        return client.delete()
                .uri("/{id}", Collections.singletonMap("id", id))
                .exchangeToMono(ClientResponse::releaseBody);
    }
}
