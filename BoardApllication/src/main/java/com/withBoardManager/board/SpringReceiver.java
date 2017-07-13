package com.withBoardManager.board;



import java.util.concurrent.CountDownLatch;

import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@Controller
public class SpringReceiver {

	private static Queue queue;
	private static String ipServer;
	private ModelMap model=new ModelMap();
	
	public static void setQueueForUI(Queue queueForUI) {
		queue=queueForUI;
	}
	public static void setIpServer(String ip) {
		ipServer=ip;
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
    	model.addAttribute("ipserver", ipServer);
    	return model;
    }	
    
    
}
