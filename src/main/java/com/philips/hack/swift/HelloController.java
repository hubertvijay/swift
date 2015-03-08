package com.philips.hack.swift;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@RequestMapping("/hello")
	public @ResponseBody String hello() {
		System.out.println("Hitting the Hello Controller");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return "Hello buddy, Now the time is " + dateFormat.format(date);
	}
	
	@RequestMapping("/login")
	public @ResponseBody String login() {
		System.out.println("Hitting the AUTH");
		User user = null;
		try {
			user = new User();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "token:"+user.getToken();
	}
	
	@RequestMapping("/logout")
	public @ResponseBody boolean logout() throws UnsupportedEncodingException, URISyntaxException, HttpException {
		User user = new User();
		return user.logout();
	}
	
	@RequestMapping("/patientid")
	public @ResponseBody String patientid() throws UnsupportedEncodingException, URISyntaxException, HttpException {
		User user = new User();
		return user.getPatientID();
	}
}
