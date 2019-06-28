package com.remco.ico.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.remco.ico.model.ReferralInfo;

public interface ReferralInfoRepository extends JpaRepository<ReferralInfo, Integer>{

	public ReferralInfo findByEmailId(String emailId);
	
	public List<ReferralInfo> findByStatus(Integer Status);
	
	public List<ReferralInfo> findReferralInfoByReferralEmailId(String emailId);
	
	public Page<ReferralInfo> findReferralInfoByReferralEmailIdAndStatus(String emailId,Integer status, Pageable pageable);
	
	public Page<ReferralInfo> findReferralInfoByReferralEmailId(String emailId, Pageable pageable);
	
}
