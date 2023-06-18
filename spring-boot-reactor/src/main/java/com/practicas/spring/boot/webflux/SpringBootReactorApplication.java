package com.practicas.spring.boot.webflux;

import com.practicas.spring.boot.webflux.models.Usuario;
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
		
		// Utilizamos el Map para transformar datos, no se modifica el original. Se crea una copia esta es la que se retorna.
		Flux<Usuario> nombres = Flux.just("Pedro", "Anna", "Maria", "Jose", "Laura")
				.map(nombre -> new Usuario(nombre.toUpperCase(), null))
				.doOnNext(usuario -> {
					//Simulamos un error si está vacio un usuario
					if(usuario == null) {
						throw new RuntimeException("Los usuarios no pueden ser vacios");
					}
					
					System.out.println(usuario.getNombre());
					
					})
					.map(usuario -> {
						String nombre = usuario.getNombre().toLowerCase();
						usuario.setNombre(nombre);
						return usuario;
					});
		
		// Es necesario subcribirse para que funcione el observable
		// Podemos hacer que desde el observable se ejecute una tarea
		// Aquí usamos referencia de método (Lambda = e -> Log.info(e))
		nombres.subscribe(e -> Log.info(e.toString()),
				// Manejamos el error con el método subscribe
				error -> Log.error(error.getMessage()),
				//Este método nos permite realizar una tarea cuando finaliza el flujo
				new Runnable() {
					
					@Override
					public void run() {
						Log.info("¡Ha finalizado la ejecución del observable con éxito!");
					}
				});
	
	}

}
