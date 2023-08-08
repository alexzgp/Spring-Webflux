package com.spring.webflux.security.app.controller;

import com.spring.webflux.security.app.models.reqresBodies.ReqLogin;
import com.spring.webflux.security.app.models.reqresModel.ReqRespModelImpl;
import com.spring.webflux.security.app.services.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collection;

@RestController
public class AuthController {

    // NO hay que anotar con Autowired porque ya creamos un Bean para que en el SecurityConfig - MapReactiveUserDetailsService
    final ReactiveUserDetailsService users;
    final JWTService jwtService;

    final PasswordEncoder encoder;

    public AuthController(ReactiveUserDetailsService users, JWTService jwtService, PasswordEncoder encoder) {
        this.users = users;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    // Vamos a poder entrar a este endpoint solo si estamos autenticados
    @GetMapping("/auth")
    public Mono<ResponseEntity<ReqRespModelImpl<String>>> auth(){

        return Mono.just(
                ResponseEntity.ok(
                        new ReqRespModelImpl<>("Welcome to the private club", "")
                )
        );
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<ReqRespModelImpl<String>>> login(@RequestBody ReqLogin user){

        // Conseguimos el usuario de la lista de usuarios
        Mono<UserDetails> foundUser = users.findByUsername(user.getEmail()).defaultIfEmpty(new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return null;
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        }); //Regresa null si está vacio/not found

        return foundUser.flatMap(u -> {
                    if (u.getUsername() == null) {
                        // Va a ser null si no se ha conseguido ningun usuario así como está escrito arriba
                        return Mono.just(ResponseEntity.status(404).body(new ReqRespModelImpl<>("", "User not found, Please Register before logging in")));
                    }

                    // Verificamos si el password está correcto
                    if (encoder.matches(user.getPassword(), u.getPassword())){
                        return Mono.just(ResponseEntity.ok(
                                new ReqRespModelImpl<>(jwtService.generate(u.getUsername()), "Success")
                        ));
                    }
                    return  Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ReqRespModelImpl<>("", "Invalid Credentials")));
                }

        );

    }
}
