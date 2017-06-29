package com.firstInfra;



import java.util.Scanner;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestingApplication {

    private static String queueName;
    //public static String ipServer="";
    


	public static void setQueueName(String queueName) {
		TestingApplication.queueName = queueName;
	}

	@Bean
    /*Good way to parse argument*/
    public ConnectionFactory connectionFactory(@Value("${serverIP}")String ipServer) {
    //public ConnectionFactory connectionFactory() {
    	return new CachingConnectionFactory(ipServer);

    }

    @Bean
    Queue queue(@Value("${queueName}")String queueName) {
    	setQueueName(queueName);
        return new Queue(queueName, false);
    }

    /**
    @Bean
    TopicExchange exchange() {
        return new TopicExchange("spring-boot-exchange");
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queueName);
    }

    @Bean
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
    	
    	if (args.length==0){
    		System.out.println("WARNING : Missing Argumrnts.");
    	}
    	if (args.length!=2){
    		System.out.println("Usage: java -jar App.jar --serverIP=[IP-ADDRESS-RABBITMQ-SERVER] --queueName=[NAME-OF-THE-QUEUE]");
    		return;
    	}
    	/**
    	Scanner sc = new Scanner(System.in);
    	while (queueName.equals("")){
    		System.out.println("--> Please enter a name for the Queue: ");
    		queueName=sc.nextLine();
    	}
    	while(ipServer.equals("")){
    		System.out.println("--> Please enter the Ip of the RabbitMQ server: ");
    		ipServer=sc.nextLine();
    	}
    	*/
    	
        new SpringApplicationBuilder(TestingApplication.class).web(false).run(args);   
       // sc.close();
    }

}