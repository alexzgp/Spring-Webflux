package com.spring.webflux.security.app.configs.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// Convierte los valores de la cabecera en Authentication token especificamente del tipo bearer token
@Component
public class AuthConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(
                exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION)
        )
                .filter(s -> s.startsWith("Bearer ")) //Aquí identificamos dónde se ubica el token
                .map(s -> s.substring(7)) // Aquí nos saltamos el "Bearer " para que inicie en el token
                .map(s -> new BearerToken(s));
    }
}
