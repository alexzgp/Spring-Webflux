package com.practicas.spring.boot.webflux;

import com.practicas.spring.boot.webflux.models.Usuario;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// CommandLineRunner se utiliza para q sea una app de tipoo comando (De consola CMD)
import org.springframework.boot.CommandLineRunner;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {
	
	// Creamos atributo para desplegar en el log
	private static final Logger Log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		ejemploToString();
	
	}
	
	// Copiamos el anterior ejercicio y lo reducimos para este ejemplo
		public void ejemploToString() throws Exception {
			
			List<Usuario> listaDeUsuarios = new ArrayList<>();
			listaDeUsuarios.add(new Usuario("Pedro", "Guzman"));
			listaDeUsuarios.add(new Usuario("Anna", "Garcia"));
			listaDeUsuarios.add(new Usuario("Maria", "Delgado"));
			listaDeUsuarios.add(new Usuario("Jose", "Iniesta"));
			listaDeUsuarios.add(new Usuario("Pedro", "Soprano"));
			listaDeUsuarios.add(new Usuario("Tony", "Stark"));
			listaDeUsuarios.add(new Usuario("Bruce", "Wayne"));
			
			// De un flujo observable utilizamos FlatMap para mapear transformarlo en otro tipo de flujo.
			Flux.fromIterable(listaDeUsuarios)
					.map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
					.flatMap(nombre -> {
						if(nombre.split(" ")[0].equalsIgnoreCase("pedro")) {
							return Mono.just(nombre);
						} else {
							return Mono.empty();
						}
					})
						.map(nombre -> {
							return nombre.toLowerCase();
						})
						.subscribe(u -> Log.info(u.toString()));
			
			}
	
	// Copiamos el anterior ejercicio y lo reducimos para este ejemplo
	public void ejemploFlatMap() throws Exception {
		
		List<String> listaDeUsuarios = new ArrayList<>();
		listaDeUsuarios.add("Pedro Guzman");
		listaDeUsuarios.add("Anna Garcia");
		listaDeUsuarios.add("Maria Delgado");
		listaDeUsuarios.add("Jose Iniesta");
		listaDeUsuarios.add("Pedro Soprano");
		listaDeUsuarios.add("Tony Stark");
		listaDeUsuarios.add("Bruce Wayne");
		
		// De un flujo observable utilizamos FlatMap para mapear transformarlo en otro tipo de flujo.
		Flux.fromIterable(listaDeUsuarios)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if(usuario.getNombre().equalsIgnoreCase("pedro")) {
						return Mono.just(usuario);
					} else {
						return Mono.empty();
					}
				})
					.map(usuario -> {
						String nombre = usuario.getNombre().toLowerCase();
						usuario.setNombre(nombre);
						return usuario;
					})
					.subscribe(u -> Log.info(u.toString()));
		}

	public void ejemploIterable() throws Exception {
		
		List<String> listaDeUsuarios = new ArrayList<>();
		listaDeUsuarios.add("Pedro Guzman");
		listaDeUsuarios.add("Anna Garcia");
		listaDeUsuarios.add("Maria Delgado");
		listaDeUsuarios.add("Jose Iniesta");
		listaDeUsuarios.add("Pedro Soprano");
		listaDeUsuarios.add("Tony Stark");
		listaDeUsuarios.add("Bruce Wayne");
		
		// Creamos un flux desde un objeto iterable
		Flux<String> nombres = Flux.fromIterable(listaDeUsuarios);
				
		
		Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> usuario.getNombre().toLowerCase().equals("pedro"))
				.doOnNext(usuario -> {
					//Simulamos un error si está vacio un usuario
					if(usuario == null) {
						throw new RuntimeException("Los usuarios no pueden ser vacios");
					}
					
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
					
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