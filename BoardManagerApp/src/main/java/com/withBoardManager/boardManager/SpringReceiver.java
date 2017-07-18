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

		String urlToRead = ManagerBoardApplication.getHttpServer();
		// System.out.println(urlToRead);

		System.out.println("Received <" + message + ">");

		//if (InetAddressUtils.isIPv4Address(message)) {
			String result = HTTPrequest.getHTML("http://" + urlToRead + ":8088/event?"+message);
			JSONObject json = new JSONObject(result);
			System.out.println("The Web Counting server has sent: " + json.toString());
			/*For the boards*/
			((HttpResponse) context.getBean("httpResponse")).setResponse((String) json.get("service"));
			((HttpResponse) context.getBean("httpResponse")).setCount((int) json.get("count"));
			((HttpResponse) context.getBean("httpResponse")).setNew(true);
			/*For the Tcall*/
			((JSONObject)json.get("ticket")).put("id",Runner.compactAnswer(json.getInt("count"), 
					json.get("service").toString(), Runner.getServicedManaged().indexOf(json.get("service"))));
			System.out.println(">>> Sending to TcallApp..."+json.toString());
			return json.toString();
		//}
		//latch.countDown();

	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
