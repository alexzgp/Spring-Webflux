package com.springboot.webflux.app;

import com.springboot.webflux.app.models.dao.ProductoDao;
import com.springboot.webflux.app.models.documents.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

	@Autowired
	private ProductoDao dao;

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		mongoTemplate.dropCollection("productos").subscribe();

		Flux.just(new Producto("Tv Panasonic", 320.29),
		new Producto("Xiaomy movil", 50.29),
		new Producto("Tv Panasonic", 320.29),
		new Producto("Patinete xiaomy", 120.29),
		new Producto("Teclado inalambrico", 20.29),
		new Producto("Xiaomy movil", 50.29),
		new Producto("Tv Panasonic", 320.29),
		new Producto("Patinete xiaomy", 120.29))
				.flatMap(producto -> {
					producto.setCreateAt(new Date());
					return dao.save(producto);
				})
				.subscribe(producto -> log.info("Insert: " + producto.getId() + " " + producto.getNombre()));

	}
}
