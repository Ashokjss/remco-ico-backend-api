package com.remco.ico.repository;

import org.springframework.data.repository.CrudRepository;

import com.remco.ico.model.BitcoinInfo;

public interface BitcoinInfoRepository extends CrudRepository<BitcoinInfo, Integer> {

	public BitcoinInfo findByEmailId(String emialId);
	
}
