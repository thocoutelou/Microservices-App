package com.withBoardManager.webServer;

import java.util.HashMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.apache.commons.cli.*;

@SpringBootApplication
public class WebApplication {

	public static HashMap<String, Integer> serviceToSent = new HashMap<String, Integer>();
	public static int numberOfServices;

	public static HashMap<String, Integer> getServiceToSent() {
		return serviceToSent;
	}

	private static void setNumberOfServices(int nb) {
		numberOfServices = nb;
	}

	private static void addServiceToSent(String service) {
		serviceToSent.put(service, 0);
	}

	@Bean
	public HashMap<String, Integer> serviceToSent() {
		return serviceToSent;
	}

	/* MAIN */
	public static void main(String[] args) throws Exception {

		Options options = new Options();

		@SuppressWarnings({ "deprecation", "static-access" })
		Option servicesToSent = OptionBuilder.withArgName("ServicesNames for sending").withValueSeparator(' ')
				.hasArgs(1).hasOptionalArgs().withLongOpt("servicesToSent")
				.withDescription("Name of the services which will receive the messages").create('t');
		servicesToSent.setRequired(true);
		options.addOption(servicesToSent);

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

		int i = 0;
		try {
			while ((cmd.getOptionValues("servicesToSent")[i] != null)) {
				addServiceToSent(cmd.getOptionValues("servicesToSent")[i]);
				i++;
			}
		} catch (Exception e) {
			setNumberOfServices(getServiceToSent().size());
		}
		
		System.out.println("Starting Web Server...");
		SpringApplication.run(WebApplication.class, args);
		/**
		 * HttpServer server = HttpServer.create(new InetSocketAddress(8888), 0);
		 * System.out.println("Starting Web Server..."); server.createContext("/event",
		 * new MyHandler()); server.start();
		 */
	}

}