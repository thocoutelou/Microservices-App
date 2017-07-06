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
        	((HttpResponse) context.getBean("httpResponse")).setResponse(result);
        	((HttpResponse) context.getBean("httpResponse")).setNew(true);
        	System.out.println("The server has sent: "+((HttpResponse) context.getBean("httpResponse")).getResponse());
        }
        latch.countDown();
    }

    
    
    public CountDownLatch getLatch() {
        return latch;
    }
    
    
}

