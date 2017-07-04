package com.firstInfra;

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
import org.springframework.context.annotation.Conditional;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.commons.cli.*;

@SpringBootApplication
public class TestingApplication {

	public static String ipServer;
	public static boolean isReciever = false;
	public static String filePath = "";
	public static String queueToSent = "";
	public static ArrayList<String> serviceToSent = new ArrayList<>();
	public static int START = 1;
	public static int END = 10;
	public static ArrayList<String> namesServices = new ArrayList<>();

	/* Get and set methods */
	public static void addService(String serviceName) {
		namesServices.add(serviceName);
	}

	public static ArrayList<String> getNamesServices() {
		return namesServices;
	}

	public static void setEND(int eND) {
		END = eND;
	}

	public static void setSTART(int sTART) {
		START = sTART;
	}

	public static void addServiceToSent(String serviceToSent) {
		TestingApplication.serviceToSent.add(serviceToSent);
	}

	public static String getFilePath() {
		return filePath;
	}

	public static void setFilePath(String filePath) {
		TestingApplication.filePath = filePath;
	}

	public static void setReciever(boolean isReciever) {
		TestingApplication.isReciever = isReciever;
	}

	public static void setIpServer(String ipServer) {
		TestingApplication.ipServer = ipServer;
	}

	/* Beans creation */

	@Bean
	@Conditional(RecieverCondition.class)
	public static ArrayList<String> namesServices() {
		return namesServices;
	}

	/* boolean to see if it is in Receiver mode or Publisher mode */
	@Bean
	public static boolean isReciever() {
		return isReciever;
	}

	/* path to the file to send */
	@Bean
	public static String pathToFile() {
		return filePath;
	}

	/* ip or URL of the server RabbitMQ */
	@Bean
	public String ipServer() {
		return (ipServer);
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
	@Conditional(RecieverCondition.class)
	//TODO find the opposite of conditional
	public ArrayList<String> serviceToSent() {
		return serviceToSent;
	}

	/* Condition: if it is in receiver mode */
	@Bean
	@Conditional(RecieverCondition.class)
	Queue queue() {
		return amqpAdmin().declareQueue();
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchanger");
	}	

	@Bean
	@Conditional(RecieverCondition.class)
	Binding binding(ArrayList<String> servicesNames) {
		Binding b =BindingBuilder.bind(queue()).to(exchange()).with(servicesNames.get(0));
		for (int i=1;i<servicesNames.size();i++){
			amqpAdmin().declareBinding(BindingBuilder.bind(queue()).to(exchange()).with(servicesNames.get(i)));
		}
		return b;
	}

	@Bean
	@Conditional(RecieverCondition.class)
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queue().getName());
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	@Conditional(RecieverCondition.class)
	MessageListenerAdapter listenerAdapter(SpringReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	/////////////////
	/* MAIN METHOD */
	/////////////////

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		/* From the apache.commons.cli library to manage the arguments */
		Options options = new Options();

		Option ipServer = new Option("i", "ipServer", true, "Ip of the RabbitMQ server");
		ipServer.setRequired(true);
		options.addOption(ipServer);

		Option recieverState = new Option("r", "recieverState", true, "Configure the client to be a receiver");
		recieverState.setRequired(false);
		options.addOption(recieverState);

		Option filePath = new Option("f", "filePath", true, "Path of the file to read for sending");
		filePath.setRequired(false);
		options.addOption(filePath);

		Option servicesToSent = new Option("t", "servicesToSent", true,
				"The name of the services which messages will be sent");
		servicesToSent.setOptionalArg(true);;
		servicesToSent.setRequired(false);
		options.addOption(servicesToSent);

		Option start = new Option("s", "start", true,
				"for the waiting time, between 'start' and 'end' seconds (default: 1-10)");
		start.setRequired(false);
		options.addOption(start);

		Option end = new Option("e", "end", true,
				"for the waiting time, between 'start' and 'end' seconds (default: 1-10)");
		end.setRequired(false);
		options.addOption(end);

		/* a different constructor to give more arguments to an option */
		@SuppressWarnings({ "deprecation", "static-access" })
		Option services = OptionBuilder.withArgName("Name of the services").withValueSeparator(' ').hasOptionalArgs()
				.withLongOpt("services").withDescription("Name of the extra services which will receive the messages")
				.create('S');

		services.setRequired(false);
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

		/* When you are in RECEIVER mode */
		/////////////////////////////////
		if (cmd.getOptionValue("recieverState") != null) {
			setReciever(true);
			System.out.println(cmd.getOptionValue("recieverState"));
			addService(cmd.getOptionValue("recieverState"));
			/* Add the services names for the routing keys */
			int i = 0;
			try {
				while ((cmd.getOptionValues("services")[i] != null)) {
					addService(cmd.getOptionValues("services")[i]);
					i++;
				}
			} catch (Exception e) {
				System.out.println(getNamesServices().toString());

			}

			/* when you are in PUBLISHER mode */
			////////////////////////////////////
		} else {
			if (cmd.getOptionValue("end") != null) {
				setEND(Integer.parseInt(cmd.getOptionValue("end")));
			}
			if (cmd.getOptionValue("start") != null) {
				setSTART(Integer.parseInt(cmd.getOptionValue("start")));
			}
			if (cmd.getOptionValue("filePath") != null) {
				setFilePath(cmd.getOptionValue("filePath"));
				int i=0;
				try {
					while ((cmd.getOptionValues("servicesToSent")[i] != null)) {
						addServiceToSent(cmd.getOptionValues("servicesToSent")[i]);
						i++;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println(getNamesServices().toString());

				}
				//setServiceToSent((cmd.getOptionValue("servicesToSent")));
				System.out.println(cmd.getOptionValue("servicesToSent"));
				if (!(new File(TestingApplication.filePath).exists())) {
					throw new FileNotFoundException("Path to file is not correct.");
				}
				/* In case of a bad Path file */
				try {
					if (TestingApplication.serviceToSent.equals("")) {
						System.out.println("Please enter a queue name for sendind");
						System.exit(1);
						return;
					}
				} catch (NullPointerException m) {
					System.out.println(">>>>>> ERROR : No Queue name for sendind");
					System.out.println("--Please restart with a queue name for sendind---");
					System.exit(1);
					return;
				}

			}

		}

		/* Launch the Spring-boot application */
		new SpringApplicationBuilder(TestingApplication.class).web(false).run(args);
	}

}