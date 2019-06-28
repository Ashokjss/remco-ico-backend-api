package com.remco.ico.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;

import com.remco.ico.DTO.KycDTO;
import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.UserRegisterDTO;

@Service
public interface AdminService {
	
	public boolean validateAdmin(String emailId);
	
	public List<KycDTO> kycList(KycDTO kycDTO);
	
	public KycDTO getKycDetails(KycDTO kycDTO);

	public String validateRequestStatus(KycDTO kycDTO); 
	
	public boolean updateKYC(KycDTO kycDTO);

	public boolean tokenPurchase(TokenDTO tokenDTO) throws IOException, CipherException, Exception;
	
	public boolean tokenPurchaseUsingVTN(TokenDTO tokenDTO) throws Exception;
	
	public String validateTokenBalanceForPurchase(TokenDTO tokenDTO) throws Exception;
	
	public List<TokenDTO> purchaseLists(TokenDTO tokenDTO) throws Exception;
	
	public List<TokenDTO> referralPurchaseList(TokenDTO tokenDTO);

	public boolean addUsersToEmailList(UserRegisterDTO userRegisterDTO, TokenDTO tokenDTO);

	public boolean triggerEmail();
	
}
