package com.withBoardManager.boardManager;

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

import java.util.ArrayList;

import org.apache.commons.cli.*;

@SpringBootApplication
public class ManagerBoardApplication {

	public static String ipServer;
	public static String httpServer;
	// public static String filePath = "";
	public static ArrayList<String> serviceToSent = new ArrayList<>();
	public static int START = 1;
	public static int END = 10;
	public static String serviceForReceiving;

	/* Get and set methods */
	static ArrayList<String> getNamesServicesToSent() {
		return serviceToSent;
	}

	public static void setEND(int eND) {
		END = eND;
	}

	public static void setSTART(int sTART) {
		START = sTART;
	}

	public static void addServiceToSent(String serviceToSent) {
		ManagerBoardApplication.serviceToSent.add(serviceToSent);
	}

	public static void setIpServer(String ipServer) {
		ManagerBoardApplication.ipServer = ipServer;
	}
	
	public static void sethttpServer (String url) {
		ManagerBoardApplication.httpServer = url;
	}


	public static void setServiceForReceiving(String service) {
		serviceForReceiving = service;
	}

	/* Beans creation */

	@Bean
	public static String serviceForReceiving() {
		return serviceForReceiving;
	}
	
	@Bean
	public ArrayList<String> serviceToSent(){
		return serviceToSent;
	}

	/* ip or URL of the server RabbitMQ */
	@Bean
	public String ipServer() {
		return (ipServer);
	}
	/* URL of the http server */
	@Bean
	public String httpServer() {
		return (httpServer);
	}
	

	/* for simulate the randomized inputs */
	@Bean
	public int START() {
		return (START);
	}

	/* for simulate the randomized inputs */
	@Bean
	public int END() {
		return (END);
	}

	/*
	 * Bean for the RabbitMq service for more details, see the web site
	 */

	@Bean
	/* Good way to parse argument */
	public ConnectionFactory connectionFactory() {
		return new CachingConnectionFactory(ipServer);
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}
	
    
	@Bean
	public HttpResponse httpResponse(){
		return new HttpResponse();
	}


	@Bean
	Queue queue() {
		return amqpAdmin().declareQueue();
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("sb-boardManager-exchange");
	}

	@Bean
	TopicExchange exchangeForSending() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	Binding binding() {
		System.out.println(serviceForReceiving);
		Binding b = BindingBuilder.bind(queue()).to(exchange()).with(serviceForReceiving());
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
	
	

	@Bean
	MessageListenerAdapter listenerAdapter(SpringReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	/////////////////
	/* MAIN METHOD */
	/////////////////

	public static void main(String[] args) throws InterruptedException {

		/* From the apache.commons.cli library to manage the arguments */
		Options options = new Options();

		Option ipServer = new Option("i", "ipServer", true, "Ip of the RabbitMQ server");
		ipServer.setRequired(true);
		options.addOption(ipServer);
		
		Option httpServer = new Option("h", "httpServer", true, "Ip or url of the http server");
		httpServer.setRequired(true);
		options.addOption(httpServer);


		Option serviceForReceiving = new Option("s", "serviceForReceiving", true,
				"Name of the service for receiving message from terminalCall");
		serviceForReceiving.setRequired(true);
		options.addOption(serviceForReceiving);

		/* a different constructor to give more arguments to an option */
		@SuppressWarnings({ "deprecation", "static-access" })
		Option servicesToSent = OptionBuilder.withArgName("ServicesNames for sending").withValueSeparator(' ').hasArgs(1).hasOptionalArgs()
				.withLongOpt("servicesToSent").withDescription("Name of the services which will receive the messages")
				.create('t');
		servicesToSent.setRequired(false);
		options.addOption(servicesToSent);

		Option start = new Option("st", "start", true,
				"for the waiting time, between 'start' and 'end' seconds (default: 1-10)");
		start.setRequired(false);
		options.addOption(start);

		Option end = new Option("e", "end", true,
				"for the waiting time, between 'start' and 'end' seconds (default: 1-10)");
		end.setRequired(false);
		options.addOption(end);
		
		

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
		sethttpServer(cmd.getOptionValue("httpServer"));
		setServiceForReceiving(cmd.getOptionValue("serviceForReceiving"));

		
		int i=0;
		try {
			while ((cmd.getOptionValues("servicesToSent")[i] != null)) {
				addServiceToSent(cmd.getOptionValues("servicesToSent")[i]);
				i++;
			}
		} catch (Exception e) {
			System.out.println("Managing the services: "+getNamesServicesToSent().toString());
		}
		
		if (cmd.getOptionValue("end") != null) {
			setEND(Integer.parseInt(cmd.getOptionValue("end")));
		}
		if (cmd.getOptionValue("start") != null) {
			setSTART(Integer.parseInt(cmd.getOptionValue("start")));
		}

		/* Launch the Spring-boot application */
		new SpringApplicationBuilder(ManagerBoardApplication.class).web(false).run(args);
	}

}