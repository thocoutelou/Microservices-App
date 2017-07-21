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

		if (message.equals("configuration")) {
			/* for configuration reloading */
			JSONObject json = new JSONObject();
			try {
				/* load new configuration from the DB */
				System.out.println("Configuration reloaded from the database");
				RedirectController.configureData(ServiceManagerApplication.addressDB);
				json.put("Configuration", "reloaded");
			} catch (Exception e) {
				json.put("error", "new configuration failed");
			}
			return json.toString();

		} else {

			String urlToRead = ServiceManagerApplication.getHttpServer();

			System.out.println("Received < " + message + " >");

			String result = HTTPrequest.getHTML("http://" + urlToRead + ":8088/" + message);
			try {
				JSONObject json = new JSONObject(result);
				System.out.println("The Web Counting server has sent: " + json.toString());
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
					System.out.println(">>> Sending to TcallApp...");
					return json.toString();
				} else {
					/* For the Kiosk */
					json.remove("acall");
					json.put("id", Runner.compactAnswer(json.getInt("ticketNumber"), json.get("service").toString(),
							Runner.getServicedManaged().indexOf(json.get("service"))));
					json.remove("ticketNumber");
					System.out.println(">>> Sending to EmitterApp (kiosk) ...");
					System.out.println("The ticket:" + json.toString());
					return json.toString();
				}
			} catch (Exception e) {
				return ("Error from the ServiceManager: type=Internal Server Error, status=500");
			}
		}

	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
