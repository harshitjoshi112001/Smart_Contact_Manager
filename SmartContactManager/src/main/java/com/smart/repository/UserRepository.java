package com.smart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Users;

public interface UserRepository extends JpaRepository<Users, Integer>{

	Optional<Users> findByEmail(String email);
	
	@Query(value = "select * from users where email=?1" ,nativeQuery = true)
	public Users getUserbyUserName(String email);
}
