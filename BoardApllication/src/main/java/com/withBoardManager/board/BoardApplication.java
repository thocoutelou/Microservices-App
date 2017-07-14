package com.withBoardManager.board;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.cli.*;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BoardApplication {

	
	public static String IpManager;
	
	public static String ipServer;
	public static ArrayList<String> services = new ArrayList<>();

	/* For the random queue name */
	public static String generateString(Random rng, String characters, int length)
	{
	    char[] text = new char[length];
	    for (int i = 0; i < length; i++)
	    {
	        text[i] = characters.charAt(rng.nextInt(characters.length()));
	    }
	    return new String(text);
	}
	
	public static void addServices(String service) {
		services.add(service);
	}

	public static void setIpServer(String ipServer) {
		BoardApplication.ipServer = ipServer;
		IndexController.setIpServer(ipServer);
	}
	public static void setIpManager(String ipManager) {
		IpManager = ipManager;
		IndexController.setIpManager(ipManager);
	}	

	static ArrayList<String> getNamesServices() {
		return services;
	}
	
	
	/*TODO remove all the useless variables*/
	/*initiate beans*/
	@Bean
	public ArrayList<String> services(){
		return services;
	}
	
	@Bean
	public String ipServer() {
		return (ipServer);
	}
	
/*	
	@Bean
	/* Good way to parse argument *
	public ConnectionFactory connectionFactory() {
		return new CachingConnectionFactory(ipServer);
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}
	

	@Bean
	Queue queue() {
		Queue randomQueue = new Queue("receiver."+ generateString(new Random(), 
				"ABCDEFGHIJKLMOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz", 12));
		amqpAdmin().declareQueue(randomQueue);
		//SpringReceiver.setQueueForUI(randomQueue);
		return randomQueue;
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchanger");
	}
	
	@Bean
	Binding binding(ArrayList<String> servicesNames) {
		Binding b =BindingBuilder.bind(queue()).to(exchange()).with(servicesNames.get(0));
		for (int i=1;i<servicesNames.size();i++){
			amqpAdmin().declareBinding(BindingBuilder.bind(queue()).to(exchange()).with(servicesNames.get(i)));
		}
		return b;
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queue().getName());
		container.setMessageListener(listenerAdapter);
		return container;
	}
	*/
	
	
	
/*
	@Bean
	MessageListenerAdapter listenerAdapter(com.withBoardManager.board.IndexController receiver) {
		//SpringReceiver.setQueueForUI(queue());
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
	*/
	/********************/
	/****MAIN METHOD*****/
	/********************/
	
	public static void main(String[] args) throws InterruptedException {

		/* From the apache.commons.cli library to manage the arguments */
		Options options = new Options();

		Option ipServer = new Option("i", "ipServer", true, "Ip of the RabbitMQ server");
		ipServer.setRequired(true);
		options.addOption(ipServer);
		

		Option ipManager =new Option("m","ipManager",true,"IP of the boardManager");
		ipManager.setRequired(true);
		options.addOption(ipManager);
		
		

		/* to read the arguments */
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
			return;
		}

		/* Parse the arguments to the beans constructor */
		//////////////////////////////////////////////////

		setIpServer(cmd.getOptionValue("ipServer"));
		setIpManager(cmd.getOptionValue("ipManager"));
		

		/* Launch the Spring-boot application */
		new SpringApplicationBuilder(BoardApplication.class).run(args);
	}
}
