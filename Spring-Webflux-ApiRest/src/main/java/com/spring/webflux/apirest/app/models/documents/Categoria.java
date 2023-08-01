package com.spring.webflux.apirest.app.models.documents;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;

public class Categoria {

    @Id
    @NotEmpty
    private String id;

    @NotEmpty
    private String nombre;

    public Categoria() {}

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}