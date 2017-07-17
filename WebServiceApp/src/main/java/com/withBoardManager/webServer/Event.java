package com.withBoardManager.webServer;

public class Event {
	
	private String service;
	private Integer count;
	private TcallTicket ticket;
	
	public Event(String serviceToSent, Integer count,TcallTicket ticket){
		this.service=serviceToSent;
		this.count=count;
		this.ticket=ticket;
	}
	
	public String getService(){
		return this.service;
	}
	public Integer getCount(){
		return this.count;
	}
	public TcallTicket getTicket() {
		return this.ticket;
	}
}
