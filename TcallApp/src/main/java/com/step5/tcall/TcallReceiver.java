package com.step5.tcall;

import java.util.concurrent.CountDownLatch;


import static com.step5.tcall.Log.LOG_ON;
import static com.step5.tcall.Log.COMM;


import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class TcallReceiver {

	
private CountDownLatch latch = new CountDownLatch(1);
    
    

    public void receiveMessage(String message) {
    	if (LOG_ON && COMM.isInfoEnabled()) 
			COMM.info("RECEIVED: "+message);
        //System.out.println("Received <" + message + ">");
        try {
            JSONObject json =new JSONObject(message);
            CallController.setJsonResponse(json);
        }
        catch (Exception e) {
        	if (LOG_ON && COMM.isInfoEnabled()) 
        		COMM.error("ERROR: Not a valid JSON object");
			//System.out.println("Not a valid JSON object.");
		}
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
}
}
