package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contacts;
import com.smart.entities.Users;
import com.smart.helper.Message;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private PasswordEncoder pass;	
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContactRepository contactRepo;
	
	//add common data user
	@ModelAttribute
	public void addCommonData(Model model,Principal principal)
	{
		String email = principal.getName();   //give email id
		Users user = this.userRepo.getUserbyUserName(email);
		
		model.addAttribute("user",user);
	}
	
	//home dashboard
	@RequestMapping("/index")
	public String dashboard(Model model,HttpSession session)
	{
		model.addAttribute("title","User Dashboard");
//		if(session.getAttribute("message") != null) {
//			session.removeAttribute("message");
//		}
		return "normal/user_dashboard";
	}
	
	//open add contact
	@RequestMapping("/add-contact")
	public String addContactForm(Model model,HttpSession session)
	{
		model.addAttribute("title","Add Contacts");
		model.addAttribute("contact",new Contacts());
//		if(session.getAttribute("message") != null) {
//			session.removeAttribute("message");
//		}
		return "normal/add_contact_form";
	}
	
	//process contact
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contacts contact,
			@RequestParam("profileImage") MultipartFile file, 
			Principal principal ,HttpSession session)
	{
		try {
			String email = principal.getName();   //give email id
			Users user = this.userRepo.getUserbyUserName(email);
			
			
			//processing image and save to database
			if(file.isEmpty()) {
				//Is empty
				System.out.println("Image is Empty..");
				contact.setImageUrl("contact.jpeg");
			}
			else {
				contact.setImageUrl(file.getOriginalFilename());
				
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is successfully uploaded ..");
				
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			
			this.userRepo.save(user);
			
			session.setAttribute("message", new Message("Successfully added the data..Add more!!","success"));
//			System.out.println(session.getAttribute("message"));
//			System.out.println("Contact details "+contact.toString());
			System.out.println("Added to database....");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Errors -> "+e.getMessage());
			session.setAttribute("message", new Message("Contact not added..Try again..!!","danger"));
		}
		return "normal/add_contact_form";
	}
	
	//show contacts
	//per page 5 contacts
	//current page = 0page
	@RequestMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page")Integer page ,Model model,Principal principal)
	{
		model.addAttribute("title","View Contacts");
		Users user = this.userRepo.getUserbyUserName(principal.getName());
		
		//current page
		//contact per page-5
		Pageable pageable =PageRequest.of(page, 3);
		Page<Contacts> contact= this.contactRepo.getContactsByUserId(user.getUid(),pageable);
//		System.out.println("contacts -> "+contact);
		model.addAttribute("contact",contact);
		model.addAttribute("currentPage",page);   //current page show
		
		model.addAttribute("totalPages", contact.getTotalPages());
//		System.out.println("user-> "+user);
		
		return "normal/show_contacts";
	}
	
	
	//show particular contact details
	@RequestMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") int id,Model model,Principal principal)
	{
//		System.out.println("cid -> "+id);
		Optional<Contacts> contactOptional = this.contactRepo.findById(id);
		if(contactOptional.isPresent())
		{
			Contacts contact = contactOptional.get();
			
			String email = principal.getName();
			Users user = this.userRepo.getUserbyUserName(email);
			
			//apply check
			if(user.getUid() == contact.getUser().getUid())
			{
				model.addAttribute("contact",contact);
				model.addAttribute("title",contact.getName());
			}
			else
			{
				model.addAttribute("title", "Wrong User");
			}
		}
		else
		{
			model.addAttribute("title", "Wrong User");
		}
		return "normal/contact_details";
	}
	
	//delete contact
	@RequestMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") int id,Model model,
			Principal principal,
			HttpSession session)
	{
		Optional<Contacts> con = this.contactRepo.findById(id);
		if(con.isPresent())
		{
			Contacts contact = con.get();
			//if contact not getting deleted so we do ....contact.setUser(null);
			String email = principal.getName();
			Users user = this.userRepo.getUserbyUserName(email);
			
			String img = contact.getImageUrl();
			File path = new File("/img/"+img);
			
			//apply check
			if(user.getUid() == contact.getUser().getUid())
			{
				if(img.equals("contact.jpeg") ==false)
				{
					boolean b =path.delete();
					System.out.println("delete "+b);
				}
				this.contactRepo.delete(contact);
				session.setAttribute("message", new Message("Successfully deleted the contact ...!!","success"));
			}
			else
			{
				model.addAttribute("title", "Wrong User");
				session.setAttribute("message", new Message("Sorry ..!! You can't delete another Users contact ...!!","danger"));
			}
		}
		else
		{
			model.addAttribute("title", "Wrong User");
			session.setAttribute("message", new Message("Sorry ..!! You can't delete another Users contact ...!!","danger"));
		}
		
		return "redirect:/user/show-contacts/0";
	}
	
	//update the contact
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") int id,Model model) 
	{
		model.addAttribute("title","Update Contact");
		
		Contacts contact = this.contactRepo.findById(id).get();
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}
	
	//update handler
	@RequestMapping(value = "/process-update" , method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contacts contact,@RequestParam("profileImage")MultipartFile file,
			Model model,HttpSession session,Principal principal)
	{
		try {
			Contacts oldDetails = this.contactRepo.findById(contact.getCid()).get();
				if(!file.isEmpty())
				{
					//delete the pic
					File saveFile1 = new ClassPathResource("static/img").getFile();
					File newfile = new File(saveFile1 , oldDetails.getImageUrl());
					newfile.delete();
					
					//update new pic
					File saveFile = new ClassPathResource("static/img").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					
					contact.setImageUrl(file.getOriginalFilename());
				}
				else
				{
					contact.setImageUrl(oldDetails.getImageUrl());
				}
				Users user = this.userRepo.getUserbyUserName(principal.getName());
				contact.setUser(user);
				this.contactRepo.save(contact);
				
				session.setAttribute("message", new Message("Your contact is updated....","success"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	
	//your profile
	@GetMapping("/profile")
	public String getProfile(Model model)
	{
		model.addAttribute("title","Profile Page");
		return "normal/profile";
	}
	
	//delete user
	
	@RequestMapping("/deleteUser/{uid}")
	public String deleteUser(@PathVariable("uid") int id,Model model,
			Principal principal,
			HttpSession session)
	{
		Optional<Users> user = this.userRepo.findById(id);
		if(user.isPresent())
		{
			String email = principal.getName();
			Users users = this.userRepo.getUserbyUserName(email);
			
			//apply check
			if(user.get().getUid() == users.getUid())
			{
//				if(img.equals("contact.jpeg") ==false)
//				{
//					boolean b =path.delete();
//					System.out.println("delete "+b);
//				}
				this.userRepo.delete(users);
//				session.setAttribute("message", new Message("Successfully deleted the User ...!!","success"));
			}
			else
			{
				model.addAttribute("title", "Wrong User");
				session.setAttribute("message", new Message("Sorry ..!! You can't delete another Users ...!!","danger"));
			}
		}
		else
		{
			model.addAttribute("title", "Wrong User");
			session.setAttribute("message", new Message("Sorry ..!! You can't delete another Users ...!!","danger"));
		}
		return "redirect:/signin";
	}
	
	//open setting
	@GetMapping("/settings")
	public String openSettings(Model model)
	{
		model.addAttribute("title", "User Setting");
		return "normal/settings";
	}
	
	//change password
	@PostMapping("/changepassword")
	public String changePassword(@RequestParam("oldPassword") String old,@RequestParam("newPassword") String newPass
			,Principal principal,HttpSession session)
	{
//		System.out.println("old pass "+old);
//		System.out.println("new pass "+newPass);
		
		Users user = this.userRepo.getUserbyUserName(principal.getName());
		
		if(this.pass.matches(old, user.getPassword()))  //check the bcrypt pass
		{
			//change the pass
			user.setPassword(this.pass.encode(newPass));
			this.userRepo.save(user);
			session.setAttribute("message", new Message("Your Password is successfully changed...","success"));
		}
		else
		{
			session.setAttribute("message", new Message("Your Old Password is incorrect...","warning"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}
}
