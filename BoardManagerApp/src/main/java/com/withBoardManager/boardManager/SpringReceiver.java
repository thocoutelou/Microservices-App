package com.withBoardManager.boardManager;

import java.util.concurrent.CountDownLatch;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONObject;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringReceiver {

	private CountDownLatch latch = new CountDownLatch(1);
	@SuppressWarnings("unused")
	private final ConfigurableApplicationContext context;

	public SpringReceiver(ConfigurableApplicationContext context) {
		this.context = context;
	}

	public void receiveMessage(String message) throws Exception {

		String urlToRead = ManagerBoardApplication.getHttpServer();
		// System.out.println(urlToRead);

		System.out.println("Received <" + message + ">");

		//if (InetAddressUtils.isIPv4Address(message)) {
			String result = HTTPrequest.getHTML("http://" + urlToRead + ":8088/event?"+message);
			JSONObject json = new JSONObject(result);
			System.out.println("The Web Counting server has sent: " + json.toString());
			((HttpResponse) context.getBean("httpResponse")).setResponse((String) json.get("service"));
			((HttpResponse) context.getBean("httpResponse")).setCount((int) json.get("count"));
			((HttpResponse) context.getBean("httpResponse")).setNew(true);
			System.out.println(">>>Sending to TcallApp...");
			Runner.sendToTcall(json);
		//}
		latch.countDown();

	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
