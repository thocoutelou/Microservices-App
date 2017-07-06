package com.withBoardManager.webServer;

public class Event {
	
	private String service;
	private Integer count;
	
	public Event(String serviceToSent, Integer count){
		this.service=serviceToSent;
		this.count=count;
	}
	
	public String getService(){
		return this.service;
	}
	public Integer getCount(){
		return this.count;
	}

}
