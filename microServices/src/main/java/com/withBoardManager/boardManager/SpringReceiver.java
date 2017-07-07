package com.withBoardManager.boardManager;

import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringReceiver {

	private CountDownLatch latch = new CountDownLatch(1);
	private final ConfigurableApplicationContext context;

	public SpringReceiver(ConfigurableApplicationContext context) {
		this.context = context;
	}

	public void receiveMessage(String message) throws Exception {

		String urlToRead = (String) context.getBean("httpServer");
		// System.out.println(urlToRead);

		System.out.println("Received <" + message + ">");

		if (message.equals("Event")) {
			String result = HTTPrequest.getHTML("http://" + urlToRead + ":8080/event");
			JSONObject json = new JSONObject(result);
			((HttpResponse) context.getBean("httpResponse")).setResponse((String) json.get("service"));
			((HttpResponse) context.getBean("httpResponse")).setCount((int) json.get("count"));
			((HttpResponse) context.getBean("httpResponse")).setNew(true);
			System.out.println("The server has sent: " + ((HttpResponse) context.getBean("httpResponse")).getResponse()
					+ " with the counter: " + ((HttpResponse) context.getBean("httpResponse")).getCount());
		}
		latch.countDown();

	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
