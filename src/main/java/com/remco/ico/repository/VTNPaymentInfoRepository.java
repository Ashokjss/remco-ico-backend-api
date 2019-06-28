package com.remco.ico.repository;

import org.springframework.data.repository.CrudRepository;

import com.remco.ico.model.VTNPaymentInfo;

public interface VTNPaymentInfoRepository extends CrudRepository<VTNPaymentInfo, Integer>{

	public VTNPaymentInfo findVTNPaymentInfoByIpnRefno(String ipnRefno);
	
}
