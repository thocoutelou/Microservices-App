package com.publisher;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



import com.rabbitmq.client.Connection;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * 
 *Receiver Class without the Spring-boot framework for a lighter execution.
 */
public class LightPublisherApp 
{
	
	
	public static void main( String[] args ) throws IOException, TimeoutException, InterruptedException
    {
		if(args.length!=2) {
			System.out.println("usage: java -jar App.jar <IpServer> <RoutingKey>");
			return;
		}
		
		System.out.println("Publisher is started...");
		System.out.println("Press [Ctrl-C] to quit...");


		/* Create the connection*/
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(args[0]);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare("sb-boardManager-exchange","topic",true);
		
		String routingKey = args[1];
		String message="Event";
		BasicProperties var=MessageProperties.TEXT_PLAIN;
		Random rand= new Random();
		
		while (true) {
			TimeUnit.SECONDS.sleep(rand.nextInt(10));
			System.out.println("Sending an event...");
			
			channel.basicPublish("sb-boardManager-exchange", routingKey, var, message.getBytes("UTF-8"));
			System.out.println("Target: " + routingKey);
			System.out.println("--------");
		}
    }
}
