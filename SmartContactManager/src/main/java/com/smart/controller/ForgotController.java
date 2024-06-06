package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.Users;
import com.smart.repository.UserRepository;
import com.smart.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	//email id from open handler
	Random num = new Random(1000);
	
	@Autowired
	private EmailService emailSer;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder pass;	
	
	@RequestMapping("/forgot")
	public String openEmailForm(Model model)
	{
		model.addAttribute("title", "Email Form");
		return "forgot_email_form";
	}
	
	//otp generate
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,Model model,HttpSession session)
	{
		System.out.println("Email -> "+email);
		//gereate otp
		int otp = num.nextInt(99999);
		System.out.println("random -> "+otp);
		model.addAttribute("title", "Verify OTP");
		
		//code to send otp
		String subject = "OTP from SCM";
		String message = "<div style='border:1px solid #e2e2e2;padding:20px;'>"
				+ "<h1>OTP is "
				+ "<b>"+otp
				+ "</n>"
				+ "</h1>"
				+ "</div>";
		String to = email;
		
		boolean  b=this.emailSer.sendEmail(subject, message, to);
		
		if(b) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verfiy_otp";
		}else {
			session.setAttribute("message", "Check your email_Id!!");
			return "forgot_email_form";
		}
	}
	
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp")int otp,HttpSession session)
	{
		int myotp = (int)session.getAttribute("myotp");
		String email =(String)session.getAttribute("email");
		
		if(myotp == otp) {
			
			Users user = this.userRepo.getUserbyUserName(email);
			if(user==null) {
				//send error message
				session.setAttribute("message", "User does not exist with this email !!!!");
				return "forgot_email_form";
			}else {
				
			}
			return "password_change_form";
		}else {
			session.setAttribute("message", "You Have entered wrong otp...");
			return "verfiy_otp";
		}
		
	}
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword")String newpass,HttpSession session)
	{
		String email =(String)session.getAttribute("email");
		Users user = this.userRepo.getUserbyUserName(email);
		user.setPassword(this.pass.encode(newpass));
		
		this.userRepo.save(user);
		
		return "redirect:/signin?change=Password change successfully ..!!";
	}
}
