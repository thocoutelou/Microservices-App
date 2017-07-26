package com.withBoardManager.board;

import static com.withBoardManager.board.Log.GEN;
import static com.withBoardManager.board.Log.LOG_ON;

import java.util.ArrayList;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BoardViewerApplication {

	
	public static String IpManager;
	
	public static String ipServer;
	public static ArrayList<String> services = new ArrayList<>();



	public static void setIpServer(String ipServer) {
		BoardViewerApplication.ipServer = ipServer;
		IndexController.setIpServer(ipServer);
	}
	public static void setIpManager(String ipManager) {
		IpManager = ipManager;
		IndexController.setIpManager(ipManager);
	}	
	@Bean
	public String ipServer() {
		return (ipServer);
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
			if (LOG_ON && GEN.isEnabledFor(Level.INFO)) 
				GEN.error(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
			return;
		}

		/* Parse the arguments to the beans constructor */
		//////////////////////////////////////////////////

		setIpServer(cmd.getOptionValue("ipServer"));
		setIpManager(cmd.getOptionValue("ipManager"));
		

		/* Launch the Spring-boot application */
		new SpringApplicationBuilder(BoardViewerApplication.class).run(args);
	}
}
