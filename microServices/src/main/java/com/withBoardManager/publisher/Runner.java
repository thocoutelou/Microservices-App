package com.withBoardManager.publisher;

import java.net.InetAddress;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

	private final RabbitTemplate rabbitTemplate;
	public static String serviceForSending = "";

	public static void setServiceForSending(String service) {
		serviceForSending = service;
	}

	private final ConfigurableApplicationContext context;

	public Runner(RabbitTemplate rabbitTemplate, ConfigurableApplicationContext context) {
		this.rabbitTemplate = rabbitTemplate;
		this.context = context;
	}

	public void run(String... args) throws Exception {

		System.out.println("Sending an event...");
		InetAddress IP=InetAddress.getLocalHost();
		String nameService = (String) context.getBean("service");
		
		rabbitTemplate.convertAndSend(nameService, "Event");
		rabbitTemplate.convertAndSend(nameService, IP.getHostAddress());
		System.out.println("----END----");
		context.close();
	}

}
