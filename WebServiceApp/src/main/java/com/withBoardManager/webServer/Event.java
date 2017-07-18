package com.withBoardManager.webServer;

/*Response Object to a Tcall*/

public class Event {
	
	private String service;
	private Integer ticketCalled;
	//private TcallTicket ticket;
	private boolean isACall=true;
	

	public Event(String serviceToSent, Integer count){
		this.service=serviceToSent;
		this.ticketCalled=count;
		//this.ticket=ticket;
	}
	
	public String getService(){
		return this.service;
	}
	public Integer getTicketCalled(){
		return this.ticketCalled;
	}
	/*public TcallTicket getTicket() {
		return this.ticket;
	}
	*/
	public boolean isACall() {
		return isACall;
	}
}
