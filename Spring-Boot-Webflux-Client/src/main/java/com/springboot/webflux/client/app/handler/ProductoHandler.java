package com.springboot.webflux.client.app.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.webflux.client.app.models.Producto;
import com.springboot.webflux.client.app.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class ProductoHandler {

    @Autowired
    private ProductoService service;

    public Mono<ServerResponse> listar(ServerRequest request){
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Producto.class);
    }

    public Mono<ServerResponse> ver(ServerRequest request){
        String id = request.pathVariable("id");

        return service.findById(id).flatMap(producto -> {
            return errorHandler(ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(producto))
                    .switchIfEmpty(ServerResponse.notFound().build()));
        });
    }


        public Mono<ServerResponse> crear(ServerRequest request){
            Mono<Producto> producto = request.bodyToMono(Producto.class);

            return producto.flatMap(p-> {
                        if(p.getCreateAt()==null) {
                            p.setCreateAt(new Date());
                        }
                        return service.save(p);
                    }).flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(p.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(p))
                    .onErrorResume(error -> {
                        WebClientResponseException errorResponse = (WebClientResponseException) error;
                        if (errorResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            return ServerResponse.badRequest()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(errorResponse.getResponseBodyAsString());
                        }
                        return Mono.error(error);
                    });
        }

    public Mono<ServerResponse> editar(ServerRequest request){
        Mono<Producto> productoMono = request.bodyToMono(Producto.class);
        String id = request.pathVariable("id");

        return errorHandler(productoMono
                .flatMap(p -> service.update(p, id))
                .flatMap(p -> ServerResponse
                        .created(URI.create("/api/client/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p)));
    }

    public Mono<ServerResponse> eliminar(ServerRequest request){
        String id = request.pathVariable("id");
        return errorHandler(service.delete(id).then(ServerResponse.noContent().build()));
    }

    public Mono<ServerResponse> upload(ServerRequest request){
        String id = request.pathVariable("id");

        return errorHandler(request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(file -> service.upload(file, id))
                .flatMap(p -> ServerResponse.created(URI.create("/api/client".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p)));
    }

    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response){
        return response.onErrorResume(error -> {
            WebClientResponseException errorResponse = (WebClientResponseException) error;
            if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                // De esta manera con un Map podemos enviar mensaje personalizado
                Map<String, Object> body = new HashMap<>();
                body.put("error", "No se ha conseguido el producto ".concat(errorResponse.getMessage()));
                body.put("fecha", new Date());
                body.put("status", errorResponse.getStatusCode().value());
                return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(body);
            }
            return Mono.error(error);
        });
    }
}
