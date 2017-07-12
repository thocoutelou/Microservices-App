package com.withBoardManager.receiver;


import java.util.concurrent.CountDownLatch;

import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@Controller
public class SpringReceiver {

	private Queue queue;
	private ModelMap model=new ModelMap();
	
	public void setQueueForUI(Queue queueForUI) {
		this.queue=queueForUI;
	}
	
    private CountDownLatch latch = new CountDownLatch(1);
    
    

    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");

        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
    
    @RequestMapping("/index")
    public ModelMap index() {
    	model.addAttribute("queue",queue.getName());
    	return model;
    }	
    
    
}
