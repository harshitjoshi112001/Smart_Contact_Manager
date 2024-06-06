package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.entities.Contacts;
import com.smart.entities.Users;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@RestController
public class SearchController {

	@Autowired
	private ContactRepository contactRepo;
	@Autowired
	private UserRepository UserRepo;
	
	//search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query ,Principal principal)
	{
		System.out.println(query);
		Users user = this.UserRepo.getUserbyUserName(principal.getName());
		List<Contacts> list = this.contactRepo.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(list);
	}
}
