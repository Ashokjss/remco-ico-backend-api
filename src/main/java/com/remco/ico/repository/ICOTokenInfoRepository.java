package com.remco.ico.repository;

import org.springframework.data.repository.CrudRepository;
import com.remco.ico.model.ICOTokenInfo;

public interface ICOTokenInfoRepository extends CrudRepository<ICOTokenInfo, Integer>{
	
	public ICOTokenInfo findIcoTokenInfoByIcoKey(String key);
}
