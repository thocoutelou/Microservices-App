package com.withBoardManager.boardManager;


import java.util.concurrent.CountDownLatch;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringReceiver {

    private CountDownLatch latch = new CountDownLatch(1);
	private final ConfigurableApplicationContext context;

	public SpringReceiver(ConfigurableApplicationContext context) {
		this.context=context;
	}
    

    public void receiveMessage(String message) throws Exception {
    	
    	String urlToRead = (String) context.getBean("httpServer");
        System.out.println(urlToRead);

        System.out.println("Received <" + message + ">");
        if(message.equals("Event")){
        	//DONE send an HTTP REQUEST too the CountingManager
        	String result=HTTPrequest.getHTML(urlToRead+"/event");
        	System.out.println("From HTTP server: " + result);
        }
        latch.countDown();
    }

    
    
    public CountDownLatch getLatch() {
        return latch;
    }
    
    
}

