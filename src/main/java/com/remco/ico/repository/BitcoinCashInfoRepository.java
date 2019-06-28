package com.remco.ico.repository;

import org.springframework.data.repository.CrudRepository;

import com.remco.ico.model.BitcoinCashInfo;

public interface BitcoinCashInfoRepository extends CrudRepository<BitcoinCashInfo, Integer> {

	public BitcoinCashInfo findByEmailId(String emailId);
	
}
