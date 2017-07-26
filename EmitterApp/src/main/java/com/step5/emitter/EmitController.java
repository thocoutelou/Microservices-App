package com.step5.emitter;

import static com.step5.emitter.Log.COMM;
import static com.step5.emitter.Log.LOG_ON;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmitController {

	public static JSONObject jsonResponse = null;

	public static void setJsonResponse(JSONObject json) {
		jsonResponse = json;
	}

	@RequestMapping("/")
	public String home() {
		return "Hello! \n" + "This app is used by a Kiosk to asks for a new ticket. \n"
				+ "Please use the browser of a terminal call to make your request with the route '/ticket?service=' with the service you want.";
	}

	@RequestMapping("/ticket")
	public String event(HttpServletRequest request, @RequestParam(value = "service") String service)
			throws InterruptedException {
		// Get the ip of the client
		String ipSource = request.getRemoteAddr();
    	if (LOG_ON && COMM.isEnabledFor(Level.WARN)) 
    		COMM.log(Level.INFO,"WEB: request from: " + ipSource + " for the service:" + service);
		//System.out.println("Request from: " + ipSource + " for the service:" + service);
		Runner.sendEvent("ticket?ip=" + ipSource + "&service=" + service);
		while (jsonResponse == null) {
			TimeUnit.MILLISECONDS.sleep(50);
		}
		synchronized (jsonResponse) {
			String helloResponse = "Demand accepted. Your ticket is created: ";
			helloResponse += jsonResponse.toString();
			return helloResponse;
		}
	}
}
