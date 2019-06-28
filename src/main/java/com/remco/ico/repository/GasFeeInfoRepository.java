package com.remco.ico.repository;

import org.springframework.data.repository.CrudRepository;

import com.remco.ico.model.GasFeeInfo;

public interface GasFeeInfoRepository extends CrudRepository<GasFeeInfo, Integer>{

	public GasFeeInfo findGasFeeInfoById(Integer id);
	
}
