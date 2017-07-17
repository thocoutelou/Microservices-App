package com.withBoardManager.boardManager;

import java.util.ArrayList;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

	private static RabbitTemplate rabbitTemplate;
	private ArrayList<String> prefix;

	public ArrayList<String> getPrefix() {
		return prefix;
	}

	public void setPrefix(ArrayList<String> prefix) {
		this.prefix = prefix;
	}

	@SuppressWarnings("unused")
	private final SpringReceiver reciever;
	private final ConfigurableApplicationContext context;

	public Runner(SpringReceiver receiver, RabbitTemplate rabbitTemplate, ConfigurableApplicationContext context) {
		this.reciever = receiver;
		Runner.rabbitTemplate = rabbitTemplate;
		this.context = context;
	}

	public String compactAnswer(int count, String service, int index) {
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
	public static void sendToTcall(JSONObject json) {
		rabbitTemplate.convertAndSend("sb-boardManager-exchange","ReceiveForBoardManager",json.toString());
	}
	
	
	public void run(String... args) throws Exception {

		/* For the Receiver Mode */
		System.out.println("---- Ready to receive event----");
		ArrayList<String> servicesManaged = (ManagerBoardApplication.getNamesServicesToSent());
		setPrefix(ManagerBoardApplication.getPrefix());
		while (true) {
			TimeUnit.MILLISECONDS.sleep(250);
			/* For sending response */
			if (context.getBean(HttpResponse.class).isNew()) {
				String response = ((context.getBean(HttpResponse.class).getResponse()));
				int count = ((context.getBean(HttpResponse.class).getCount()));
				if (servicesManaged.contains(response)) {
					rabbitTemplate.convertAndSend("spring-boot-exchanger", response,
							compactAnswer(count, response, servicesManaged.indexOf(response)));
					System.out.println("Send message to: " + response);
				} else {
					System.out.println("Received: " + response + ">>> Unknown service...");
				}
				context.getBean(HttpResponse.class).setNew(false);

			}
		}

	}

}