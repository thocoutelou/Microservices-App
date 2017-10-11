package com.withBoardManager.board;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

	@SuppressWarnings("unused")
	private final RabbitTemplate rabbitTemplate;

	@SuppressWarnings("unused")
	private final IndexController iController;

	public Runner(IndexController controller, RabbitTemplate rabbitTemplate) {
		this.iController = controller;
		this.rabbitTemplate = rabbitTemplate;
	}

	public void run(String... args) throws Exception {

		while(true) {
		/* For the Receiver Mode */
		System.out.println("---- Ready to recieve messages from the Manager ----");
		while (true) {
			TimeUnit.MILLISECONDS.sleep(100);
			
			}
		}
	}

}