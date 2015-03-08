package com.philips.hack.swift;

import java.util.HashMap;
import java.util.Map;

public class ConvertUtility {
	public static final Map<String, String> result = new HashMap<String, String>();

	public static String getObservationId(String name) {
		result.put("bloodpressure", "55284-4");
		result.put("vitamin-d", "35365-6");
		result.put("pulserate", "8867-4");
		result.put("glucose", "2339-0");
		result.put("bodytemperature", "8310-5");
		result.put("mood", "52497-5");
		result.put("bodyweight","3141-9");
		result.put("steps", "41950-7");
		result.put("painlevel", "38221-8");

		return result.get(name);
	}

}
