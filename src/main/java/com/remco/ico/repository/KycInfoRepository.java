package com.remco.ico.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.remco.ico.model.KycInfo;

public interface KycInfoRepository extends JpaRepository<KycInfo, Integer>{
	
	public KycInfo findKycInfoByEmailIdIgnoreCase(String emailid);
	
	public KycInfo findKycInfoById(Integer id);
	
	public Page<KycInfo> findRegisterInfoByKycStatus(String kycStatus, Pageable pageable);
	
	@Query("select r from KycInfo r where (upper(r.firstName) like concat('%', upper(?1), '%') or upper(r.lastName) like concat('%', upper(?2), '%') or upper(r.middleName) like concat('%', upper(?3), '%') or upper(r.emailId) like concat('%', upper(?4), '%')) and (r.kycStatus = ?5 or r.kycStatus = ?6 or r.kycStatus = ?7)")
	Page<KycInfo> getByNames(String firstName, String lastName, String midname, String emailId , String pending, String approved,String rejected, Pageable pageable);

}
