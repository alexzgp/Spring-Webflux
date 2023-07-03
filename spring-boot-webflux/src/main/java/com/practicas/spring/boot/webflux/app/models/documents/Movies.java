package com.practicas.spring.boot.webflux.app.models.documents;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "movies")
public class Movies {
	

	@Id
	private String id;
	
	private String title;
	
	private String rated;
	
	private int year;
	
	private int lastupdated;
	
	private Date released;
	
	private ArrayList<String> gender;
	
	private ArrayList<String> languages;
	
	private ArrayList<String> cast;
	
	private ArrayList<String> directors;
	
	private ArrayList<Object> awards;
	
	public Movies() {}

	public Movies(String title, String rated, int year, int lastupdated, Date released, ArrayList<String> gender,
			ArrayList<String> languages, ArrayList<String> cast, ArrayList<String> directors,
			ArrayList<Object> awards) {
		this.title = title;
		this.rated = rated;
		this.year = year;
		this.lastupdated = lastupdated;
		this.released = released;
		this.gender = gender;
		this.languages = languages;
		this.cast = cast;
		this.directors = directors;
		this.awards = awards;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRated() {
		return rated;
	}

	public void setRated(String rated) {
		this.rated = rated;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getLastupdated() {
		return lastupdated;
	}

	public void setLastupdated(int lastupdated) {
		this.lastupdated = lastupdated;
	}

	public Date getReleased() {
		return released;
	}

	public void setReleased(Date released) {
		this.released = released;
	}

	public ArrayList<String> getGender() {
		return gender;
	}

	public void setGender(ArrayList<String> gender) {
		this.gender = gender;
	}

	public ArrayList<String> getLanguages() {
		return languages;
	}

	public void setLanguages(ArrayList<String> languages) {
		this.languages = languages;
	}

	public ArrayList<String> getCast() {
		return cast;
	}

	public void setCast(ArrayList<String> cast) {
		this.cast = cast;
	}

	public ArrayList<String> getDirectors() {
		return directors;
	}

	public void setDirectors(ArrayList<String> directors) {
		this.directors = directors;
	}

	public ArrayList<Object> getAwards() {
		return awards;
	}

	public void setAwards(ArrayList<Object> awards) {
		this.awards = awards;
	}


}
