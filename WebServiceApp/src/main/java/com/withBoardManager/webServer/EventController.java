package com.withBoardManager.webServer;

import static com.withBoardManager.webServer.Log.COMM;
import static com.withBoardManager.webServer.Log.LOG_ON;

import java.util.HashMap;

import org.apache.log4j.Level;
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
			if (LOG_ON && COMM.isEnabledFor(Level.INFO))
				COMM.info("COUNTER: No more tickets for the service "+service);
			//System.out.println("No more tickets for the service "+service);
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
		try{
			return WebCounterApplication.getLastCall().get(service);
		} catch (NullPointerException e) {
			return new Event("Service never called", -1);

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
