package com.remco.ico.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.remco.ico.model.TransactionHistory;

public interface TransactionInfoRepository extends JpaRepository<TransactionHistory, Integer>{
	
	public List<TransactionHistory> findByFromAddressOrToAddressOrderByTransferDateDesc(String walletAddress, String walletAddress2);
	
	public List<TransactionHistory> findByStatusAndPaymentMode(String status, String mode);
	
	public List<TransactionHistory> findByStatus(String status);
	
	public List<TransactionHistory> findByPaymentMode(String paymentMode);
	
	public TransactionHistory findTxInfoById(Integer id);
	
	public Page<TransactionHistory> findByFromAddressOrToAddressOrderByTransferDateDesc(String walletAddress,
			String walletAddress2,Pageable pg);

}
