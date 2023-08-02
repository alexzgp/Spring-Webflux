package com.springboot.webflux.client.app.handler;

import com.springboot.webflux.client.app.models.Producto;
import com.springboot.webflux.client.app.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

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
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(producto))
                    .switchIfEmpty(ServerResponse.notFound().build());
        });
    }

    public Mono<ServerResponse> crear(ServerRequest request){
        Mono<Producto> productoMono = request.bodyToMono(Producto.class);

        return productoMono.flatMap(producto -> {
            if (producto.getCreateAt() == null){
                producto.setCreateAt(new Date());
            }
            return service.save(producto);
        }).flatMap(p -> {
            return ServerResponse.created(URI.create("/api/client/".concat(p.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(p));
        });
    }

    public Mono<ServerResponse> editar(ServerRequest request){
        Mono<Producto> productoMono = request.bodyToMono(Producto.class);
        String id = request.pathVariable("id");

        return productoMono.flatMap(p -> ServerResponse
                .created(URI.create("/api/client/".concat(id)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.update(p, id), Producto.class));
    }

    public Mono<ServerResponse> eliminar(ServerRequest request){
        String id = request.pathVariable("id");
        return service.delete(id).then(ServerResponse.noContent().build());
    }
}
