package com.remco.ico.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import com.remco.ico.model.UserReferralInfo;

public interface UserReferralInfoRepository extends CrudRepository<UserReferralInfo, Serializable>{
	
	public UserReferralInfo findByNewuserEmail(String newUserEmail);
	
	public UserReferralInfo findByUserEmail(String userEmail);
	
	public UserReferralInfo findByUserEmailAndNewuserEmail(String userEmail, String newUserEmail);
	
	
	
}
