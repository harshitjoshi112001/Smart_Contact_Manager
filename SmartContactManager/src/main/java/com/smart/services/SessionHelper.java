package com.smart.services;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {
	
	public void removeSession()
	{
		try {
			System.out.println("Removing session....");
			HttpSession http = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
			http.removeAttribute("message");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
