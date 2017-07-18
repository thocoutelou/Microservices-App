package com.step5.emitter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EmitterApp {

	public static String ipServer;
	public static String service;

	public static void setService(String service) {
		EmitterApp.service = service;
	}

	public static void setIpServer(String ipServer) {
		EmitterApp.ipServer = ipServer;
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
	TopicExchange exchange() {
		return new TopicExchange("ke-emitters-exchange");
	}

	public static void main(String[] args) {

		Options options = new Options();

		Option ipServer = new Option("i", "ipServer", true, "Ip of the RabbitMQ server");
		ipServer.setRequired(true);
		options.addOption(ipServer);

		/*
		 * Option service = new Option("s", "service", true,
		 * "name of the service for sending"); service.setRequired(true);
		 * options.addOption(service);
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

		System.out.println("Emitter Application is started...");

		/* Parse the arguments to the beans constructor */
		setIpServer(cmd.getOptionValue("ipServer"));
		setService("CallForBoardManager");

		/* Launch the Spring-boot application */
		new SpringApplicationBuilder(EmitterApp.class).run(args);
	}
}
