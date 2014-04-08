package com.esteniek.treasurely_android;

public class Treasure {

	public String title;
	public String media;
	public String text;
	
	public Treasure() {
		super();
	}
	
	public Treasure(String title, String text) { 
		super();
		this.title = title;
		this.text = text;		
	}
	
	public Treasure(String title, String text, String media) {
		super();
		this.title = title;
		this.media = media;
		this.text = text;
	}

}
