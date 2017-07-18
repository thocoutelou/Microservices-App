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
import java.util.Random;

import org.apache.commons.cli.*;

@SpringBootApplication
public class ManagerBoardApplication {

	public static String ipServer;
	public static String httpServer;
	public static ArrayList<String> prefix = new ArrayList<>();
	public static ArrayList<String> serviceToSent = new ArrayList<>();
	public static int start = 1;
	public static int end = 10;
	public static String serviceForReceiving;
	public static String data;
	public static String pathToData;
	private static TopicExchange exchangeForSendind;
	private static AmqpAdmin amqpAdmin;
	private static ConnectionFactory connectFactory;
	
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
	

	/* Get and set methods */
	static ArrayList<String> getNamesServicesToSent() {
		return serviceToSent;
	}


	public static void setEND(int nend) {
		end = nend;
	}

	public static void setSTART(int nstart) {
		start = nstart;
	}

	public static void addServiceToSent(String serviceToSent) {
		ManagerBoardApplication.serviceToSent.add(serviceToSent);
	}

	public static void addPrefixString(String servicePrefix) {
		prefix.add(servicePrefix);
	}

	public static void setIpServer(String ipServer) {
		ManagerBoardApplication.ipServer = ipServer;
	}

	public static void sethttpServer(String url) {
		ManagerBoardApplication.httpServer = url;
	}

	public static void setServiceForReceiving(String service) {
		serviceForReceiving = service;
	}

	/* Beans creation */

	public static String getServiceForReceiving() {
		return serviceForReceiving;
	}

	public static ArrayList<String> getServiceToSent() {
		return serviceToSent;
	}
	
	/*specify the prefix of the services*/
	public static ArrayList<String> getPrefix() {
		return prefix;
	}

	/* ip or URL of the server RabbitMQ */
	public static String getIpServer() {
		return (ipServer);
	}

	/* URL of the http server */
	public static String getHttpServer() {
		return (httpServer);
	}

	/* for simulate the randomized inputs */
	
	public static int START() {
		return (start);
	}

	/* for simulate the randomized inputs */
	
	public static int END() {
		return (end);
	}

	public static String getPathToData() {
		return pathToData;
	}


	public static void setPathToData(String pathToData) {
		ManagerBoardApplication.pathToData = pathToData;
	}
	/*
	 * Bean for the RabbitMq service; for more details, see the web site
	 */

	@Bean
	/* Good way to parse argument */
	public ConnectionFactory connectionFactory() {
		connectFactory= new CachingConnectionFactory(ipServer);
		return connectFactory;
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		amqpAdmin=new RabbitAdmin(connectionFactory());
		return amqpAdmin;
	}

	@Bean
	public HttpResponse httpResponse() {
		return new HttpResponse();
	}

	@Bean
	Queue queue() {
		return new Queue("bm.rpc.requests");
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("sb-boardManager-exchange");
	}

	@Bean
	static
	TopicExchange exchangeForSending() {
		exchangeForSendind=new TopicExchange("spring-boot-exchanger");
		return exchangeForSendind ;
	}

	@Bean
	Binding binding() {
		System.out.println(serviceForReceiving);
		Binding b = BindingBuilder.bind(queue()).to(exchange()).with("CallForBoardManager");
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

	
	public static String createAndConfigureQueue(ArrayList<String>services) {
		Queue randomQueue = new Queue("receiver."+ generateString(new Random(), 
				"ABCDEFGHIJKLMOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz", 12));
		amqpAdmin.declareQueue(randomQueue);
		amqpAdmin.declareBinding(BindingBuilder.bind(randomQueue).to(exchangeForSendind).with(services.get(0)));
		for (int i=0;i<services.size();i++){
			amqpAdmin.declareBinding(BindingBuilder.bind(randomQueue).to(exchangeForSendind).with(services.get(i)));
		}
		
		return randomQueue.getName();
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
		
		Option data = new Option("d", "data", true, "Path to the Json data");
		data.setRequired(true);
		options.addOption(data);

		/*
		Option serviceForReceiving = new Option("s", "serviceForReceiving", true,
				"Name of the service for receiving message from terminalCall");
		serviceForReceiving.setRequired(true);
		options.addOption(serviceForReceiving);
		 */

		/* a different constructor to give more arguments to an option */
		@SuppressWarnings({ "deprecation", "static-access" })
		Option servicesToSent = OptionBuilder.withArgName("ServicesNames for sending").withValueSeparator(' ')
				.hasArgs(1).hasOptionalArgs().withLongOpt("servicesToSent")
				.withDescription("Name of the services which will receive the messages").create('t');
		servicesToSent.setRequired(true);
		options.addOption(servicesToSent);

		@SuppressWarnings({ "deprecation", "static-access" })
		Option prefix = OptionBuilder.withArgName("prefix for sending").withValueSeparator(' ').hasOptionalArgs()
				.withLongOpt("prefix")
				.withDescription("Prefix for the answer to a board. WARNING correrpond to the service order ")
				.create('p');
		prefix.setRequired(false);
		options.addOption(prefix);

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
		setServiceForReceiving("CallForBoardManager");

		int i = 0;
		try {
			while ((cmd.getOptionValues("servicesToSent")[i] != null)) {
				addServiceToSent(cmd.getOptionValues("servicesToSent")[i]);
				i++;
			}
		} catch (Exception e) {
			System.out.println("Managing the services: " + getNamesServicesToSent().toString());
		}

		i = 0;
		try {
			while ((cmd.getOptionValues("prefix")[i] != null)) {
				addPrefixString(cmd.getOptionValues("prefix")[i]);
				i++;
			}
		} catch (Exception e) {
			System.out.println("With the Prefix: " + getPrefix().toString());
		}

		if (cmd.getOptionValue("end") != null) {
			setEND(Integer.parseInt(cmd.getOptionValue("end")));
		}
		if (cmd.getOptionValue("start") != null) {
			setSTART(Integer.parseInt(cmd.getOptionValue("start")));
		}
		setPathToData(cmd.getOptionValue("data"));
		
		RedirectController.configureData(getPathToData());
		/* Launch the Spring-boot application */
		new SpringApplicationBuilder(ManagerBoardApplication.class).web(true).run(args);
	}

}