package com.step5.tcall;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

	private static RabbitTemplate rabbitTemplate;
	public static String serviceForSending = "";

	public static void setServiceForSending(String service) {
		serviceForSending = service;
	}

	private static ConfigurableApplicationContext context;

	@SuppressWarnings("static-access")
	public Runner(RabbitTemplate rabbitTemplate, ConfigurableApplicationContext context) {
		this.rabbitTemplate = rabbitTemplate;
		this.context = context;
	}

	public void run(String... args) throws Exception {

		System.out.println("-------Ready for managing the terminal calls------");
		while (true) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
	}

	/*
	 * Send a message to the BoardManager for the Terminal Call which have the ip:
	 * ipEvent
	 */
	public static void sendEvent(String ipEvent) {

		System.out.println("Sending an event...");
		// InetAddress IP=InetAddress.getLocalHost();
		String nameService = (String) context.getBean("service");
		TopicExchange exchange = (TopicExchange) context.getBean("exchange");
		System.out.println("Target: " + nameService);

		Object message = rabbitTemplate.convertSendAndReceive(exchange.getName(), nameService, ipEvent);
		String object = new String((message.toString()));
		System.out.println("Answer received...");
		System.out.println(object);

		try {
			JSONObject json = new JSONObject(object);
			System.out.println(json.toString());
			CallController.setJsonResponse(json);
		} catch (Exception e) {
			System.out.println("Not a valide JSON object.");
		}
		System.out.println("------------------------");
	}
}
