package com.remco.ico.repository;

import org.springframework.data.repository.CrudRepository;

import com.remco.ico.model.AuthorizationInfo;

public interface AuthorizationInfoRepository extends CrudRepository<AuthorizationInfo, Integer> {

	public AuthorizationInfo findByAuthToken(String authToken);
	
	public AuthorizationInfo findByEmailIdIgnoreCase(String emailId);
		
}
