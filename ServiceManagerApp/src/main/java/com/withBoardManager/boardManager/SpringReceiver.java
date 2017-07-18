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

		// TODO Change the port
		String result = HTTPrequest.getHTML("http://" + urlToRead + ":8088/" + message);
		JSONObject json = new JSONObject(result);
		System.out.println("The Web Counting server has sent: " + json.toString());
		if (json.getBoolean("acall")) {
			/* For the Tcall & boards */
			if (!ServiceManagerApplication.serviceToSent.contains(json.get("service"))){
				/*There are no ticket for this service*/
				return "{\"error\":\" The service is not avaliable\" }";
			}
			((HttpResponse) context.getBean("httpResponse")).setResponse((String) json.get("service"));
			
			((HttpResponse) context.getBean("httpResponse")).setCount((int) json.get("count"));
			/*Launch the http Response*/
			((HttpResponse) context.getBean("httpResponse")).setNew(true);
			json.put("id",
					Runner.compactAnswer(json.getInt("count"), json.get("service").toString(),
							Runner.getServicedManaged().indexOf(json.get("service"))));
			System.out.println(">>> Sending to TcallApp...");
			return json.toString();
		} else {
			/* For the Kiosk */
			System.out.println(">>> Sending to EmitterApp (kiosk) ...");
			System.out.println("The ticket:" +json.toString());
			return json.toString();
		}

	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
