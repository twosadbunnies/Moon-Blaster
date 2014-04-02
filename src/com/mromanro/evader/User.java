package com.mromanro.evader;

public class User {

	private String name;
	private int score;
	private int id;
	
	public User(){
		this.name = "DEFAULT";
		this.score = 0;
		this.id = 0;
	}
	
	public User(String name, int score){
		this.name = name;
		this.score = score;
		this.id = name.hashCode();
	}
	
	public User(int id, String name, int score){
		this.id = id;
		this.name = name;
		this.score = score;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getScore(){
		return this.score;
	}
	
	public int getID(){
		return this.id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	
}
