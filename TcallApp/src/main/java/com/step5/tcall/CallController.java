package com.step5.tcall;

import static com.step5.tcall.Log.COMM;
import static com.step5.tcall.Log.GEN;
import static com.step5.tcall.Log.LOG_ON;

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
		return "Hello! \n" + "This app is used by a terminal call to send an event to the Service Manager. \n"
				+ "Please use the browser of a terminal call to make your request with the route '/event?service=' with the service you want to call";
	}

	/* for consuming a ticket of a service */
	@RequestMapping("/event")
	public String event(HttpServletRequest request, @RequestParam(value = "service") String service)
			throws InterruptedException {
		// Get the ip of the client
		String ipSource = request.getRemoteAddr();
    	if (LOG_ON && COMM.isInfoEnabled()) 
    		COMM.info("WEB: request from "+ipSource+ " for the service "+service);
		//System.out.println("Request from: " + ipSource + " for the service:" + service);
		Runner.sendEvent("event?ip=" + ipSource + "&service=" + service);
		while (jsonResponse == null) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
		synchronized (jsonResponse) {
			String helloResponse = "";
			if (jsonResponse.has("error")) {
				helloResponse = "Call failed :";
			} else {
				helloResponse = "Call accepted. You have called the ticket: ";
			}
			helloResponse += jsonResponse.toString();
			return helloResponse;
		}
	}

	/* reload the configuration */
	@RequestMapping("/configuration")
	public String configuration(HttpServletRequest request) {
		String ipSource = request.getRemoteAddr();
    	if (LOG_ON && GEN.isInfoEnabled()) 
    		GEN.info("WEB: Reconfigure data asking by " + ipSource);
		//System.out.println("Reconfigure data asking by " + ipSource);
		Runner.sendEvent("configuration");
		synchronized (jsonResponse) {
			String helloResponse = "";
			if (jsonResponse.has("error")) {
				helloResponse = "Configuration failed :";
			} else {
				helloResponse = "New configuration accepted.";
			}
			//helloResponse += jsonResponse.toString();
			return helloResponse;
		}
	}
	
	
	/* for recalling the last ticket of a service*/
	@RequestMapping("/recall")
	public String recall(HttpServletRequest request, @RequestParam(value = "service") String service)
			throws InterruptedException {
		// Get the ip of the client
		String ipSource = request.getRemoteAddr();
    	if (LOG_ON && COMM.isInfoEnabled()) 
    		COMM.info("WEB: Recall from: " + ipSource + " for the service:" + service);
		//System.out.println("Recall from: " + ipSource + " for the service:" + service);
		Runner.sendEvent("recall?ip=" + ipSource + "&service=" + service);
		while (jsonResponse == null) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
		synchronized (jsonResponse) {
			String helloResponse = "";
			if (jsonResponse.has("error")) {
				helloResponse = "Call failed :";
			} else {
				helloResponse = "Recall accepted. You have recalled the ticket: ";
			}
			helloResponse += jsonResponse.toString();
			return helloResponse;
		}
	}
}
