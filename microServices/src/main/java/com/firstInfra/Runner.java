package com.firstInfra;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

//import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {


	private final RabbitTemplate rabbitTemplate;
	private final SpringReceiver reciever;
	private final ConfigurableApplicationContext context;

	public Runner(SpringReceiver receiver, RabbitTemplate rabbitTemplate, ConfigurableApplicationContext context) {
		this.reciever = receiver;
		this.rabbitTemplate = rabbitTemplate;
		this.context = context;
	}

	public void run(String... args) throws Exception {

		Scanner sc = new Scanner(System.in);

		if (context.getBean("isReciever").equals(true)) {
			System.out.println("---- Ready to recieve messages ----");
			System.out.println("To quit, enter <QUIT>");
			while (true) {
				TimeUnit.MILLISECONDS.sleep(100);
				String message = sc.nextLine();
				if (message.equals("QUIT"))
					break;
			}

		} else {
			/* if a path to a file is given */
			
			if (!context.getBean("pathToFile").equals("")) {
				System.out.println("Sending a file");
				String pathToFile = (String) context.getBean("pathToFile");
				Path path = Paths.get(pathToFile);
				String recieverQueue = (String) context.getBean("queueToSent");
				Files.lines(path, StandardCharsets.UTF_8)
						.forEachOrdered(l -> rabbitTemplate.convertAndSend(recieverQueue, l));
			} else {
				while (true) {
					System.out.println("--> Enter the message you want to send:");
					System.out.println("<enter QUIT to exit the application>");
					String message = sc.nextLine();

					if (message.equals("QUIT"))
						break;

					String recieverQueue = "";
					while (recieverQueue.equals("")) {
						System.out.println("--> Enter the name of the receiving queue:");
						recieverQueue = sc.nextLine();
					}

					System.out.println("Sending message...");
					rabbitTemplate.convertAndSend(recieverQueue, message);

				}
			}
		}
		context.close();
		sc.close();

	}

}