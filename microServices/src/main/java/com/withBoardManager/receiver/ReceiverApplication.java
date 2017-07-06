package com.withBoardManager.receiver;

import java.util.ArrayList;

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
import com.withBoardManager.boardManager.SpringReceiver;

@SpringBootApplication
public class ReceiverApplication {

	
	
	public static String ipServer;
	public static ArrayList<String> services = new ArrayList<>();

	
	public static void addServices(String service) {
		services.add(service);
	}

	public static void setIpServer(String ipServer) {
		ReceiverApplication.ipServer = ipServer;
	}
	static ArrayList<String> getNamesServices() {
		return services;
	}
	
	/*initiate beans*/
	@Bean
	public ArrayList<String> services(){
		return services;
	}
	
	@Bean
	public String ipServer() {
		return (ipServer);
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
	
	

	@Bean
	MessageListenerAdapter listenerAdapter(com.withBoardManager.receiver.SpringReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
	
	/********************/
	/****MAIN METHOD*****/
	/********************/
	
	public static void main(String[] args) throws InterruptedException {

		/* From the apache.commons.cli library to manage the arguments */
		Options options = new Options();

		Option ipServer = new Option("i", "ipServer", true, "Ip of the RabbitMQ server");
		ipServer.setRequired(true);
		options.addOption(ipServer);
		

		/* a different constructor to give more arguments to an option */
		@SuppressWarnings({ "deprecation", "static-access" })
		Option services = OptionBuilder.withArgName("ServicesNames for sending").withValueSeparator(' ').hasArgs(1).hasOptionalArgs()
				.withLongOpt("servicesToSent").withDescription("Name of the services which will receive the messages")
				.create('s');
		services.setRequired(true);
		options.addOption(services);

		
		

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
		int i=0;
		try {
			while ((cmd.getOptionValues("servicesToSent")[i] != null)) {
				addServices(cmd.getOptionValues("servicesToSent")[i]);
				i++;
			}
		} catch (Exception e) {
			System.out.println(getNamesServices().toString());

		}
		

		/* Launch the Spring-boot application */
		new SpringApplicationBuilder(ReceiverApplication.class).web(false).run(args);
	}
}
