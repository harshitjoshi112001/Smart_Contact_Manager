package com.smart.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.Users;
import com.smart.helper.Message;
import com.smart.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder pass;
	
	@RequestMapping("/")
	public String getHome(Model model)
	{
		model.addAttribute("title","Home - Smart contact manager");
		return "Home";
	}
	
	@RequestMapping("/about")
	public String getAbout(Model model)
	{
		model.addAttribute("title","About - Smart contact manager");
		return "About";
	}
	
	@RequestMapping("/signup")
	public String getSignup(Model model,HttpSession session)
	{
		model.addAttribute("title","Register - Smart contact manager");
		model.addAttribute("user",new Users());
//		if(session.getAttribute("message") != null) {
//			session.removeAttribute("message");			
//		}
		return "Signup";
	}
	
	@RequestMapping(value="/do_register" , method = RequestMethod.POST)     //user , checkbox , model  ,session
	public String registerUser(@Valid @ModelAttribute("user") Users user,BindingResult result1,@RequestParam(value = "agreement" ,defaultValue = "false") boolean agreement,Model model ,HttpSession session)
	{
//		System.out.println("Agreement "+agreement);
//		System.out.println("USERS "+user.toString());
		try {
			if(result1.hasErrors())
			{
				System.out.println("ERROR "+result1.toString());
				model.addAttribute("user",user);
				return "Signup";
			}
			if(!agreement)
			{
				System.out.println("ERRORs "+result1.toString());
				throw new Exception("You have not agreed terms and conditions !!!");
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(this.pass.encode(user.getPassword()));
			
			Optional<Users> users = this.userRepo.findByEmail(user.getEmail());
			if(users.isPresent())
			{
				throw new Exception("Email is already registered by other user !!!!");
			}
			this.userRepo.save(user);
			model.addAttribute("user",new Users());   //set model attribute
			session.setAttribute("message", new Message("Successfully Register ...! ","alert-success"));  //show message
			
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something Went Wrong !! "+e.getMessage(),"alert-danger"));
			session.removeAttribute("");
		}
		return "Signup";   //after submitting again open signup page....
	}
	
	@RequestMapping("/signin")
	public String getLogin(Model model)
	{
		model.addAttribute("title","Login Page");
		return "Login";
	}
}
