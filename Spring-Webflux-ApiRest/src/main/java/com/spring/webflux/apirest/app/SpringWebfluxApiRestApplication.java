package com.spring.webflux.apirest.app;

import com.spring.webflux.apirest.app.models.documents.Categoria;
import com.spring.webflux.apirest.app.models.documents.Producto;
import com.spring.webflux.apirest.app.models.services.ProductoService;
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
public class SpringWebfluxApiRestApplication implements CommandLineRunner {

	@Autowired
	private ProductoService service;

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	private static final Logger log = LoggerFactory.getLogger(SpringWebfluxApiRestApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxApiRestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//Reseteo automático BBDD
		mongoTemplate.dropCollection("producto").subscribe();
		mongoTemplate.dropCollection("categoria").subscribe();

		Categoria electronico = new Categoria("Electrónico");
		Categoria computacion = new Categoria("Computación");
		Categoria deporte = new Categoria("Deporte");
		Categoria muebles = new Categoria("Muebles");

		Flux.just(electronico, computacion, deporte, muebles)
				.flatMap(service::saveCategoria)
				.doOnNext(c -> {
					log.info("Categoria creada: " + c.getNombre() + ", Id: " + c.getId());
				})
				.thenMany(
						Flux.just(new Producto("Tv Panasonic", 320.29, electronico),
										new Producto("Xiaomy movil", 50.29, computacion),
										new Producto("Bicicleta de Montaña", 300.29, deporte),
										new Producto("Escritorio", 50.29, muebles),
										new Producto("Tv Panasonic", 320.29, electronico),
										new Producto("Patinete xiaomy", 120.29, electronico),
										new Producto("Teclado inalambrico", 20.29, computacion),
										new Producto("Xiaomy movil", 50.29, computacion),
										new Producto("Tv Panasonic", 320.29, electronico),
										new Producto("Patinete xiaomy", 120.29, electronico))
								.flatMap(producto -> {
									producto.setCreateAt(new Date());
									return service.save(producto);
								})
				)
				.subscribe(producto -> log.info("Insert: " + producto.getId() + " " + producto.getNombre()));

	}
}
