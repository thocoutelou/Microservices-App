package com.step5.emitter;

import static com.step5.emitter.Log.COMM;

import static com.step5.emitter.Log.LOG_ON;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Level;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class EmitReceiver {

	
private CountDownLatch latch = new CountDownLatch(1);
    
    

    public void receiveMessage(String message) {
    	if (LOG_ON && COMM.isEnabledFor(Level.WARN)) 
			COMM.log(Level.INFO,"RECEIVE: "+message);
        //System.out.println("Received <" + message + ">");
        try {
            JSONObject json =new JSONObject(message);
            EmitController.setJsonResponse(json);
        }
        catch (Exception e) {
        	if (LOG_ON && COMM.isEnabledFor(Level.WARN)) 
        		COMM.error("Not a valide JSON object.");
			//System.out.println("Not a valide JSON object.");
		}
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
}
}
