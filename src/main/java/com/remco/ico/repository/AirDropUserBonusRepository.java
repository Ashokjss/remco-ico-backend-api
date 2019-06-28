package com.remco.ico.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.remco.ico.model.AirDropUserBonus;

public interface AirDropUserBonusRepository extends CrudRepository<AirDropUserBonus, Integer>{
	
	@Query("select r from AirDropUserBonus r where (upper(r.emailId) like concat('%', upper(?1), '%') or upper(r.fullName) like concat('%', upper(?2), '%')) and ( r.tokenTransferStatus = ?3 )")
	Page<AirDropUserBonus> getByNames(String emailId,String name, Integer tokenTransferStatus, Pageable pageable);
	
	@Query("select r from AirDropUserBonus r where (upper(r.emailId) like concat('%', upper(?1), '%') or upper(r.fullName) like concat('%', upper(?2), '%')) and ( r.tokenTransferStatus = ?3 ) and (r.allocationType = ?4)")
	Page<AirDropUserBonus> getByNamesAndAllocation(String emailId,String name, Integer tokenTransferStatus, String allocationType, Pageable pageable);
	
	public Page<AirDropUserBonus> findByTokenTransferStatus(Integer tokenTransferStatus, Pageable pageable);
	
	public Page<AirDropUserBonus> findByTokenTransferStatusAndAllocationType(Integer tokenTransferStatus, String allocationType, Pageable pageable);
	
	@Query("SELECT SUM(m.earnedToken) FROM AirDropUserBonus m where m.tokenTransferStatus = 1")
	public Double getTheTotalTransferredTokenCount();
	
}
