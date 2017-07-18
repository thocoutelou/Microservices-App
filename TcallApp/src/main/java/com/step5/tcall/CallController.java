package com.step5.tcall;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallController {

	public static JSONObject jsonResponse = null;

	public static void setJsonResponse(JSONObject json) {
		jsonResponse = json;
	}

	@RequestMapping("/")
	public String home() {
		return "Hello! \n" + "This app is used by a terminal call to send an event to the BoardManager. \n"
				+ "Please use the browser of a terminal call to make your request with the route '/event?service=' with the service you want to call";
	}

	@RequestMapping("/event")
	public String event(HttpServletRequest request, @RequestParam(value = "service") String service)
			throws InterruptedException {
		// Get the ip of the client
		String ipSource = request.getRemoteAddr();
		System.out.println("Request from: " + ipSource + " for the service:" + service);
		Runner.sendEvent("event?ip=" + ipSource + "&service=" + service);
		while (jsonResponse == null) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
		synchronized (jsonResponse) {
			String helloResponse = "Call accepted. You have called the ticket: ";
			helloResponse += jsonResponse.toString();
			return helloResponse;
		}
	}
}
