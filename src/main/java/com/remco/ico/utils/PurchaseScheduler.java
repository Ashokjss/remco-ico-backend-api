package com.remco.ico.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.remco.ico.service.AdminService;
import com.remco.ico.solidityHandler.SolidityHandler;

@Service
public class PurchaseScheduler {
	
	private static final Logger LOG = LoggerFactory.getLogger(PurchaseScheduler.class);

	@Autowired
	private SolidityHandler solidityHandler;
	@Autowired
	private AdminService adminService;
	
	//@Scheduled(cron = "0 0 1 ? * *")
	public void cronTask() throws Exception
	{
		solidityHandler.transferTokensForPurchase();
		LOG.info("Entered into the cron");
	}

	@Scheduled(cron = "0 0/15 * * * ?")
	public void emailTrigger() throws Exception {
		adminService.triggerEmail();
		LOG.info("Entered into the cron");
		
	}
	
}
