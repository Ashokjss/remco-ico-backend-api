package com.remco.ico.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.remco.ico.model.RegisterInfo;

public interface RegisterInfoRepository extends JpaRepository<RegisterInfo, Integer>{
	
	public Integer countHealthycoinUserInfoByEmailIdIgnoreCase(String trim);
	
	public RegisterInfo findRegisterInfoByEmailIdIgnoreCase(String emailid);
	
	public List<RegisterInfo> findRegisterInfoByUserType(int userType);
	
	public RegisterInfo findRegisterInfoById(Integer id);
	
	public RegisterInfo findRemcoUserInfoByEmailIdAndPassword(String trim, String password);
	
	public Page<RegisterInfo> findRegisterInfoByUserTypeAndActive(Integer userType, Pageable pageable, Integer active);
	
	public Page<RegisterInfo> findRegisterInfoByUserType(Integer userType, Pageable pageable);
	
	public Integer countRegisterInfoUserCountByUserType(Integer userType);
	
	public List<RegisterInfo> findByMobileNo(String mobileNo);
	
	@Query("select r from RegisterInfo r where r.userType=0 and (upper(r.firstName) like concat('%', upper(?1), '%') or upper(r.lastName) like concat('%', upper(?2), '%') or upper(r.emailId) like concat('%', upper(?3), '%') or upper(r.mobileNo) like concat('%', upper(?4), '%')) and (r.active = ?5 or r.active = ?6 or r.active = ?7)")
	Page<RegisterInfo> getByNames(String firstName, String lastName, String emailId, String mobileNo, Integer active , Integer pending, Integer all, Pageable pageable);
	
	@Query("select r from RegisterInfo r where r.userType=0 and (upper(r.firstName) like concat('%', upper(?1), '%') or upper(r.lastName) like concat('%', upper(?2), '%') or upper(r.emailId) like concat('%', upper(?3), '%') or upper(r.mobileNo) like concat('%', upper(?4), '%')) and (r.active = ?5 or r.active = ?6 or r.active = ?7)")
	List<RegisterInfo> filterByNamesOrEmailOrMobileNo(String firstName, String lastName, String emailId, String mobileNo, Integer active , Integer pending, Integer all);

	public List<RegisterInfo> findByEmailStatus(int emailStatus);
	
}

