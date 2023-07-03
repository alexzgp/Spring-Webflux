package com.practicas.spring.boot.webflux.app;

import com.practicas.spring.boot.webflux.app.models.dao.ProductoDao;
import com.practicas.spring.boot.webflux.app.models.documents.Producto;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import reactor.core.publisher.Flux;

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
		
		// Utilizamos dropCollection ÚNICAMENTE en desarrollo para que no se acumulen datos al hacer las pruebas
		mongoTemplate.dropCollection("productos").subscribe();
		
		Flux.just(new Producto("TV de plastma", 1000),
				new Producto("Xiaomy", 2000),
				new Producto("PSP", 1500)
				)
		// Usamos Flatmap y no map porque el dao.save regresa un mono y con el flatmap e convierte en un producto para trabajar con él.
		.flatMap(producto -> {
			
			// Agregamos la fecha de creación del producto
			producto.setCreateAt(new Date());
			return dao.save(producto);
			})
			.subscribe(producto -> log.info("Insert: " + producto.getNombre() + producto.getPrecio()));
		
	}

}
