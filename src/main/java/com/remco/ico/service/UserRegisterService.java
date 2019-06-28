package com.remco.ico.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.remco.ico.DTO.KycDTO;
import com.remco.ico.DTO.LoginDTO;
import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.UserRegisterDTO;

@Service
public interface UserRegisterService {
	
	public boolean isAccountExistCheckByEmailId(String lowerCase);
	
	public boolean saveAccountInfo(UserRegisterDTO userRegisterDTO) throws Exception;
	
	public boolean isAccountExistCheckByMobileNo(String lowerCase);

	boolean isWalletCreate(UserRegisterDTO userRegisterDTO);

	boolean activateUser(LoginDTO loginDTO) throws Exception;
	
	public LoginDTO isEmailAndPasswordExit(UserRegisterDTO userRegisterDTO) throws Exception;
	
	public LoginDTO loginSecure(UserRegisterDTO userRegisterDTO)throws Exception;
	
	public boolean isEmailIdExist(UserRegisterDTO userRegisterDTO, HttpServletRequest request) throws Exception;
	
	public boolean saveNewPasswordInfo(UserRegisterDTO userRegisterDTO) throws Exception;
	
	public boolean isOldPassword(UserRegisterDTO userRegisterDTO) throws Exception;

	public boolean isChangePassword(UserRegisterDTO userRegisterDTO);
	
	public boolean isOldWalletPassword(UserRegisterDTO userRegisterDTO) throws Exception;
	
	public boolean isValidWalletPassword(TokenDTO tokenDTO) throws Exception;
	
	public boolean referral(UserRegisterDTO userRegisterDTO) throws Exception;
	
	public boolean saveKycInfo(KycDTO kycDTO);

	public String userDocumentUpload(MultipartFile uploadedFileRef, String emailId,
			String docType) throws IOException;
	
	public boolean resendVerificationMail(UserRegisterDTO userRegisterDTO) throws Exception;
	
	public List<UserRegisterDTO> userList(UserRegisterDTO userRegisterDTO) throws Exception ;
	
	public boolean isEmailIdAndPasswordExists(UserRegisterDTO userRegisterDTO, String flag) throws Exception;
	
	public LoginDTO login(UserRegisterDTO userRegisterDTO)throws Exception;

	public boolean isChangeAuthentication(UserRegisterDTO userRegisterDTO);
	
	public LoginDTO transferSecure(TokenDTO tokenDTO) throws Exception;
	
	public boolean validateAdminIp(UserRegisterDTO userRegisterDTO) throws Exception;
	
	public boolean changeAdminIpAddress(UserRegisterDTO userRegisterDTO) throws Exception;
	
	public boolean isChangeWalletPassword(UserRegisterDTO userRegisterDTO);
	
	public boolean isUserExist(UserRegisterDTO userRegisterDTO, HttpServletRequest request) throws Exception;
	
	public boolean sendWalletPassword(UserRegisterDTO userRegisterDTO) throws Exception;
	
	public boolean createWallet(UserRegisterDTO userRegisterDTO) throws Exception;
	
	public List<UserRegisterDTO> viewUserDetails(UserRegisterDTO userRegisterDTO);
	
	public List<UserRegisterDTO> listAirdropUers(UserRegisterDTO userRegisterDTO);
	
	public List<UserRegisterDTO> listAirdropUersWithFilter(UserRegisterDTO userRegisterDTO);
	
	public boolean userRegistration(List<UserRegisterDTO> userList) throws Exception;
	
	public boolean registerPromoUser(UserRegisterDTO userRegisterDTO);
	
	public boolean checkPromotionStatus(UserRegisterDTO userRegisterDTO);
	
	public boolean addPromo(UserRegisterDTO userRegisterDTO);
	
	public List<UserRegisterDTO> listPromoUsers(UserRegisterDTO userRegisterDTO);
	
	public boolean savePromoAccountInfo(UserRegisterDTO userRegisterDTO) throws Exception;
	
	public boolean verifyAccessTokenForRegistration(UserRegisterDTO userRegisterDTO);
	
	public boolean verifyAccessTokenForBalance(UserRegisterDTO userRegisterDTO);
	
	public List<UserRegisterDTO> listAirdropUsers(UserRegisterDTO userRegisterDTO);
	
	public Integer checkUserExistWithEmailAndMobile(UserRegisterDTO userRegisterDTO);

	public boolean isAccountExistCheckByMobileNoForRegistrationViaVtnOrQuiz(String lowerCase, String promoCode);

	public boolean isAccountExistCheckByEmailIdForRegistrationViaVtnOrQuiz(UserRegisterDTO userRegisterDTO);



	

}
