package com.firstInfra;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.cli.*;

@SpringBootApplication
public class TestingApplication {

	private static String queueName;
	public static String ipServer;
	public static boolean isReciever = false;
	public static String filePath = "";
	public static String queueToSent = "";

	/* Get and set methods */

	public static void setQueueToSent(String queueToSent) {
		TestingApplication.queueToSent = queueToSent;
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

	public static void setQueueName(String queueName) {
		TestingApplication.queueName = queueName;
	}

	/* Beans creation */
	

	@Bean
	public static boolean isReciever() {
		return isReciever;
	}

	@Bean
	public static String pathToFile() {
		return filePath;
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
	public String queueToSent() {
		return queueToSent;
	}

	@Bean
	@Conditional(RecieverCondition.class)
	Queue queue() {
		return new Queue(queueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	@Conditional(RecieverCondition.class)
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(queueName);
	}

	@Bean
	@Conditional(RecieverCondition.class)
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	@Conditional(RecieverCondition.class)
	MessageListenerAdapter listenerAdapter(SpringReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		Options options = new Options();

		Option ipServer = new Option("i", "ipServer", true, "Ip of the RabbitMQ server");
		ipServer.setRequired(true);
		options.addOption(ipServer);

		Option recieverState = new Option("r", "recieverState", true,
				"Configure the client to be a reciever [NAME-OF-THE-RECIEVING-QUEUE]");
		recieverState.setRequired(false);
		options.addOption(recieverState);

		Option filePath = new Option("f", "filePath", true, "Path of the file to read for sending");
		filePath.setRequired(false);
		options.addOption(filePath);

		Option queueToSent = new Option("q", "queueToSent", true, "The name of the queue which messages will be sent");
		queueToSent.setRequired(false);
		options.addOption(queueToSent);

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

		setIpServer(cmd.getOptionValue("ipServer"));
		/* When you are in client mode */
		if (cmd.getOptionValue("recieverState") != null) {
			setReciever(true);
			setQueueName(cmd.getOptionValue("recieverState"));

			/* when you are in publisher mode */
		} else {
			if (cmd.getOptionValue("filePath") != null) {
				setFilePath(cmd.getOptionValue("filePath"));
				setQueueToSent(cmd.getOptionValue("queueToSent"));
				if (!(new File(TestingApplication.filePath).exists())) {
					throw new FileNotFoundException("Path to file is not correct.");
				}
				try {
					if (TestingApplication.queueToSent.equals("")) {
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

		new SpringApplicationBuilder(TestingApplication.class).web(false).run(args);
	}

}