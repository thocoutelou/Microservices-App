package com.withBoardManager.boardManager;

import java.io.FileReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {

	public static JSONObject data;
	public static JSONArray boards;
	
	public static void configureData(String path) {
		JSONParser parser = new JSONParser();
		try {

			Object obj = parser.parse(new FileReader(path));
			data = (JSONObject) obj;
			System.out.println(data.get("boards").toString());
			boards= (JSONArray) data.get("boards");
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
