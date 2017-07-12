package com.withBoardManager.receiver;



import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@Controller
public class SpringReceiver {

	private static Queue queue;
	private ModelMap model=new ModelMap();
	
	public static void setQueueForUI(Queue queueForUI) {
		queue=queueForUI;
	}
	
    
    @RequestMapping("/index")
    public ModelMap index() {
    	model.addAttribute("queue",queue.getName());
    	return model;
    }	
    
    
}
