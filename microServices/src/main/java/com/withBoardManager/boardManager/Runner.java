package com.withBoardManager.boardManager;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
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

	@SuppressWarnings("unused")
	private final SpringReceiver reciever;
	private final ConfigurableApplicationContext context;

	public Runner(SpringReceiver receiver, RabbitTemplate rabbitTemplate, ConfigurableApplicationContext context) {
		this.reciever = receiver;
		this.rabbitTemplate = rabbitTemplate;
		this.context = context;
	}

	
	public void run(String... args) throws Exception {


		/* For the Receiver Mode */
		System.out.println("---- Ready to receive event----");
		System.out.println("To quit, enter <QUIT>");
		while (true) {
			TimeUnit.MILLISECONDS.sleep(250);
			/* For sending response */
			if(context.getBean(HttpResponse.class).isNew()){
				int number= Integer.parseInt((context.getBean(HttpResponse.class).getResponse()));
				@SuppressWarnings("unchecked")
				ArrayList<String> servicesManaged =((ArrayList<String>) context.getBean("serviceToSent"));
				int modulo= servicesManaged.size();
				number= number%modulo;
				rabbitTemplate.convertAndSend("spring-boot-exchanger",servicesManaged.get(number), "hello");
				System.out.println("Send message to: "+servicesManaged.get(number));
				context.getBean(HttpResponse.class).setNew(false);
			}
		}

	}

}