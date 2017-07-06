package com.withBoardManager.webServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.apache.commons.cli.*;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SpringBootApplication
public class WebApplication {

	public static HashMap<String, Integer> serviceToSent = new HashMap<String, Integer>();
	public static int numberOfServices;

	public static HashMap<String, Integer> getServiceToSent() {
		return serviceToSent;
	}

	private static void setNumberOfServices(int nb) {
		numberOfServices = nb;
	}

	private static void addServiceToSent(String service) {
		serviceToSent.put(service, 0);
	}
	
	@Bean
	public HashMap<String,Integer>serviceToSent(){
		return serviceToSent;
	}

	/* MAIN */
	public static void main(String[] args) throws Exception {

		Options options = new Options();

		@SuppressWarnings({ "deprecation", "static-access" })
		Option servicesToSent = OptionBuilder.withArgName("ServicesNames for sending").withValueSeparator(' ')
				.hasArgs(1).hasOptionalArgs().withLongOpt("servicesToSent")
				.withDescription("Name of the services which will receive the messages").create('t');
		servicesToSent.setRequired(true);
		options.addOption(servicesToSent);

		/* to read the arguments */
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
			return;
		}

		int i = 0;
		try {
			while ((cmd.getOptionValues("servicesToSent")[i] != null)) {
				addServiceToSent(cmd.getOptionValues("servicesToSent")[i]);
				i++;
			}
		} catch (Exception e) {
			setNumberOfServices(getServiceToSent().size());
		}
		System.out.println("Starting Web Server...");
        SpringApplication.run(WebApplication.class, args);
		/**
		 * HttpServer server = HttpServer.create(new InetSocketAddress(8888),
		 * 0); System.out.println("Starting Web Server...");
		 * server.createContext("/event", new MyHandler()); server.start();
		 */
	}

	/*
	 * Find the key of the minimum value of the HashMap
	 */
	
	/**
	 * static class MyHandler implements HttpHandler {
	 * 
	 * @Override public synchronized void handle(HttpExchange t) throws
	 *           IOException {
	 * 
	 *           System.out.println("Request received..."); JSONObject json =
	 *           new JSONObject(); json.put("service",
	 *           minInHashMap(getServiceToSent()));
	 *           System.out.println(json.toString()); final byte[]
	 *           rawResponseBody =
	 *           json.toString().getBytes(StandardCharsets.UTF_8);
	 *           t.sendResponseHeaders(200, rawResponseBody.length);
	 *           t.getResponseBody().write(rawResponseBody); t.close();
	 * 
	 *           } }
	 */
	/**
	 * static class MyHandler implements HttpHandler {
	 * 
	 * @Override public void handle(HttpExchange t) {
	 * 
	 *           InputStream is; // used for reading in the request data
	 *           OutputStream os; // used for writing out the response data
	 *           String requestString = ""; JSONObject jsonRequest = new
	 *           JSONObject(); String response = "";
	 * 
	 *           StringBuilder responseBuffer = new StringBuilder(); // put the
	 *           // response text // in this // buffer to be // sent out at //
	 *           the end int httpResponseCode = 200; // This is where the HTTP
	 *           response code // to send back to the client should go
	 * 
	 *           String uri = t.getRequestURI().getPath(); String requestMethod
	 *           = t.getRequestMethod();
	 * 
	 *           // We parse the GET parameters through a Filter object that is
	 *           // registered in ServerBootstrap // It is possible to parse
	 *           POST parameters like this too, but I // don't want to
	 *           Map<String, Object> getParams = (Map<String, Object>)
	 *           t.getAttribute("parameters");
	 * 
	 *           // GET Requests won't have any data in the body if
	 *           (requestMethod.equalsIgnoreCase("POST")) {
	 * 
	 *           try { StringBuilder requestBuffer = new StringBuilder(); is =
	 *           t.getRequestBody(); int rByte; while ((rByte = is.read()) !=
	 *           -1) { requestBuffer.append((char) rByte); } is.close();
	 * 
	 *           if (requestBuffer.length() > 0) { requestString =
	 *           URLDecoder.decode(requestBuffer.toString(), "UTF-8"); } else {
	 *           requestString = null; } } catch (IOException e) {
	 *           e.printStackTrace(); }
	 * 
	 *           } /* code to respond to each type of request goes here
	 * 
	 *           requestString is a String that hold JSON (or not) data to be
	 *           parsed and acted upon uri is a String that holds the request
	 *           path (disregards http://hostname:port as well as any GET
	 *           parameters) responseBuffer is a StringBuilder that you write
	 *           your return data to in JSON format httpResponseCode is an int
	 *           that holds what response code you want to return
	 */
	/**
	 * if (requestMethod.equalsIgnoreCase("POST")) { if (uri.equals("/event")) {
	 * JSONObject json = new JSONObject(); json.put("service",
	 * minInHashMap(getServiceToSent()));
	 * responseBuffer.append(json.toString());
	 * 
	 * }
	 * 
	 * // this section sends back the return data try { response =
	 * responseBuffer.toString();
	 * 
	 * Headers h = t.getResponseHeaders(); h.add("Content-Type",
	 * "application/jsonp; charset=UTF-8"); h.add("Access-Control-Allow-Origin",
	 * "*"); h.add("Access-Control-Allow-Headers", "Origin, X-Requested-With,
	 * Content-Type, Accept"); h.add("Access-Control-Allow-Methods", "POST, GET,
	 * OPTIONS");
	 * 
	 * t.sendResponseHeaders(httpResponseCode, response.length()); os =
	 * t.getResponseBody(); os.write(response.getBytes("UTF-8")); os.flush();
	 * os.close(); t.close(); } catch (IOException e) { e.printStackTrace(); }
	 */
	// }
	// }
	// }

}