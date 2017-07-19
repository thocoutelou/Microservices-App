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
		int value = ((HashMap<String, CounterOfService>) context.getBean("serviceToSent")).get(service)
				.getCounterLastCalled();
		if (((HashMap<String, CounterOfService>) context.getBean("serviceToSent")).get(service).popNextTicket() == -1) {
			System.out.println("No more tickets for this service");
			Event response = new Event("No more ticket for the service: " + service, -1);
			return response;
		} else {
			Event response = new Event(service, value + 1);
			WebCounterApplication.getLastCall().put(service, response);
			return (response);
		}
	}

	/* Response for recall, send again the last event*/
	@RequestMapping("/recall")
	public Event recall(@RequestParam(value = "ip") String ip, @RequestParam(value = "service") String service) {
		if (WebCounterApplication.getLastCall().get(service)==null) {
			return new Event("Service never called", -1);
		} else {
			return WebCounterApplication.getLastCall().get(service);
		}
	}

	/* Create a new ticket */
	@SuppressWarnings("unchecked")
	@RequestMapping("/ticket")
	public TcallTicket ticket(@RequestParam(value = "service") String service) {
		int ticketNumber = ((HashMap<String, CounterOfService>) context.getBean("serviceToSent")).get(service)
				.getCounterLastCreated() + 1;
		TcallTicket ticketToSend = new TcallTicket(service, ticketNumber);
		((HashMap<String, CounterOfService>) context.getBean("serviceToSent")).get(service).addTicket(ticketToSend);
		return ticketToSend;

	}
}
