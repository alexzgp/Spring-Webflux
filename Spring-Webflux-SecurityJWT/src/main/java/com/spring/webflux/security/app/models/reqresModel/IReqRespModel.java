package com.spring.webflux.security.app.models.reqresModel;

public interface IReqRespModel<T> {

    T getData(); // Regresa los datos de la respuesta
    String getMessage(); // Regresa un mensaje(String) si hubo algun error. Ej: 404 que debe regresar "not found"
}
