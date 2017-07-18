package com.withBoardManager.webServer;

public class TcallTicket {
	
	//public String ip;
	public String service;
	public int ticketNumber;
	
	public TcallTicket(String service, int number) {
		//this.ip=ip;
		this.service=service;
		ticketNumber=number;
	}
/*
	public String getIp() {
		return ip;
	}
*/
	public String getService() {
		return service;
	}

	public int getTicketNumber() {
		return ticketNumber;
	}
}


