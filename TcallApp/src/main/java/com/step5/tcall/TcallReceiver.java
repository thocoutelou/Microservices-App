package com.step5.tcall;

import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class TcallReceiver {

	
private CountDownLatch latch = new CountDownLatch(1);
    
    

    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
        try {
            JSONObject json =new JSONObject(message);
            CallController.setJsonResponse(json);
        }
        catch (Exception e) {
			System.out.println("Not a valide JSON object.");
		}
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
}
}
