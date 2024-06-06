package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig{
		
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
//	@Bean
//	public UserDetailsService userDetailsService()
//	{
//		UserDetails normal = User
//				.withUsername("harshit")
//				.password(this.passwordEncoder().encode("pass"))
//				.roles("USER")
//				.build();
//		UserDetails admin = User
//				.withUsername("admin")
//				.password(this.passwordEncoder().encode("admin123"))
//				.roles("ADMIN")
//				.build();
//		
//		return new InMemoryUserDetailsManager(normal,admin);
//	}
//	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http.csrf().disable()
		.authorizeHttpRequests()
		.requestMatchers("/admin/**").hasRole("ADMIN")
		.requestMatchers("/user/**").hasRole("USER")
		.requestMatchers("/**")
		.permitAll()
//		.anyRequest()
//		.authenticated()
		.and()
		.formLogin().loginPage("/signin")
		.loginProcessingUrl("/dologin")
		.defaultSuccessUrl("/user/index");
//		.failureUrl("/login-fail");
		
		return http.build();
	}
	
	@Bean
	public AuthenticationManager auth(AuthenticationConfiguration configuration) throws Exception
	{
		return configuration.getAuthenticationManager();
	}
}
