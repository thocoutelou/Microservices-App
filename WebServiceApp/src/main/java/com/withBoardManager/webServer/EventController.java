package com.withBoardManager.webServer;

import java.util.HashMap;
import java.util.Iterator;

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

	@SuppressWarnings("rawtypes")
	public static String minInHashMap(HashMap<String, Integer> map) {
		Iterator it = map.keySet().iterator();
		/* for the first value */
		if (it.hasNext()) {
			String keyOfTheMin = (String) it.next();
			int min = map.get(keyOfTheMin);
			while (it.hasNext()) {
				String key = (String) it.next();
				int val = map.get(key);
				if (val <= min) {
					keyOfTheMin = key;
					min = val;
				}
			}
			return keyOfTheMin;
		} else {
			return ("No service");
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/event")
	public Event event(@RequestParam(value="ip")String ip,@RequestParam(value="service")String service) {
		HashMap<String, Integer> map = ((HashMap<String, Integer>) context.getBean("serviceToSent"));
		String key = minInHashMap(map);
		int value = map.get(key);
		TcallTicket ticket= new TcallTicket(service, value+1);
		Event response = new Event(key,value,ticket);
		((HashMap<String, Integer>) context.getBean("serviceToSent")).put(key, value + 1);
		return (response);
	}
}
