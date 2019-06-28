package com.remco.ico.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import com.remco.ico.model.TokenDiscountInfo;

public interface TokenDiscountInfoRepository  extends CrudRepository<TokenDiscountInfo, Serializable> {
	
	public TokenDiscountInfo findByTokenName(String tokenName);
	

}
