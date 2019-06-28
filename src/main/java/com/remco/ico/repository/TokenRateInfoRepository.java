package com.remco.ico.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.remco.ico.model.TokenRateInfo;

public interface TokenRateInfoRepository extends CrudRepository<TokenRateInfo, Serializable> {
	
	public TokenRateInfo findById(Integer id);
	
	public List<TokenRateInfo> findBySaletypeOrSaletypeOrSaletype(String privateSal, String prePublicSal, String publicSal);
	
	public TokenRateInfo findBySaletype(String saleType);
	
}
