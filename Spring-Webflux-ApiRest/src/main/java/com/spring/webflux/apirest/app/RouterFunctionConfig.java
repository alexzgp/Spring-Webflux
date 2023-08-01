package com.spring.webflux.apirest.app;

import com.spring.webflux.apirest.app.handler.ProductoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

// import static org.springframework.web.reactive.function.server.RequestPredicates.*;
// import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ProductoHandler handler){

        return RouterFunctions.route(RequestPredicates.GET("/api/v2/productos")
                                    .or(RequestPredicates.GET("/api/v3/productos")),
                                    request -> handler.listar(request));
    }
}
