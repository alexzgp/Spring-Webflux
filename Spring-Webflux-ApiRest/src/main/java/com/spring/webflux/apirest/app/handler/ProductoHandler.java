package com.spring.webflux.apirest.app.handler;

import com.spring.webflux.apirest.app.models.documents.Producto;
import com.spring.webflux.apirest.app.models.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

@Component
public class ProductoHandler {
    @Autowired
    private ProductoService service;

    @Value("${config.uploads.path}")
    private String path;

    public Mono<ServerResponse> uploads(ServerRequest request){
        String id = request.pathVariable("id");

        return request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(file -> service.findById(id)
                        .flatMap(p -> {
                            p.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
                                    .replace(" ", "")
                                    .replace(":", "")
                                    .replace("\\", ""));
                            return file.transferTo(new File(path + p.getFoto()))
                                    .then(service.save(p));
                        })).flatMap(p -> ServerResponse
                        .created(URI.create("/api/v2/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(p)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listar(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Producto.class);
    }

    public Mono<ServerResponse> ver(ServerRequest request){
        String id = request.pathVariable("id");
        return service.findById(id).flatMap(p -> ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(p)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> crear(ServerRequest request){
        Mono<Producto> producto = request.bodyToMono(Producto.class);

        return producto.flatMap(p -> {
            if (p.getCreateAt() == null){
                p.setCreateAt(new Date());
            }
            return service.save(p);
        }).flatMap(p -> ServerResponse
                .created(URI.create("/api/v2/productos/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(p)));
    }

    public Mono<ServerResponse> editar(ServerRequest request){
        Mono<Producto> producto = request.bodyToMono(Producto.class);
        String id = request.pathVariable("id");

        Mono<Producto> productoDB = service.findById(id);

        return productoDB.zipWith(producto, (db, req) -> {
            db.setNombre(req.getNombre());
            db.setPrecio(req.getPrecio());
            db.setCategoria(req.getCategoria());
            return db;
        }).flatMap(p -> ServerResponse
                .created(URI.create("/api/v2/productos/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.save(p), Producto.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> eliminar(ServerRequest request){
        String id = request.pathVariable("id");

        Mono<Producto> productoDB = service.findById(id);

        return productoDB.flatMap(producto -> service.delete(producto)
                .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


}
