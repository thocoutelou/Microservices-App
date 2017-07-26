package com.withBoardManager.webServer;

import static com.withBoardManager.webServer.Log.GEN;
import static com.withBoardManager.webServer.Log.LOG_ON;

import java.util.HashMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.apache.commons.cli.*;
import org.apache.log4j.Level;

@SpringBootApplication
public class WebCounterApplication {

	public static HashMap<String, CounterOfService> serviceToSent = new HashMap<String, CounterOfService>();
	public static int numberOfServices;
	public static HashMap<String, Event> lastCall=new HashMap<String,Event>();

	
	public static HashMap<String, CounterOfService> getServiceToSent() {
		return serviceToSent;
	}

	public static HashMap<String, Event> getLastCall() {
		return lastCall;
	}

	
	private static void setNumberOfServices(int nb) {
		numberOfServices = nb;
	}

	private static void addServiceToSent(String service) {
		serviceToSent.put(service, new CounterOfService());
	}

	@Bean
	public HashMap<String, CounterOfService> serviceToSent() {
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
			if (LOG_ON && GEN.isEnabledFor(Level.INFO)) 
				GEN.error( "ERROR: "+e.getMessage());
			//System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
			return;
		}

		int i = 0;
		try {
			while ((cmd.getOptionValues("servicesToSent")[i] != null)) {
				addServiceToSent(cmd.getOptionValues("servicesToSent")[i]);
				WebCounterApplication.getLastCall().put(cmd.getOptionValues("servicesToSent")[i], null);
				i++;
			}
		} catch (Exception e) {
			setNumberOfServices(getServiceToSent().size());
		}
		if (LOG_ON && GEN.isInfoEnabled()) 
			GEN.info("INIT: Starting Web Server and ready!");
		//System.out.println("Starting Web Server...");
		SpringApplication.run(WebCounterApplication.class, args);
	
	}

}