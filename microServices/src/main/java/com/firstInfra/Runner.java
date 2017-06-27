package com.firstInfra;


import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final SpringReceiver receiver;
    private final ConfigurableApplicationContext context;

    public Runner(SpringReceiver receiver, RabbitTemplate rabbitTemplate,
            ConfigurableApplicationContext context) {
        this.receiver = receiver;
        this.rabbitTemplate = rabbitTemplate;
        this.context = context;
    }

   
    public void run(String... args) throws Exception {
        System.out.println("Waiting message...");
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        context.close();
    }

}