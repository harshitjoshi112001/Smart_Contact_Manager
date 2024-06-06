package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int uid;
	
	@NotBlank(message ="Name field is required !!")
	@Size(min = 2,max = 20,message="min 2 and max 20 Characters is allowed !!")
	private String name;
	
	@Column(unique = true)
	@jakarta.validation.constraints.Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
	private String email;
	
	@NotBlank(message ="Password field is required !!")
//	@Size(min = 8,max = 20,message="min 8 and max 20 Characters is allowed !!")
	private String password;    //^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*]).{8,}$
	private String role;
	private boolean enabled;
	
	private String imageUrl;
	@Column(length = 500)
	@NotBlank(message ="About field is required !!")
	private String about;
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "user")
	private List<Contacts> contacts = new ArrayList<>();
}
