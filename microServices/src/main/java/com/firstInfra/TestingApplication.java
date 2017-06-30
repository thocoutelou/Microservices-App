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
import org.apache.commons.cli.*;

@SpringBootApplication
public class TestingApplication {

    private static String queueName;
    public static String ipServer;
    public static boolean isReciever=false;
    

	public static void setReciever(boolean isReciever) {
		TestingApplication.isReciever = isReciever;
	}

	public static void setIpServer(String ipServer) {
		TestingApplication.ipServer = ipServer;
	}

	public static void setQueueName(String queueName) {
		TestingApplication.queueName = queueName;
	}

	@Bean
	public String ipServer(){
		return(ipServer);
	}
	
	@Bean
	public static boolean isReciever() {
		return isReciever;
	}

	@Bean
    /*Good way to parse argument*/
    public ConnectionFactory connectionFactory(String ipServer) {
    	return new CachingConnectionFactory(ipServer);

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
    MessageListenerAdapter listenerAdapter(SpringReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }


    public static void main(String[] args) throws InterruptedException {
    	
    	Options options = new Options();

        Option ipSever = new Option("i", "ip-server", true, "Ip of the RabbitMQ server");
        ipSever.setRequired(true);
        options.addOption(ipSever);

        /**
        Option queueName = new Option("q", "queueName", true, "Name of the queue for messages reception");
        ipSever.setRequired(true);
        options.addOption(queueName);
        */
        
        Option recieverState = new Option("r", "recieverState", true, "Configure the client to be a reciever [NAME-OF-THE-RECIEVING-QUEUE]");
        recieverState.setRequired(false);
        options.addOption(recieverState);

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
        
        
        setReciever(cmd.hasOption("recieverState"));
        setIpServer(cmd.getOptionValue("ipServer"));
        setQueueName(cmd.getOptionValue("recieverState"));

    
    	
        new SpringApplicationBuilder(TestingApplication.class).web(false).run(args);   
    }

}