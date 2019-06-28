package com.remco.ico.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.remco.ico.model.PurchaseTokenInfo;

public interface PurchaseTokenInfoRepository extends JpaRepository<PurchaseTokenInfo, Integer> {

	public PurchaseTokenInfo findByEmailIdAndTypeOfPurchase(String emailId, Integer typeOfPurchase);
	
	public List<PurchaseTokenInfo> findByTransferTypeOrTransferTypeOrTransferTypeOrTransferType(Integer type1 , Integer type2, Integer type3, Integer type4);
	
	public List<PurchaseTokenInfo> findPurchaseTokenInfoByEtherWalletAddress(String etherWalletAddress);
	
	public Page<PurchaseTokenInfo> findPurchaseTokenInfoByEtherWalletAddress(String etherWalletAddress, Pageable pageable);
	
	public List<PurchaseTokenInfo> findVestingListByEtherWalletAddressAndIsVestingAndTransferType(String walletAddress, Integer isVesting, Integer transferType);
		
	public PurchaseTokenInfo findById(Integer id);
	
		
	@Query("select r from PurchaseTokenInfo r where r.transferType = ?1 and r.purchasedDate between ?2 and ?3 and etherWalletAddress = ?4")
	Page<PurchaseTokenInfo> getPurchaseList(Integer transType, Date startDate, Date endDate, String etherWallet, Pageable pageable);
	
	@Query("select r from PurchaseTokenInfo r where r.purchasedDate between ?1 and ?2 and etherWalletAddress = ?3")
	Page<PurchaseTokenInfo> getPurchaseList(Date startDate, Date endDate, String etherWallet, Pageable pageable);
	
	@Query("select r from PurchaseTokenInfo r where r.transferType = ?1 and r.purchasedDate between ?2 and ?3")
	Page<PurchaseTokenInfo> getPurchaseListAdmin(Integer transType, Date startDate, Date endDate, Pageable pageable);
	
	@Query("select r from PurchaseTokenInfo r where r.purchasedDate between ?1 and ?2")
	Page<PurchaseTokenInfo> getPurchaseListAdmin(Date startDate, Date endDate, Pageable pageable);
	
	@Query("select r from PurchaseTokenInfo r where r.transferType = ?1 and r.purchasedDate between ?2 and ?3 and upper(r.emailId) like concat('%', upper(?4), '%')")
	Page<PurchaseTokenInfo> getPurchaseListAdmin(Integer transType, Date startDate, Date endDate, String keyword, Pageable pageable);
	
	@Query("select r from PurchaseTokenInfo r where r.purchasedDate between ?1 and ?2 and upper(r.emailId) like concat('%', upper(?3), '%')")
	Page<PurchaseTokenInfo> getPurchaseListAdmin(Date startDate, Date endDate,String keyword, Pageable pageable);
	
	public List<PurchaseTokenInfo> findByEmailIdAndAsynchStatus(String emailId, String status);
	
	@Query("SELECT SUM(m.purchaseTokens) FROM PurchaseTokenInfo m where m.transferType = 0")
	public Double getpurchasedTokenTransferCount();
	
}
