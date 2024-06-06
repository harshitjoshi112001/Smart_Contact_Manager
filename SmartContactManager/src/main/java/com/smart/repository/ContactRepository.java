package com.smart.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contacts;
import com.smart.entities.Users;

public interface ContactRepository extends JpaRepository<Contacts, Integer>{
	
//	@Query(value="select * from contacts where user_Uid=?1", nativeQuery = true)
	//current page
	//contact per page-5
	@Query("select c from Contacts as c WHERE c.user.uid =:id")
	public Page<Contacts> getContactsByUserId(@Param("id")int id,Pageable page);
	
	//search
	public List<Contacts> findByNameContainingAndUser(String name,Users user);
}
