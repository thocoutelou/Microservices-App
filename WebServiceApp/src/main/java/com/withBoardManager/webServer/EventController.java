package com.withBoardManager.webServer;

import java.util.HashMap;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

	private final ConfigurableApplicationContext context;

	public EventController(ConfigurableApplicationContext cont) {
		context = cont;
	}



	/* Response for Call. send a json with all the informations */
	@SuppressWarnings("unchecked")
	@RequestMapping("/event")
	public Event event(@RequestParam(value = "ip") String ip, @RequestParam(value = "service") String service) {
		int value = ((HashMap<String, CounterOfService>) context.getBean("serviceToSent")).get(service).getCounterLastCalled();
		Event response = new Event(service, value);
		((HashMap<String, CounterOfService>) context.getBean("serviceToSent")).get(service).popNextTicket();
		return (response);
	}

	
	/* Create a new ticket */
	@SuppressWarnings("unchecked")
	@RequestMapping("/ticket")
	public TcallTicket ticket(@RequestParam(value = "service")String service) {
		int ticketNumber = ((HashMap<String, CounterOfService>) context.getBean("serviceToSent"))
				.get(service).getCounterLastCreated()+1;
		TcallTicket ticketToSend = new TcallTicket(service, ticketNumber);
		((HashMap<String, CounterOfService>) context.getBean("serviceToSent")).get(service).addTicket(ticketToSend);
		return ticketToSend;
		
	}
}
