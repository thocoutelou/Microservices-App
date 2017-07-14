package com.withBoardManager.board;



import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Controller
public class IndexController {

	private static String ipServer;
	private static String ipManager;
	private ModelMap model=new ModelMap();
	
	public static void setIpServer(String ip) {
		ipServer=ip;
	}
	public static void setIpManager(String ip) {
		ipManager = ip;
	}
	//TODO remove useless things
    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
}
    
    @RequestMapping("/index")
    public ModelMap index(HttpServletRequest request) throws JsonProcessingException, IOException {
    	//DONE
    	//make a HTTP request to the BoardManager
    	RestTemplate restTemplate = new RestTemplate();
    	//Get the ip of the client
    	String ipSource = request.getRemoteAddr();
    	System.out.println(ipSource);
    	String bmUrl= "http://"+ipManager+":8080/queue?ip="+ipSource;
    	//GET the queueName
    	ResponseEntity<String> response
    	  = restTemplate.getForEntity( bmUrl, String.class);
    	String queue = (String)response.getBody();
    	
    	model.addAttribute("queue",queue);
    	model.addAttribute("ipserver", ipServer);
    	
    	return model;
    }	
    
    
}
