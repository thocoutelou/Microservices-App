package com.withBoardManager.boardManager;

import static com.withBoardManager.boardManager.Log.GEN;
import static com.withBoardManager.boardManager.Log.COMM;
import static com.withBoardManager.boardManager.Log.LOG_ON;

import java.util.ArrayList;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

	private static RabbitTemplate rabbitTemplate;
	private static ArrayList<String> prefix;
	private static ArrayList<String> servicedManaged;

	public static ArrayList<String> getServicedManaged() {
		return servicedManaged;
	}

	public static void setServicedManaged(ArrayList<String> servicedManaged) {
		Runner.servicedManaged = servicedManaged;
	}

	public static ArrayList<String> getPrefix() {
		return prefix;
	}

	public void setPrefix(ArrayList<String> prefix) {
		Runner.prefix = prefix;
	}

	@SuppressWarnings("unused")
	private final SpringReceiver reciever;
	private final ConfigurableApplicationContext context;

	public Runner(SpringReceiver receiver, RabbitTemplate rabbitTemplate, ConfigurableApplicationContext context) {
		this.reciever = receiver;
		Runner.rabbitTemplate = rabbitTemplate;
		this.context = context;
	}

	public static String compactAnswer(int count, String service, int index) {
		String input = Integer.toString(count);
		for (int i = 0; i < 3; i++) {
			if (i >= input.length()) {
				input = "0" + input;
			}
		}
		try {
			return (getPrefix().get(index) + input);
		} catch (Exception e) {
			return (service + input);
		}
	}

	public void run(String... args) throws Exception {

		/* For the Receiver Mode */
		System.out.println("----- Ready to receive event -----");
		if (LOG_ON && GEN.isInfoEnabled()) 
			GEN.info("INIT: Ready to receive event");
		setServicedManaged(ServiceManagerApplication.getNamesServicesToSent());
		setPrefix(ServiceManagerApplication.getPrefix());
		while (true) {
			TimeUnit.MILLISECONDS.sleep(250);
			/* For sending response */
			if (context.getBean(HttpResponse.class).isNew()) {
				String response = ((context.getBean(HttpResponse.class).getResponse()));
				int count = ((context.getBean(HttpResponse.class).getCount()));
				if (getServicedManaged().contains(response)) {
					rabbitTemplate.convertAndSend("spring-boot-exchanger", response,
							compactAnswer(count, response, getServicedManaged().indexOf(response)));
					if (LOG_ON && COMM.isInfoEnabled()) 
						COMM.info("SEND: Send message to " + response);
					//System.out.println("Send message to: " + response);
				} else {
					if (LOG_ON && COMM.isInfoEnabled()) 
						COMM.info("ERROR:" +response +" >>> Unknown service...");
					//System.out.println("Received: " + response + ">>> Unknown service...");
				}
				context.getBean(HttpResponse.class).setNew(false);

			}
		}

	}

}