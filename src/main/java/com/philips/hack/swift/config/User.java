package com.philips.hack.swift.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.philips.hack.swift.Constants;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.commons.codec.binary.*;

public class User {
	String mAccessToken;
	HttpClient httpclient;
	com.google.gson.JsonParser parser;
	boolean mAuthenticated;
	private String mPatientUrlId;
	private String mOrganizationUrlId;
	private String pictureUrlString;

	public User() throws UnsupportedEncodingException, URISyntaxException,
			HttpException {
		httpclient = new DefaultHttpClient();
		parser = new JsonParser();
		authenticateApp(httpclient, parser);
	}

	private void authenticateApp(HttpClient httpClient,
			com.google.gson.JsonParser parser)
			throws UnsupportedEncodingException, URISyntaxException,
			HttpException {
		String authString = Constants.CLIENT_ID + ":" + Constants.CLIENT_SECRET;

		System.out.println(authString);
		String base64AuthString = "Basic "
				+ Base64.encodeBase64String(authString.getBytes());

		HttpPost httpPostToken = new HttpPost(Constants.BASE_URL_TOKEN);
		httpPostToken.setHeader("Authorization", base64AuthString);
		System.out.println(httpPostToken.getRequestLine());
		System.out.println(httpPostToken.getAllHeaders()[0].getName() + " "
				+ httpPostToken.getAllHeaders()[0].getValue());
		try {
			HttpResponse responseToken = httpClient.execute(httpPostToken);
			StatusLine statusLine = responseToken.getStatusLine();
			System.out.println("Completes Token");
			System.out.println(statusLine.getStatusCode());
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				JsonObject jsonComplete = parser.parse(
						getStringFromHttpResponse(responseToken))
						.getAsJsonObject();
				String accessToken = jsonComplete.get("access_token")
						.getAsString();
				System.out.println("Got Token:" + accessToken);

				mAccessToken = accessToken;
				authenticateUser();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean getIsUserAuthenticated() {
		return mAuthenticated;
	}

	public String getToken() {
		return mAccessToken;
	}

	private void authenticateUser() throws URISyntaxException, HttpException {
		try {
			HttpPost httpPostLogin = new HttpPost(Constants.BASE_URL_LOGIN);
			httpPostLogin.setHeader("Content-type", "application/json");
			httpPostLogin.setHeader("Authorization",
					getAuthorizationBearerHttpHeader());
			httpPostLogin.setEntity(new StringEntity(getHttpEntity()));

			HttpResponse responseLogin = httpclient.execute(httpPostLogin);
			StatusLine statusLine = responseLogin.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				JsonObject jsonComplete = parser.parse(
						getStringFromHttpResponse(responseLogin))
						.getAsJsonObject();

				// This is to prevent the application from crashing when the
				// info returned
				// doesn't contain patient or organization id
				if (jsonComplete.get("user") != null
						&& jsonComplete.get("user").getAsJsonObject()
								.get("fhir_patient_id") != null
						&& jsonComplete.get("user").getAsJsonObject()
								.get("fhir_organization_id") != null) {
					mPatientUrlId = jsonComplete.get("user")
							.getAsJsonObject().get("fhir_patient_id")
							.getAsString();
					mOrganizationUrlId = jsonComplete.get("user")
							.getAsJsonObject().get("fhir_organization_id")
							.getAsString();
					pictureUrlString = jsonComplete.get("user")
							.getAsJsonObject().get("picture").getAsString();

					mAuthenticated = true;
				} else {
					mAuthenticated = false;
				}

			} else {
				mAuthenticated = false;
			}
		} catch (IOException e) {
			mAuthenticated = false;
			e.printStackTrace();
		}

	}

	public String getPatientID() {
		return mPatientUrlId;
	}
	
	public boolean logout() throws URISyntaxException, HttpException {
		HttpDelete httpPostToken = new HttpDelete(Constants.BASE_URL_LOGOUT);
		httpPostToken.setHeader("Authorization",
				getAuthorizationBearerHttpHeader());

		boolean logoutCompleted = false;
		try {
			HttpResponse responseToken = httpclient.execute(httpPostToken);
			StatusLine statusLine = responseToken.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK)
				logoutCompleted = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		mAuthenticated = false;
		mAccessToken = null;
		return logoutCompleted;
	}

	public String getStringFromHttpResponse(HttpResponse inResponse) {
		String responseString = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			inResponse.getEntity().writeTo(out);
			out.close();
			responseString = out.toString();

			// Log.i("HS", responseString);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseString;
	}

	public String getAuthorizationBearerHttpHeader() {
		return "Bearer " + mAccessToken;
	}

	public String getHttpEntity() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("username", Constants.USER_ID);
		jsonObject.addProperty("password", Constants.USER_SECRET);
		return jsonObject.toString();
	}

	public String getAccessToken() {
		return mAccessToken;
	}

}
