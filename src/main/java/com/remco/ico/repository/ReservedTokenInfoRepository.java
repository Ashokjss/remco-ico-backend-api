package com.remco.ico.repository;

import org.springframework.data.repository.CrudRepository;

import com.remco.ico.model.ReservedTokenInfo;


public interface ReservedTokenInfoRepository extends CrudRepository<ReservedTokenInfo, Integer>{
	
	public ReservedTokenInfo findReservedTokenInfoByReserveKey(String key);


}
	
