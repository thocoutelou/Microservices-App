package com.withBoardManager.boardManager;

import java.util.ArrayList;

import java.util.concurrent.TimeUnit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

	private final RabbitTemplate rabbitTemplate;
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
		this.rabbitTemplate = rabbitTemplate;
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

	@SuppressWarnings("unchecked")
	public void run(String... args) throws Exception {

		/* For the Receiver Mode */
		System.out.println("---- Ready to receive event----");
		ArrayList<String> servicesManaged = ((ArrayList<String>) context.getBean("serviceToSent"));
		setPrefix((ArrayList<String>) context.getBean("prefix"));
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