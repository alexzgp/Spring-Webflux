package com.practicassrpingbootwebflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// CommandLineRunner se utiliza para q sea una app de tipoo comando (De consola CMD)
import org.springframework.boot.CommandLineRunner;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {
	
	// Creamos atributo para desplegar en el log
	private static final Logger Log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		// Creamos el primer observable con una variable, se utiiza el método just y luego la data.
		Flux<String> nombres = Flux.just("Pedro", "Anna", "", "Jose", "Laura")
				// Con el método doOnNext le decimos qué queremos que haga con la data - aquí usamos una expresión lambda (RM = System.out::println)
				.doOnNext(elemento -> {
					//Simulamos un error si está vacio un elemento
					if(elemento.isEmpty()) {
						throw new RuntimeException("Los nombres no pueden ser vacios");
					}
					
					System.out.println(elemento);
					
					});
		
		// Es necesario subcribirse para que funcione el observable
		// Podemos hacer que desde el observable se ejecute una tarea
		// Aquí usamos referencia de método (Lambda = e -> Log.info(e))
		nombres.subscribe(Log::info,
				// Manejamos el error con el método subscribe
				error -> Log.error(error.getMessage()));
	}

}
