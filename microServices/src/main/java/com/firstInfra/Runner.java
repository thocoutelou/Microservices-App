package com.firstInfra;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
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
	/*
	 * To generate a random int between two values.
	 * 
	 */
	private int RandomInteger(int aStart, int aEnd, Random aRandom) {
		if (aStart > aEnd) {
			throw new IllegalArgumentException("Start cannot exceed End.");
		}
		// get the range, casting to long to avoid overflow problems
		long range = (long) aEnd - (long) aStart + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long) (range * aRandom.nextDouble());
		int randomNumber = (int) (fraction + aStart);
		return randomNumber;
	}

	public void run(String... args) throws Exception {

		Scanner sc = new Scanner(System.in);

		/* For the Receiver Mode */
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
			/* For the Publisher Mode */
			/* if a path to a file is given */

			if (!context.getBean("pathToFile").equals("")) {
				/* Infinite loop with random wait to simulate activity */
				int start = (int) context.getBean("START");
				int end = (int) context.getBean("END");
				Random random = new Random();
				while (true) {
					// System.out.println("Sending a file");
					String pathToFile = (String) context.getBean("pathToFile");
					Path path = Paths.get(pathToFile);

					@SuppressWarnings("unchecked")
					ArrayList<String> serviceToSent = (ArrayList<String>) context.getBean("serviceToSent");

					for (int i = 0; i < serviceToSent.size(); i++) {
						setServiceForSending(serviceToSent.get(i));
						System.out.println("Sending a file for the service: " + serviceToSent.get(i));
						/* Send line by line */
						Files.lines(path, StandardCharsets.UTF_8).forEachOrdered(
								l -> rabbitTemplate.convertAndSend("spring-boot-exchanger", serviceForSending, l));
					}
					/* for simulate the input */
					TimeUnit.SECONDS.sleep(RandomInteger(start, end, random));
				}

			} else {
				/* Not used anymore*/
				while (true) {
					System.out.println("--> Enter the message you want to send:");
					System.out.println("<enter QUIT to exit the application>");
					String message = sc.nextLine();

					if (message.equals("QUIT"))
						break;

					String serviceToSent = "";
					while (serviceToSent.equals("")) {
						System.out.println("--> Enter the name of the service you want to send the message:");
						serviceToSent = sc.nextLine();
					}

					System.out.println("Sending message...");
					rabbitTemplate.convertAndSend(serviceToSent, message);

				}
			}
		}
		context.close();
		sc.close();

	}

}