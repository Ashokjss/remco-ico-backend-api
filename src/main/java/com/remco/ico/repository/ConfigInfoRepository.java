package com.remco.ico.repository;

import org.springframework.data.repository.CrudRepository;
import com.remco.ico.model.Config;

public interface ConfigInfoRepository extends CrudRepository<Config, Integer>{
	
	public Config findConfigByConfigKey(String configKey);

}
