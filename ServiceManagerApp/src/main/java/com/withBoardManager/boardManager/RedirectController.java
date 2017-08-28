package com.withBoardManager.boardManager;

import static com.withBoardManager.boardManager.Log.COMM;
import static com.withBoardManager.boardManager.Log.GEN;
import static com.withBoardManager.boardManager.Log.LOG_ON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Level;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;

@RestController
public class RedirectController {

	public static JSONObject data;
	public static HashMap<String, String> boards = new HashMap<>();

	public static void configureData(String address, String password) {
		// JSONParser parser = new JSONParser();
		try {
			Jedis client = new Jedis(address);
			client.auth(password);
			Set<String> board = client.smembers("boards");
			if (LOG_ON && GEN.isDebugEnabled()) 
				GEN.debug("INIT: " + board.toString());
			//System.out.println(board.toString());
			//JSONObject interData = new JSONObject();
			Iterator<String> iter = board.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				String ip = client.lpop(key).toString();
				//interData.putIfAbsent("ip", ip);
				if (LOG_ON && GEN.isDebugEnabled()) 
					GEN.debug("INIT: " + ip);
				//System.out.println(ip);
				//interData.putIfAbsent("services", client.lrange(key, 0, -1));
				boards.put(ip, client.lrange(key, 0, -1).toString());
				client.lpush(key, ip);

				//interData.clear();
			}
			if (LOG_ON && GEN.isEnabledFor(Level.WARN)) 
				GEN.warn("INIT: " + boards.toString());
			//System.out.println(boards.toString());
			client.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> findServices(String ip) {
		ArrayList<String> services = null;
		if ((boards.containsKey(ip))) {
			services = new ArrayList<>();
			
			String pseudoTab = boards.get(ip);
			String tab[] = (pseudoTab.substring(1, pseudoTab.length() - 1).toString()).split(", ");

			for (int j = 0; j < tab.length; j++) {
				services.add(tab[j]);
			}
		}
		//System.out.println(services.toString());
		return services;
	}

	@RequestMapping("/queue")
	public String queue(@RequestParam(value = "ip") String ip) {
		if (LOG_ON && COMM.isEnabledFor(Level.WARN)) 
			COMM.warn("QUEUE: Request from: " + ip);
		//System.out.println("Request from: " + ip);
		ArrayList<String> services = findServices(ip);
		if (services == null) {
			return "Not a valide ip";
		} else {
			String name = ServiceManagerApplication.createAndConfigureQueue(services);
			return name;
		}
	}

}
