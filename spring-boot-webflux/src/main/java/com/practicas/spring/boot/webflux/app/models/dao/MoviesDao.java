package com.practicas.spring.boot.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.practicas.spring.boot.webflux.app.models.documents.Movies;

public interface MoviesDao extends ReactiveMongoRepository<Movies, String>{

}
