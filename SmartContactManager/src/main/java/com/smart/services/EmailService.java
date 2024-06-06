package com.smart.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;


@Service
public class EmailService {
	
	public boolean sendEmail(String subject,String message,String to)
	{
		boolean f=false;
		String from = "happybank202@gmail.com";
		
		//variable host
		String host = "smtp.gmail.com";
		
		//get the system properties
		Properties properties = System.getProperties();
		System.out.println("Properties = "+properties);
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		//step:1 to get the session object
		Session session = Session.getInstance(properties , new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication("happybank202@gmail.com", "asarbwlsagggcnkp");
			}
			
		}); 
		session.setDebug(true);
		//step 2:compose the message
		
		MimeMessage m = new MimeMessage(session);
		
		try {
			//from email
			m.setFrom(from);
			
			//adding Recipient
			m.addRecipient(Message.RecipientType.TO	, new InternetAddress(to));
			
			//adding subject to message
			m.setSubject(subject);
			
			//adding text to message
//			m.setText(message);
			m.setContent(message, "text/html");
			
			//send the message to transport class
			Transport.send(m);
			
			System.out.println("Sent success.......");
			f = true;
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return f;
	}
}
