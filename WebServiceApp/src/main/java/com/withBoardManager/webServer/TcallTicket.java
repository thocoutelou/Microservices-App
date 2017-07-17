package com.withBoardManager.webServer;

public class TcallTicket {
	
	public String ip;
	public String service;
	public int TicketNumber;
	
	public TcallTicket(String ip, String service, int number) {
		this.ip=ip;
		this.service=service;
		TicketNumber=number;
	}

	public String getIp() {
		return ip;
	}

	public String getService() {
		return service;
	}

	public int getTicketNumber() {
		return TicketNumber;
	}
}


