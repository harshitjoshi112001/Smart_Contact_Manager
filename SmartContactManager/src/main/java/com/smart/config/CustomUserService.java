package com.smart.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smart.entities.Users;
import com.smart.repository.UserRepository;

@Service
public class CustomUserService implements UserDetailsService{

	@Autowired
	private UserRepository userRepo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Optional<Users> user = this.userRepo.findByEmail(username);
		if(user.isEmpty())
		{
			throw new UsernameNotFoundException("Could not found User !!!!");
		}
		CustomUserDetails custom = new CustomUserDetails(user.get());
		return custom;
	}

}
