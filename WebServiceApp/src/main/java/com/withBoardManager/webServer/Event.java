package com.withBoardManager.webServer;

/*Response Object to a Tcall*/

public class Event {
	
	private String service;
	private Integer count;
	//private TcallTicket ticket;
	private boolean isACall=true;
	

	public Event(String serviceToSent, Integer count){
		this.service=serviceToSent;
		this.count=count;
		//this.ticket=ticket;
	}
	
	public String getService(){
		return this.service;
	}
	public Integer getCount(){
		return this.count;
	}
	/*public TcallTicket getTicket() {
		return this.ticket;
	}
	*/
	public boolean isACall() {
		return isACall;
	}
}
