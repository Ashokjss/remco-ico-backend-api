package com.remco.ico.repository;

import org.springframework.data.repository.CrudRepository;

import com.remco.ico.model.SaleDatesInfo;

public interface SaleDatesInfoRepository extends CrudRepository<SaleDatesInfo, Integer>{

	public SaleDatesInfo findById(Integer id);
	
}
