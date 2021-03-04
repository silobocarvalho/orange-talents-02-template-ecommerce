package br.com.zup.orange.util;

public class Email {

	public static void sendEmail(String from, String to, String message) {
			//Here you send the email...
		System.out.println("---- SEND EMAIL SERVICE ----");
		System.out.println("---- FROM:" + from + " ----");
		System.out.println("---- TO:" + to + " ----");
		System.out.println("---- " + message + " ----");
	}
	
}
