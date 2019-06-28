package com.remco.ico.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.remco.ico.model.PromoUsersInfo;

public interface PromoUsersInfoRepository extends JpaRepository<PromoUsersInfo, Integer>{
	
	public PromoUsersInfo findByEmailIdIgnoreCase(String emailId);
	
	public PromoUsersInfo findByEmailIdIgnoreCaseAndPromoType(String emailId, String promoType);
	
	@Query("select r from PromoUsersInfo r where (upper(r.firstName) like concat('%', upper(?1), '%') or upper(r.lastName) like concat('%', upper(?2), '%') or upper(r.emailId) like concat('%', upper(?3), '%'))")
	Page<PromoUsersInfo> getByNames(String firstName, String lastName, String emailId, Pageable pageable);

	@Query("select r from PromoUsersInfo r where (upper(r.firstName) like concat('%', upper(?1), '%') or upper(r.lastName) like concat('%', upper(?2), '%') or upper(r.emailId) like concat('%', upper(?3), '%')) and (r.promoType = ?4)")
	Page<PromoUsersInfo> getByNamesAndPromoType(String firstName, String lastName, String emailId, String promoType, Pageable pageable);
	
	Page<PromoUsersInfo> findByPromoType(String promoType, Pageable pageable);
	
	public PromoUsersInfo findByMobileNoAndPromoType(String mobileNo, String promoType);

}
