package com.practicas.spring.boot.webflux;

import com.practicas.spring.boot.webflux.models.Comentarios;
import com.practicas.spring.boot.webflux.models.Usuario;
import com.practicas.spring.boot.webflux.models.UsuarioComentarios;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
		
		ejemplointervaloInfinito();
	
	}
	
	public void ejemplointervaloInfinito() throws InterruptedException {
		
		
		CountDownLatch latch = new CountDownLatch(1);
		
		// Creamos un intervalo infinito que se ejecuta cada 1 segundo
		Flux.interval(Duration.ofSeconds(1))
		.doOnTerminate(() -> latch.countDown())
		// Controlamos el flujo para que finalice en el quinto intervalo con un error
		.flatMap(i -> {
			if(i >= 5) {
				return Flux.error(new InterruptedException("Solo hasta 5!"));
			}
			return Flux.just(i);
		})
		.map(i -> "Hola "+i)
		.retry(2)
		.doOnNext(string -> Log.info(string))
		.subscribe();
		
		latch.await();
	}
	
	// Copiamos el ejemplo anterior
	public void ejemploDelayElements() {
		
		Flux<Integer> rango = Flux.range(0, 12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> Log.info(i.toString()));
		
		// A modo de ejemplo se va a usar el block
		rango
		.blockLast();
		// .subscribe(); Si dejamos el subscribe no vamos a ver los números porque se emiten en segundo plano y ya finalizó el main
		
		
		
	}
	
	public void ejemplointerval() {
		
		Flux<Integer> rango = Flux.range(0, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));
		
		rango.zipWith(retraso, (ra, re) -> ra)
		.doOnNext(i -> Log.info(i.toString()))
		// .subscribe(); Si dejamos el subscribe no vamos a ver los números porque se emiten en segundo plano y ya finalizó el main
		// A modo de ejemplo se va a usar el block
		.blockLast();
		
	}

	public void ejemploZipWithRangos() {
		
		// Creamos un Flux sencillo y utilizamos un map para repasar lo aprendido.
		Flux.just(1, 2, 3, 4)
		.map(i -> i*2)
		// Usamos el método range para crear un rango de números y usamos String.format para imprimir el texto de manera secilla.
		.zipWith(Flux.range(0, 4), (uno, dos) -> String.format("Primer Flux: %d - Segundo Flux: %d", uno, dos))
		.subscribe(texto -> Log.info(texto));
	}
	
	//Copiamos el ejemplo anterior
		public void ejemploUsuarioComentariosZipWithForma2() {
			
			// Creamos los 2 observables Mono que vamos a fusionar
			Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("John", "Doe"));
			
			Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()-> {
				Comentarios comentarios = new Comentarios();
				comentarios.addComentario("Hola crack, ¿cómo andas?");
				comentarios.addComentario("Voy al gym y luego a estudiar un rato");
				comentarios.addComentario("¡Estamos en modo guerra!");
				return comentarios;
			});
			
			// Fusionamos ambos observables utilizando FlatMap - para abreviar usamos usuario(u), comentarios(c), usuarioComentarios(uc)
			Mono<UsuarioComentarios> usuarioConComentarios = usuarioMono
					.zipWith(comentariosUsuarioMono)
					.map(tuple -> {
						Usuario u = tuple.getT1();
						Comentarios c = tuple.getT2();
						return new UsuarioComentarios(u, c);
					});
			
			usuarioConComentarios.subscribe(uc -> Log.info(uc.toString()));
		}
	
	//Copiamos el ejemplo anterior
	public void ejemploUsuarioComentariosZipWith() {
		
		// Creamos los 2 observables Mono que vamos a fusionar
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("John", "Doe"));
		
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()-> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola crack, ¿cómo andas?");
			comentarios.addComentario("Voy al gym y luego a estudiar un rato");
			comentarios.addComentario("¡Estamos en modo guerra!");
			return comentarios;
		});
		
		// Fusionamos ambos observables utilizando FlatMap - para abreviar usamos usuario(u), comentarios(c), usuarioComentarios(uc)
		Mono<UsuarioComentarios> usuarioConComentarios = usuarioMono
				.zipWith(comentariosUsuarioMono, (usuario, comentariosUsuario) -> new UsuarioComentarios(usuario, comentariosUsuario));
		
		usuarioConComentarios.subscribe(uc -> Log.info(uc.toString()));
	}
	
	public void ejemploUsuarioComentariosFlatMap() {
		
		// Creamos los 2 observables Mono que vamos a fusionar
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("John", "Doe"));
		
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()-> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola crack, ¿cómo andas?");
			comentarios.addComentario("Voy al gym y luego a estudiar un rato");
			comentarios.addComentario("¡Estamos en modo guerra!");
			return comentarios;
		});
		
		// Fusionamos ambos observables utilizando FlatMap - para abreviar usamos usuario(u), comentarios(c), usuarioComentarios(uc)
		usuarioMono.flatMap(u -> comentariosUsuarioMono.map(c -> new UsuarioComentarios(u, c)))
		.subscribe(uc -> Log.info(uc.toString()));
	}
	
	// Copiamos el anterior ejercicio y lo reducimos para este ejemplo
			public void ejemploCollectList() throws Exception {
				
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
						// De este modo se muestra cada elemento de la lista como un observable Flux.
						// .subscribe(usuario -> Log.info(usuario.toString())) 
						.collectList() // Con este método transforma el observable en un Mono (emite un solo elemento, la lista completa)
						.subscribe(lista -> {
							// Podemos recorrer cada elemento de la lista con el método forEach
							lista.forEach(item -> Log.info(item.toString()));
						});
						
				
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
						if(nombre.contains("pedro".toUpperCase())) {
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