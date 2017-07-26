package com.withBoardManager.board;

import static com.withBoardManager.board.Log.GEN;
import static com.withBoardManager.board.Log.LOG_ON;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
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


		/* For the Receiver Mode */
		if (LOG_ON && GEN.isEnabledFor(Level.INFO)) 
			GEN.info("INIT: Ready to forward request to the BoardManager");
		//System.out.println("---- Ready for forward request to the BoardManager ----");
		while (true) {
			TimeUnit.MILLISECONDS.sleep(100);
			
			}
		
	}

}