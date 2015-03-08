package com.philips.hack.swift.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.model.Observation;
import org.hl7.fhir.instance.model.Organization;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.philips.hack.swift.Constants;
import com.philips.hack.swift.Constants.PHPS_OBJECT;
import com.philips.hack.swift.ConvertUtility;
import com.philips.hack.swift.config.User;
import com.philips.hack.swift.model.HSDPObject;
import com.philips.hack.swift.model.HSDPObservation;
import com.philips.hack.swift.model.HSDPOrganization;
import com.philips.hack.swift.model.HSDPPatient;

@RestController
public class PatientController {
	RestTemplate restTemplate = new RestTemplate();
	private User user;

	public User getUser() throws UnsupportedEncodingException,
			URISyntaxException, HttpException {
		if (user == null) {
			user = new User();
		}

		return user;
	}

	@RequestMapping("/hello")
	public @ResponseBody String hello() {
		System.out.println("Hitting the Hello Controller");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return "Hello buddy, Now the time is " + dateFormat.format(date);
	}

	@RequestMapping("/login")
	public @ResponseBody String login() throws UnsupportedEncodingException,
			URISyntaxException, HttpException {
		System.out.println("Hitting the AUTH");
		User user = getUser();
		return "token:" + user.getToken();
	}

	@RequestMapping("/logout")
	public @ResponseBody boolean logout() throws UnsupportedEncodingException,
			URISyntaxException, HttpException {
		User user = getUser();
		return user.logout();
	}

	@RequestMapping(value = "/observation/{patientid}/{observationid}", method = RequestMethod.GET)
	public @ResponseBody String getObservation(
			@PathVariable("patientid") String patientId,
			@PathVariable("observationid") String observationId)
			throws URISyntaxException, HttpException, IOException {

		String observationCode = ConvertUtility.getObservationId(observationId);

		String url = Constants.BASE_URL_OBSERVATION + "?subject._id="
				+ patientId + "&name="
				+ URLEncoder.encode("http://loinc.org|" + observationCode)
				+ "?_format=json?_pretty=true";
		// System.out.println(url);
		return getData(url);
	}

	@RequestMapping(value = "/observationid/{observationid}", method = RequestMethod.GET)
	public @ResponseBody String getObservationCode(
			@PathVariable("observationid") String observationId)
			throws URISyntaxException, HttpException, IOException {

		// String observationCode =
		// ConvertUtility.getObservationId(observationId);
		String patientId = "a102";
		String url = Constants.BASE_URL_OBSERVATION + "?subject._id="
				+ patientId + "&name="
				+ URLEncoder.encode("http://loinc.org|" + observationId)
				+ "&_format=json&_pretty=true";
		// System.out.println(url);
		return getData(url);
	}

	@RequestMapping(value = "/observation/{observationname}", method = RequestMethod.GET)
	public @ResponseBody String getObservation(
			@PathVariable("observationname") String observationname)
			throws URISyntaxException, HttpException, IOException {

		String observationCode = ConvertUtility
				.getObservationId(observationname);
		String patientId = "a102";
		String url = Constants.BASE_URL_OBSERVATION + "?subject._id="
				+ patientId + "&name="
				+ URLEncoder.encode("http://loinc.org|" + observationCode)
				+ "&_format=json&_pretty=true";
		// System.out.println(url);
		String json = getData(url);
		String response = jsonParseToObject(json);
		return response;
	}

	@RequestMapping(value = "/patient/{id}", method = RequestMethod.GET)
	public @ResponseBody String getPatientInfo(@PathVariable("id") String id)
			throws URISyntaxException, HttpException, IOException {
		String url = Constants.BASE_URL_PATIENT + "/" + id
				+ "?_format=json?_pretty=true";
		return getData(url);
	}

	public String getData(String url) throws URISyntaxException, HttpException,
			IOException {
		User user = new User();
		String patientId = user.getPatientID();
		// System.out.println(patientId);

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET con.setRequestMethod("GET");

		// add request header // con.setRequestProperty("User-Agent",
		// USER_AGENT);
		con.setRequestProperty("Authorization",
				"Bearer " + user.getAccessToken());
		con.setRequestProperty("Accept", "application/json");

		int responseCode = con.getResponseCode();
		// System.out.println("\nSending 'GET' request to URL : " + url);
		// System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		user.logout();
		// System.out.println("Logged out user successfully");
		// System.out.println(response.toString());
		return response.toString();
	}

	public static HSDPObject getPHPSObjectFromJson(PHPS_OBJECT inWhat,
			JsonParser fhirJsonParser, JsonObject jsonObject) {
		try {
			Resource resource = fhirJsonParser.parse(jsonObject);

			switch (inWhat) {
			case Patient:
				Patient patientObj = (Patient) resource;
				HSDPPatient phpsPatientObject = new HSDPPatient(patientObj);
				return phpsPatientObject;
			case Observation:
				Observation observationObj = (Observation) resource;
				HSDPObservation phpsObservationObject = new HSDPObservation(
						observationObj);
				return phpsObservationObject;
			case Organization:
				Organization organizationObj = (Organization) resource;
				HSDPOrganization phpsOrganizationObject = new HSDPOrganization(
						organizationObj);
				return phpsOrganizationObject;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String jsonParseToObject(String request) {
		StringBuffer response = new StringBuffer();
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		JsonObject inJsonComplete = parser.parse(request).getAsJsonObject();

		JsonParser fhirJsonParser = new JsonParser();

		boolean set = false;

		response.append("{");

		if (inJsonComplete.has("entry")) {
			JsonArray jsonList = inJsonComplete.getAsJsonArray(("entry"));

			for (int i = 0; i < jsonList.size(); i++) {
				// Data comes with double quotes "SomeTitle", so removing double
				// quotes
				String title = jsonList.get(i).getAsJsonObject().get("title")
						.toString().replaceAll("^\"|\"$", "");
				String id = jsonList.get(i).getAsJsonObject().get("id")
						.toString().replaceAll("^\"|\"$", "");
				;
				String dateUpdated = jsonList.get(i).getAsJsonObject()
						.get("updated").toString().replaceAll("^\"|\"$", "");

				JsonObject observationJson = jsonList.get(i).getAsJsonObject()
						.getAsJsonObject("content");

				HSDPObservation phpsObservation = (HSDPObservation) getPHPSObjectFromJson(
						PHPS_OBJECT.Observation, fhirJsonParser,
						observationJson);
				if (phpsObservation == null)
					return null;

				phpsObservation.setExtraInfo(id, title, dateUpdated);

				response.append("{");
				response.append(phpsObservation.getObservationName());
				response.append(":");
				response.append(phpsObservation.getObservationValue());
				response.append("}");
				response.append(",");
				/*
				 * System.out.println(phpsObservation.getObservationName() +
				 * " - " + phpsObservation.getObservationValue());
				 */
			}
		} else {
			HSDPObservation phpsObservation = (HSDPObservation) getPHPSObjectFromJson(
					PHPS_OBJECT.Observation, fhirJsonParser, inJsonComplete);
			response.append("{");
			response.append(phpsObservation.getObservationName());
			response.append(":");
			response.append(phpsObservation.getObservationValue());
			response.append("}");
			response.append(",");
			if (phpsObservation == null)
				return null;
		}
		response.append("}");
		return response.toString();

	}
}
