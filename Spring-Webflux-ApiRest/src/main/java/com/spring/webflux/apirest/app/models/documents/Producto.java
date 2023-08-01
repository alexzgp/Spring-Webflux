package com.spring.webflux.apirest.app.models.documents;

import org.springframework.data.annotation.Id;
import org.springframework.validation.annotation.Validated;


public class Producto {

    @Id
    private String id;

    @NotEmpty
    private String nombre;


}
