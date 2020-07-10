package org.mule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Bootstrap {

	static String propertyPrepender = "route.";
	
	
	public static java.util.ArrayList <HashMap<String, String>> init()
	{
		ArrayList<Integer> routeSequence = new ArrayList <Integer>();
		
		ArrayList<HashMap<String, String>> routes = new ArrayList <HashMap<String, String>>();
		
		for(String propertyName : System.getProperties().stringPropertyNames()) {
			if (propertyName.startsWith(Bootstrap.propertyPrepender)) {
				String[] sequence = propertyName.split("\\.",2); 
				if ((sequence.length == 2) && (sequence[1].matches("\\d+"))) 
					routeSequence.add(Integer.valueOf(sequence[1]));	
			}
		}
		Collections.sort(routeSequence);
		for (int sequence : routeSequence) {
			String routeID = "route." + String.valueOf(sequence);
			String route = System.getProperty(routeID);
			String[] routeArray = route.split("@", 2);
			HashMap<String, String> routeMap = new HashMap<String, String>();
			routeMap.put("path", routeArray[0]);
			routeMap.put("url", routeArray[1]);
			routes.add(routeMap);
		}
		return routes;
	}
	

}
