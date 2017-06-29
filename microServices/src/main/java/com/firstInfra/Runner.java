package com.firstInfra;


import java.util.Scanner;

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

    public Runner(SpringReceiver receiver, RabbitTemplate rabbitTemplate,
            ConfigurableApplicationContext context) {
        this.reciever = receiver;
        this.rabbitTemplate = rabbitTemplate;
        this.context = context;
    }

   
    public void run(String... args) throws Exception {
    	
    	Scanner sc = new Scanner(System.in);
    	
    	while(true){
    	System.out.println("--> Enter the message you want to send:");
    	System.out.println("<enter QUIT to exit the application>");
    	String message = sc.nextLine();
    	
    	if (message.equals("QUIT"))
    		break;
    	
    	
    	String recieverQueue="";
    	while (recieverQueue.equals("")){
    	System.out.println("--> Enter the name of the receiving queue:");
    	recieverQueue = sc.nextLine();
    	}
    	
    	System.out.println("Sending message...");
    	rabbitTemplate.convertAndSend(recieverQueue, message);
    	
        //receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    	}
        context.close();
        sc.close();
    }

}