package com.withBoardManager.boardManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;



@RestController
public class RedirectController {

	public static JSONObject data;
	public static JSONArray boards=new JSONArray();
	
	@SuppressWarnings("unchecked")
	public static void configureData(String address) {
		//JSONParser parser = new JSONParser();
		try {
			Jedis client = new Jedis(address);
			client.auth("redis");
			Set<String>board=client.smembers("boards");
			System.out.println(board.toString());
			JSONObject interData=new JSONObject();
			Iterator<String>iter = board.iterator();
			while(iter.hasNext()){
				String key= iter.next();
				String ip=client.lpop(key).toString();
				interData.putIfAbsent("ip", ip);
				interData.putIfAbsent("services", client.lrange(key,0,-1));
				client.lpush(key, ip);
				boards.add(interData.clone());
				interData.clear();
			}
			System.out.println(boards.toString());
			client.close();
			//Object obj = parser.parse(new FileReader(path));
			//data = (JSONObject) obj;
			//System.out.println(value);
			//boards= (JSONArray) data.get("boards");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private ArrayList<String> findServices(String ip) {
		ArrayList<String>services=null;
		for (int i=0;i<boards.size();i++) {
			if(((JSONObject)boards.get(i)).get("ip").equals(ip)) {
				services=new ArrayList<>();
				JSONArray tab =(JSONArray) ((JSONObject)boards.get(i)).get("services");
				for (int j=0;j<tab.size();j++) {
					services.add(tab.get(j).toString());
				}
			}
		}
		return services;
	}
	
	
	@RequestMapping("/queue")
	public String queue(@RequestParam(value="ip")String ip) {
		System.out.println("Request from: "+ip);
		ArrayList<String>services = findServices(ip);
		if(services==null){
			return "Not a valide ip";
		} else {
			String name=ServiceManagerApplication.createAndConfigureQueue(services);
			return name;
		}
	}

}
