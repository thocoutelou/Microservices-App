package com.withBoardManager.webServer;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/*
 * Object used in the Counting Service to know which is the ticket which waiting for the service
 * */

public class CounterOfService {

	private Integer counterLastCalled;
	private Integer counterLastCreated;
	private LinkedList<Integer> ticketQueue;

	public CounterOfService() {
		this.counterLastCalled = 0;
		this.counterLastCreated = 0;
		this.ticketQueue = new LinkedList<>();
	}

	/* return the number of the last ticket called a tcall */
	public Integer getCounterLastCalled() {
		return counterLastCalled;
	}

	/* return the number ofthe last ticket added of the queue */
	public Integer getCounterLastCreated() {
		return counterLastCreated;
	}

	/*
	 * add a ticket to the ticket queue and return his position in the queue. If the
	 * ticket is already in the queue, do nothing
	 */
	public void addTicket(TcallTicket ticket) {
		if (!ticketQueue.contains(ticket.getTicketNumber())) {
			counterLastCreated++;
			ticketQueue.add(ticket.getTicketNumber());
	
		}
	}

	/*
	 * remove the next ticket to the queue and return his number Return -1 if the
	 * queue is empty
	 */
	public int popNextTicket() {
		counterLastCalled++;
		try {
			return ticketQueue.pop();
		} catch (NoSuchElementException e) {
			return -1;
		}
	}

}
