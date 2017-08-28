package com.withBoardManager.boardManager;

import static com.withBoardManager.boardManager.Log.*;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Level;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringReceiver {

	private CountDownLatch latch = new CountDownLatch(1);
	private final ConfigurableApplicationContext context;

	public SpringReceiver(ConfigurableApplicationContext context) {
		this.context = context;
	}

	@RabbitListener(queues = "bm.rpc.requests")
	public String receiveMessage(String message) throws Exception {

		if (message.equals("configuration")) {
			/* for configuration reloading */
			JSONObject json = new JSONObject();
			try {
				/* load new configuration from the DB */
				if (LOG_ON && GEN.isEnabledFor(Level.INFO)) 
					GEN.log(Level.INFO,"Configuration reloaded from the database");
				//System.out.println("Configuration reloaded from the database");
				RedirectController.configureData(ServiceManagerApplication.addressDB,ServiceManagerApplication.getPassDB());
				json.put("Configuration", "reloaded");
			} catch (Exception e) {
				json.put("error", "new configuration failed");
			}
			return json.toString();

		} else {

			String urlToRead = ServiceManagerApplication.getHttpServer();
			if (LOG_ON && COMM.isEnabledFor(Level.INFO)) 
				COMM.log(Level.INFO,"RECEIVE: " +message);
			//System.out.println("Received < " + message + " >");

			String result = HTTPrequest.getHTML("http://" + urlToRead + ":8088/" + message);
			try {
				JSONObject json = new JSONObject(result);
				if (LOG_ON && COMM.isEnabledFor(Level.INFO)) 
					COMM.log(Level.INFO,"COUNTER: The Web Counting server has sent: " + json.toString());
				//System.out.println("The Web Counting server has sent: " + json.toString());
				if (json.getBoolean("acall")) {
					json.remove("acall");
					/* For the Tcall & boards */
					if (!ServiceManagerApplication.serviceToSent.contains(json.get("service"))) {
						/* There are no ticket for this service */
						return "{\"error\":\" The service is not avaliable\" }";
					}
					((HttpResponse) context.getBean("httpResponse")).setResponse((String) json.get("service"));

					((HttpResponse) context.getBean("httpResponse")).setCount((int) json.get("ticketCalled"));
					/* Launch the http Response */
					((HttpResponse) context.getBean("httpResponse")).setNew(true);
					json.put("id", Runner.compactAnswer(json.getInt("ticketCalled"), json.get("service").toString(),
							Runner.getServicedManaged().indexOf(json.get("service"))));
					json.remove("ticketCalled");
					if (LOG_ON && COMM.isEnabledFor(Level.INFO)) 
						COMM.log(Level.INFO,"SEND: sending to TcallApp...");
					//System.out.println(">>> Sending to TcallApp...");
					return json.toString();
				} else {
					/* For the Kiosk */
					json.remove("acall");
					json.put("id", Runner.compactAnswer(json.getInt("ticketNumber"), json.get("service").toString(),
							Runner.getServicedManaged().indexOf(json.get("service"))));
					json.remove("ticketNumber");
					if (LOG_ON && COMM.isEnabledFor(Level.INFO)) 
						COMM.log(Level.INFO,"SEND: sending to EmitterApp (kiosk) the ticket: "+ json.toString());
					//System.out.println(">>> Sending to EmitterApp (kiosk) ...");
					//System.out.println("The ticket:" + json.toString());
					return json.toString();
				}
			} catch (Exception e) {
				if (LOG_ON && COMM.isEnabledFor(Level.INFO)) 
					COMM.log(Level.INFO,"ERROR: type=Internal Server Error, status=500");
				return ("Error from the ServiceManager: type=Internal Server Error, status=500");
			}
		}

	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
