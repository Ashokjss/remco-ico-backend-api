package com.remco.ico.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.remco.ico.model.EmailContentInfo;

public interface EmailContentInfoRepo extends CrudRepository<EmailContentInfo, Integer> {

	public List<EmailContentInfo> findByStatus(int status);
	
}
