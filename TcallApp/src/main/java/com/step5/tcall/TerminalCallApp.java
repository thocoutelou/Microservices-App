package com.step5.tcall;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
public class TerminalCallApp {
	
	
	
	public static String ipServer;
	public static String service;
	
	public static void setService(String service) {
		TerminalCallApp.service = service;
	}

	public static void setIpServer(String ipServer) {
		TerminalCallApp.ipServer = ipServer;
	}
	
	@Bean
		public String service() {
		return service;
	}

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
	Queue queue() {
		return amqpAdmin().declareQueue();
	}
	
	@Bean
	TopicExchange exchange() {
		return new TopicExchange("sb-boardManager-exchange");
	}	
	
	@Bean
	Binding binding() {
		Binding b = BindingBuilder.bind(queue()).to(exchange()).with(("ReceiveForBoardManager"));
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
	MessageListenerAdapter listenerAdapter(TcallReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}


	public static void main(String[] args) {
		
		
		Options options = new Options();

		Option ipServer = new Option("i", "ipServer", true, "Ip of the RabbitMQ server");
		ipServer.setRequired(true);
		options.addOption(ipServer);
		
		/*
		Option service = new Option("s", "service", true, "name of the service for sending");
		service.setRequired(true);
		options.addOption(service);
		*/
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
		
		System.out.println("TcallApp is started...");


		/* Parse the arguments to the beans constructor */
		setIpServer(cmd.getOptionValue("ipServer"));
		setService("CallForBoardManager");
		

		/* Launch the Spring-boot application */
		new SpringApplicationBuilder(TerminalCallApp.class).run(args);
	}
}
