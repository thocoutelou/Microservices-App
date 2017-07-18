package com.withBoardManager.boardManager;

import java.util.concurrent.CountDownLatch;

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

		String urlToRead = ServiceManagerApplication.getHttpServer();

		System.out.println("Received < " + message + " >");

		//TODO Change the port
		String result = HTTPrequest.getHTML("http://" + urlToRead + ":8088/" + message);
		JSONObject json = new JSONObject(result);
		System.out.println("The Web Counting server has sent: " + json.toString());
		
		/* For the boards */
		((HttpResponse) context.getBean("httpResponse")).setResponse((String) json.get("service"));
		((HttpResponse) context.getBean("httpResponse")).setCount((int) json.get("count"));
		((HttpResponse) context.getBean("httpResponse")).setNew(true);
		
		/* For the Tcall & Kiosk */
		JSONObject jsonTicket = ((JSONObject) json.get("ticket"));
		jsonTicket.put("id", Runner.compactAnswer(jsonTicket.getInt("ticketNumber"),
				jsonTicket.get("service").toString(), Runner.getServicedManaged().indexOf(jsonTicket.get("service"))));
		System.out.println(">>> Sending to TcallApp...");
		return jsonTicket.toString();

	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
