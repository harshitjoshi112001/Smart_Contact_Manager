package com.smart.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contacts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int cid;
	
	private String name;
	
	private String nickname;
	
	private String email;
	
	@Column(length = 5000)
	private String description;
	
	private String work;
	
	private String imageUrl;
	
	private String phone;
	
	@ManyToOne
	@JsonIgnore
	private Users user;
}
