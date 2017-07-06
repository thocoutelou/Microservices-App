package com.withBoardManager.receiver;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

	@SuppressWarnings("unused")
	private final RabbitTemplate rabbitTemplate;

	@SuppressWarnings("unused")
	private final SpringReceiver reciever;
	private final ConfigurableApplicationContext context;

	public Runner(SpringReceiver receiver, RabbitTemplate rabbitTemplate, ConfigurableApplicationContext context) {
		this.reciever = receiver;
		this.rabbitTemplate = rabbitTemplate;
		this.context = context;
	}

	public void run(String... args) throws Exception {

		Scanner sc = new Scanner(System.in);

		/* For the Receiver Mode */
		System.out.println("---- Ready to recieve messages from the Manager ----");
		System.out.println("To quit, enter <QUIT>");
		while (true) {
			TimeUnit.MILLISECONDS.sleep(100);
			String message = sc.nextLine();
			if (message.equals("QUIT"))
				break;
		}

		context.close();
		sc.close();

	}

}