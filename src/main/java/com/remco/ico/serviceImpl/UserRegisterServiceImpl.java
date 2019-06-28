package com.remco.ico.serviceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import com.google.common.io.Files;
import com.remco.ico.DTO.KycDTO;
import com.remco.ico.DTO.LoginDTO;
import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.UserRegisterDTO;
import com.remco.ico.model.AirDropUserBonus;
import com.remco.ico.model.AuthorizationInfo;
import com.remco.ico.model.BitcoinCashInfo;
import com.remco.ico.model.BitcoinInfo;
import com.remco.ico.model.Config;
import com.remco.ico.model.KycInfo;
import com.remco.ico.model.PromoUsersInfo;
import com.remco.ico.model.RegisterInfo;
import com.remco.ico.model.UserReferralInfo;
import com.remco.ico.repository.AirDropUserBonusRepository;
import com.remco.ico.repository.AuthorizationInfoRepository;
import com.remco.ico.repository.BitcoinCashInfoRepository;
import com.remco.ico.repository.BitcoinInfoRepository;
import com.remco.ico.repository.ConfigInfoRepository;
import com.remco.ico.repository.KycInfoRepository;
import com.remco.ico.repository.PromoUsersInfoRepository;
import com.remco.ico.repository.RegisterInfoRepository;
import com.remco.ico.repository.UserReferralInfoRepository;
import com.remco.ico.service.EmailNotificationService;
import com.remco.ico.service.UserRegisterService;
import com.remco.ico.utils.BitcoinCashUtils;
import com.remco.ico.utils.BitcoinUtils;
import com.remco.ico.utils.EncryptDecrypt;
import com.remco.ico.utils.RemcoUtils;

@Service
public class UserRegisterServiceImpl implements UserRegisterService {

	private static final Logger LOG = LoggerFactory.getLogger(UserRegisterServiceImpl.class);

	// private final Web3j web3j = Web3j.build(new
	// HttpService("https://rinkeby.infura.io"));

	@SuppressWarnings("unused")
	private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io"));

	@Autowired
	private RegisterInfoRepository registerInfoRepository;
	@Autowired
	private ConfigInfoRepository configInfoRepository;
	@Autowired
	private RemcoUtils remcoUtils;
	@Autowired
	private EmailNotificationService emailNotificationService;
	@Autowired
	private AuthorizationInfoRepository authorizationInfoRepository;
	@Autowired
	private KycInfoRepository kycInfoRepository;
	// @Autowired
	// private TokenRateRopository tokenRateRepository;
	@Autowired
	private Environment env;
	@Autowired
	private BitcoinCashUtils bitcoinCashUtils;
	@Autowired
	private BitcoinUtils bitcoinUtils;
	@Autowired
	private BitcoinCashInfoRepository bitcoinCashInfoRepository;
	@Autowired
	private BitcoinInfoRepository bitcoinInfoRepository;
	@Autowired
	private UserReferralInfoRepository userReferralInfoRepository;
	@Autowired
	private AirDropUserBonusRepository airDropUserBonusRepository;
	@Autowired
	private PromoUsersInfoRepository promoUsersInfoRepository;

	@Override
	public boolean isAccountExistCheckByEmailId(String emailId) {
		Integer isEmailExist = registerInfoRepository.countHealthycoinUserInfoByEmailIdIgnoreCase(emailId.trim());
		if (isEmailExist > 0) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean saveAccountInfo(UserRegisterDTO userRegisterDTO) throws Exception {

		RegisterInfo registerInfo = new RegisterInfo();
		String psw = userRegisterDTO.getPassword();

		if (userRegisterDTO.getReferralId() == null) {

			String encryptPassword = EncryptDecrypt.encrypt(userRegisterDTO.getPassword());
			registerInfo.setPassword(encryptPassword);
			registerInfo.setCreatedDate(new Date());
			registerInfo.setEmailId(userRegisterDTO.getEmailId());
			registerInfo.setMobileNo(userRegisterDTO.getMobileno());
			registerInfo.setFirstName(userRegisterDTO.getFirstName());
			registerInfo.setLastName(userRegisterDTO.getLastName());
			registerInfo.setAddress(userRegisterDTO.getUserAddress());
			registerInfo.setCity(userRegisterDTO.getUserCity());
			registerInfo.setZipcode(userRegisterDTO.getUserZipcode());
			registerInfo.setCountry(userRegisterDTO.getUserCountry());
			registerInfo.setTwofaStatus(userRegisterDTO.getTwofaStatus());
			registerInfo.setActive(0);
			registerInfo.setReferralId(0);
			registerInfo.setKycStatus("0");
			registerInfoRepository.save(registerInfo);
		} else {

			String refId = EncryptDecrypt.decrypt(userRegisterDTO.getReferralId());
			String encryptPassword = EncryptDecrypt.encrypt(userRegisterDTO.getPassword());
			registerInfo.setPassword(encryptPassword);
			registerInfo.setCreatedDate(new Date());
			registerInfo.setEmailId(userRegisterDTO.getEmailId());
			registerInfo.setMobileNo(userRegisterDTO.getMobileno());
			registerInfo.setFirstName(userRegisterDTO.getFirstName());
			registerInfo.setLastName(userRegisterDTO.getLastName());
			registerInfo.setAddress(userRegisterDTO.getUserAddress());
			registerInfo.setCity(userRegisterDTO.getUserCity());
			registerInfo.setZipcode(userRegisterDTO.getUserZipcode());
			registerInfo.setCountry(userRegisterDTO.getUserCountry());
			registerInfo.setTwofaStatus(userRegisterDTO.getTwofaStatus());
			registerInfo.setKycStatus("0");
			registerInfo.setActive(0);
			registerInfo.setReferralId(Integer.parseInt(refId));
			registerInfoRepository.save(registerInfo);
		}

		System.out.println("Password with space from the UI is--------->" + userRegisterDTO.getPassword() + "---");
		int id = registerInfo.getId();
		String dynamicQRFolder = Integer.toString(id);
		if (registerInfo != null) {
			boolean status = isWalletCreate(userRegisterDTO);

			System.out.println("Wallet Creation Result------>" + status);
			if (status) {

				boolean status1 = bitcoinCashUtils.generateBitcoinCashWallet(userRegisterDTO);

				boolean status2 = bitcoinUtils.createBitcoinWallet(userRegisterDTO);
				if (status1 && status2) {
					String WalletAddress = userRegisterDTO.getWalletAddress();
					String WalletPassword = userRegisterDTO.getWalletPassword();
					String encryptwalletAddress = EncryptDecrypt.encrypt(WalletAddress);
					String encryptwalletPassword = EncryptDecrypt.encrypt(WalletPassword);
					registerInfo.setWalletAddress(encryptwalletAddress);
					registerInfo.setWalletPassword(encryptwalletPassword);

					Config config = configInfoRepository.findConfigByConfigKey("walletfile");

					String walletAddr = remcoUtils.getWalletAddress(config.getConfigValue(),
							userRegisterDTO.getWalletAddress());
					userRegisterDTO.setWalletAddress(walletAddr);
					registerInfo.setWalletFile(walletAddr);

					String qrFileLocation = null;

					registerInfoRepository.save(registerInfo);
					if (userRegisterDTO.getReferralId() != null && userRegisterDTO.getReferralId() != "") {

						String refId2 = EncryptDecrypt.decrypt(userRegisterDTO.getReferralId());

						RegisterInfo regInfo = registerInfoRepository.findOne(Integer.parseInt(refId2));
						if (regInfo != null) {
							UserReferralInfo referralInfo = userReferralInfoRepository
									.findByUserEmailAndNewuserEmail(regInfo.getEmailId(), userRegisterDTO.getEmailId());
							if (referralInfo != null) {
								referralInfo.setStatus(1);
								referralInfo.setRegisteredDate(new Date());
								userReferralInfoRepository.save(referralInfo);
							}
							boolean isEmailSent = emailNotificationService.sendEmailforReferralRegister(
									regInfo.getEmailId(), "Referral Info from REMCO !", regInfo.getFirstName(),
									userRegisterDTO.getEmailId());
							if (!isEmailSent) {
								boolean isEmail = emailNotificationService.sendEmailforMailError(
										env.getProperty("admin1.email"), "Email Alert Info from REMCO", "");
							}
						}
					}
					boolean isEmailSent;
					if (userRegisterDTO.getMailFlag() == "AIRDROP") {
						isEmailSent = emailNotificationService.sendEmailforAirdropRegistration(
								userRegisterDTO.getEmailId(), "WELCOME TO THE REMCO WALLET !",
								registerInfo.getFirstName(), userRegisterDTO.getPassword(), walletAddr);

					} else {
						isEmailSent = emailNotificationService.sendEmailforRegistration(userRegisterDTO.getEmailId(),
								"WELCOME TO THE REMCO WALLET !", registerInfo.getFirstName(),
								registerInfo.getFirstName() + " " + registerInfo.getLastName(), walletAddr);

					}
					if (!isEmailSent) {
						boolean isEmail = emailNotificationService.sendEmailforMailError(
								env.getProperty("admin1.email"), "Email Alert Info from REMCO", "");
					}
					return true;

				}
			}
			registerInfoRepository.delete(registerInfo.getId());
			return false;
		}

		return false;
	}

	@Override
	public boolean isAccountExistCheckByMobileNo(String lowerCase) {
		try {
			List<RegisterInfo> regInfo = registerInfoRepository.findByMobileNo(lowerCase);
			if (regInfo.size() == 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isAccountExistCheckByMobileNoForRegistrationViaVtnOrQuiz(String lowerCase, String referSite) {
		try {
			List<RegisterInfo> regInfo = registerInfoRepository.findByMobileNo(lowerCase);
			if (regInfo.size() == 0) {
				return false;
			} else {
				PromoUsersInfo promo = promoUsersInfoRepository.findByMobileNoAndPromoType(lowerCase, referSite);
				if (promo == null) {
					return false;
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isWalletCreate(UserRegisterDTO userRegisterDTO) {
		try {

			System.out.println("Inside Wallet Creation Start");
			Config configInfo = configInfoRepository.findConfigByConfigKey("walletfile");
			if (configInfo != null) {
				String walletFileLocation = configInfo.getConfigValue();

				System.out.println("Config Info is not null");

				walletFileLocation = walletFileLocation.replace("/", "\\");
				File createfolder = new File(walletFileLocation);
				if (!createfolder.exists()) {
					createfolder.mkdirs();
				}

				System.out.println("walletFileLocation " + walletFileLocation);

				String fileName = null;
				System.out.println("Before Wallet File Generation");
				fileName = WalletUtils.generateNewWalletFile(userRegisterDTO.getWalletPassword(),
						new File(walletFileLocation), false);
				System.out.println("After Wallet File Generation");
				userRegisterDTO.setWalletAddress(fileName);

				System.out.println("Inside Wallet Creation End");
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	@Override
	public boolean activateUser(LoginDTO loginDTO) throws Exception {
		/*
		 * String urlString2Decode =loginDTO.getEmailId(); String decodedURL =
		 * URLDecoder.decode(urlString2Decode, "UTF-8");
		 * System.out.println("Value from URL------>"+urlString2Decode);
		 * System.out.println("Decoded Value From URL------>"+decodedURL); String
		 * decrptEmail = EncryptDecrypt.decrypt(decodedURL);
		 */

		String emailId = loginDTO.getEmailId().replaceAll("\\s", "+");

		emailId = EncryptDecrypt.decrypt(emailId);

		RegisterInfo regInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(emailId);

		if (regInfo != null) {

			if (regInfo.getActive() == 0) {
				regInfo.setActive(1);
				registerInfoRepository.save(regInfo);
				loginDTO.setMessage("Email Verification is Success!");
				return true;

			} else {
				loginDTO.setMessage("Email ID Already Verified!");
				return false;
			}

		} else if (regInfo == null) {
			loginDTO.setMessage("Email ID is Not Exist!");
		}

		return false;
	}

	@Override
	public LoginDTO isEmailAndPasswordExit(UserRegisterDTO userRegisterDTO) throws Exception {

		LoginDTO responseDTO = new LoginDTO();
		RegisterInfo registerInfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId().trim());
		String password = registerInfo.getPassword();

		String decryptPassword = EncryptDecrypt.decrypt(password);
		if (userRegisterDTO.getPassword().equals(decryptPassword)) {

			String key = remcoUtils.keyGenerater(userRegisterDTO);
			responseDTO.setSecuredKey(key);
			responseDTO.setStatus("success");
			return responseDTO;
		}
		responseDTO.setStatus("failed");
		return responseDTO;
	}

	public LoginDTO loginSecure(UserRegisterDTO userRegisterDTO) throws Exception {

		System.out.println("Inside login secure method");
		LoginDTO responseDTO = new LoginDTO();
		RegisterInfo registerInfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId().trim());
		Config configInfo = configInfoRepository.findConfigByConfigKey("WalletFile");
		LOG.info("Email Id : " + registerInfo);
		LOG.info("Email Id : " + registerInfo.getEmailId());

		if (registerInfo != null && configInfo != null) {
			String refId = EncryptDecrypt.encrypt(registerInfo.getId().toString());
			long first = registerInfo.getKeyDate().getTime() / 1000;
			long second = new Date().getTime() / 1000;
			long diff = second - first;

			Config keyInfo = configInfoRepository.findConfigByConfigKey("keyvalidity");

			if (userRegisterDTO.getSecuredKey().equals(registerInfo.getSecuredKey())) {

				if (diff <= Integer.parseInt(keyInfo.getConfigValue())) {

					boolean token = remcoUtils.saveAuthenticationDetails(userRegisterDTO);
					System.out.println("token" + token);

					if (token) {
						if (registerInfo.getPromoFlag() == null) {
							String getBitcoinCashAddress = bitcoinCashUtils.getWalletAddress(userRegisterDTO);
							Double balance = bitcoinCashUtils.getBitcoinCashBalance(userRegisterDTO);
							if (getBitcoinCashAddress != null) {
								responseDTO.setBitcoinCashWalletAddress(getBitcoinCashAddress);
								Double bch = balance / 100000000;
								responseDTO.setBitcoinCashWalletBalance(bch);
							}
							String getbitcoinAddress = bitcoinUtils.getWalletAddress(userRegisterDTO);
							if (getbitcoinAddress != null) {
								responseDTO.setBitcoinWalletAddress(getbitcoinAddress);
							}
						}

						System.out.println("token");
						responseDTO.setEmailId(registerInfo.getEmailId());
						String walletAddress = EncryptDecrypt.decrypt(registerInfo.getWalletAddress());
						System.out.println("walletAddress" + walletAddress);
						String etherWalletAddress = remcoUtils.getWalletAddress(configInfo.getConfigValue(),
								walletAddress);
						responseDTO.setWalletAddress(etherWalletAddress);
						responseDTO.setFirstName(registerInfo.getFirstName());
						responseDTO.setLastName(registerInfo.getLastName());
						responseDTO.setUserType(registerInfo.getUserType());
						responseDTO.setKycStatus(registerInfo.getKycStatus());
						responseDTO.setReferralLink(env.getProperty("referral.link") + refId);
						if (registerInfo.getPromoFlag() == null) {
							responseDTO.setPromoFlag("FALSE");
						} else {
							responseDTO.setPromoFlag(registerInfo.getPromoFlag());
						}
						// TokenRate tokenRate = tokenRateRepository.findById(1);
						// if(tokenRate!=null)
						// {
						// responseDTO.setCoinRate(tokenRate.getTokenValue());
						// }
						AuthorizationInfo authorizationInfo = authorizationInfoRepository
								.findByEmailIdIgnoreCase(userRegisterDTO.getEmailId().trim());
						if (authorizationInfo != null) {
							responseDTO.setAuthToken(authorizationInfo.getAuthToken());
						}

						responseDTO.setStatus("success");
						userRegisterDTO.setMessage("Logged in successfully!");
						return responseDTO;

					}
					responseDTO.setStatus("failed");
					userRegisterDTO.setMessage("Auth Token creation failed!");
					return responseDTO;
				}
				responseDTO.setStatus("failed");
				userRegisterDTO.setMessage(
						"The Security PIN has expired!. Click here to login again and make sure your PIN is entered immediately.");
				return responseDTO;

			}

			responseDTO.setStatus("failed");
			userRegisterDTO.setMessage("Invalid Security PIN!");
			return responseDTO;

		}
		responseDTO.setStatus("failed");
		userRegisterDTO.setMessage("User does not exist!");
		return responseDTO;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean isEmailIdExist(UserRegisterDTO userRegisterDTO, HttpServletRequest request) throws Exception {

		RegisterInfo UserInfos = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId().trim());
		if (UserInfos != null) {

			userRegisterDTO.setEmailId(UserInfos.getEmailId());
			String decryptPassword = EncryptDecrypt.decrypt(UserInfos.getPassword());
			UserInfos.setPassword(decryptPassword);
			boolean isEmailSent = emailNotificationService.sendEmailforgot(userRegisterDTO.getEmailId(),
					"Password info from Remco Tokens", UserInfos.getPassword(),
					UserInfos.getFirstName() + " " + UserInfos.getLastName());
			if (!isEmailSent) {
				boolean isEmail = emailNotificationService.sendEmailforMailError(env.getProperty("admin1.email"),
						"Email Alert Info from REMCO", "");
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean saveNewPasswordInfo(UserRegisterDTO userRegisterDTO) throws Exception {

		/*
		 * String urlString2Decode =userRegisterDTO.getEmailId(); String decodedURL =
		 * java.net.URLDecoder.decode(urlString2Decode, "UTF-8");
		 * 
		 * System.out.println("decodedURL URL"+decodedURL);
		 * 
		 * String decrptUser = EncryptDecrypt.decrypt(decodedURL);
		 * 
		 * System.out.println("decrpt use------>"+decrptUser);
		 */

		RegisterInfo registerInfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());
		// RegisterInfo registerInfo =new RegisterInfo();
		String encryptPassword = EncryptDecrypt.encrypt(userRegisterDTO.getNewPassword());
		registerInfo.setPassword(encryptPassword);
		registerInfoRepository.save(registerInfo);
		if (registerInfo != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isOldPassword(UserRegisterDTO userRegisterDTO) throws Exception {

		RegisterInfo registerinfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());
		String oldpassword = registerinfo.getPassword();
		String decryptPassword = EncryptDecrypt.decrypt(oldpassword);
		if (decryptPassword.equals(userRegisterDTO.getOldPassword())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isChangePassword(UserRegisterDTO userRegisterDTO) {
		// HttpSession session =
		// SessionCollector.find(userRegisterDTO.getSessionId());
		// String email = (String) session.getAttribute("emailId");
		RegisterInfo registerinfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());
		String confirmChangePassword = userRegisterDTO.getConfirmPassword();
		if (confirmChangePassword != null) {
			try {
				String encryptconfirmPassword = EncryptDecrypt.encrypt(confirmChangePassword);
				if (encryptconfirmPassword != null) {
					registerinfo.setPassword(encryptconfirmPassword);
					registerInfoRepository.save(registerinfo);
				}
			} catch (Exception e) {
				// LOG.error("Problem in Change Password:", e);
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isValidWalletPassword(TokenDTO tokenDTO) throws Exception {

		RegisterInfo regInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
		if (regInfo != null) {
			String WalletPassword = EncryptDecrypt.decrypt(regInfo.getWalletPassword());
			if (tokenDTO.getWalletPassword().equals(WalletPassword)) {
				tokenDTO.setWalletPassword(WalletPassword);
				return true;
			}

		}
		return false;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean referral(UserRegisterDTO userRegisterDTO) throws Exception {

		RegisterInfo registerInfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());

		String id = EncryptDecrypt.encrypt(registerInfo.getId().toString());

		UserReferralInfo userInfo = userReferralInfoRepository
				.findByUserEmailAndNewuserEmail(userRegisterDTO.getEmailId(), userRegisterDTO.getReferralEmailId());

		if (userInfo != null) {
			if (userInfo.getReferralCount() >= 3) {
				userRegisterDTO.setMessage("You cannot refer the same user More than 3 times!");
				return false;
			} else {
				Integer count = userInfo.getReferralCount() + 1;
				userInfo.setReferralCount(count);
				userInfo.setReferredDate(new Date());
				userReferralInfoRepository.save(userInfo);
			}
		} else {
			UserReferralInfo newUserInfo = new UserReferralInfo();
			newUserInfo.setNewuserEmail(userRegisterDTO.getReferralEmailId());
			newUserInfo.setReferralCount(1);
			newUserInfo.setUserEmail(userRegisterDTO.getEmailId());
			newUserInfo.setReferredDate(new Date());
			newUserInfo.setStatus(0);
			userReferralInfoRepository.save(newUserInfo);

		}

		boolean isEmailSent = emailNotificationService.sendEmailforReferral(userRegisterDTO.getReferralEmailId(),
				"Referral Info From Remco Team", id, registerInfo.getFirstName() + " " + registerInfo.getLastName(),
				userRegisterDTO.getRefUserName());

		if (!isEmailSent) {
			boolean isEmail = emailNotificationService.sendEmailforMailError(env.getProperty("admin1.email"),
					"Email Alert Info from REMCO", "");
		}
		if (isEmailSent) {
			userRegisterDTO.setMessage("Referral Success!");
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean saveKycInfo(KycDTO kycDTO) {

		KycInfo kycInfo = kycInfoRepository.findKycInfoByEmailIdIgnoreCase(kycDTO.getEmailId());
		if (kycInfo != null) {
			kycInfo.setFirstName(kycDTO.getFirstName());
			kycInfo.setLastName(kycDTO.getLastName());
			// kycInfo.setMidName(kycDTO.getMiddleName());
			kycInfo.setIdentityType(kycDTO.getIdentityType());
			kycInfo.setIdentityNo(kycDTO.getIdentityNo());
			kycInfo.setCountryOfIssue(kycDTO.getCountryOfIssue());
			kycInfo.setDateOfIssue(kycDTO.getDateOfIssue());
			kycInfo.setDateOfExpiry(kycDTO.getDateOfExpiry());
			kycInfo.setResState(kycDTO.getResState());
			kycInfo.setResCountry(kycDTO.getResCountry());
			kycInfo.setSaleType(kycDTO.getSaleType());
			kycInfo.setEmailId(kycDTO.getEmailId());
			kycInfo.setIdPath(kycDTO.getIdPath());
			kycInfo.setSchoolName(kycDTO.getSchoolName());
			kycInfo.setKycStatus("0");
			if (kycDTO.getSaleType().equals("PUBLIC")) {
				kycInfo.setInvEntityType(kycDTO.getEntityType());
				kycInfo.setEntityName(kycDTO.getEntityName());
				kycInfo.setHowAccredited(kycDTO.getHowAccredited());
				kycInfo.setInvAmount(kycDTO.getInvAmount());
				// kycInfo.setIsVesting(kycDTO.getIsVesting());

			}
			kycInfoRepository.save(kycInfo);
			RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(kycDTO.getEmailId());
			if (registerInfo != null) {
				registerInfo.setKycStatus("1");
				registerInfoRepository.save(registerInfo);
			}
			return true;
		}
		if (kycInfo == null) {
			KycInfo kycInfos = new KycInfo();
			kycInfos.setFirstName(kycDTO.getFirstName());
			kycInfos.setLastName(kycDTO.getLastName());
			// kycInfos.setMidName(kycDTO.getMiddleName());
			kycInfos.setIdentityType(kycDTO.getIdentityType());
			kycInfos.setIdentityNo(kycDTO.getIdentityNo());
			kycInfos.setCountryOfIssue(kycDTO.getCountryOfIssue());
			kycInfos.setDateOfIssue(kycDTO.getDateOfIssue());
			kycInfos.setDateOfExpiry(kycDTO.getDateOfExpiry());
			kycInfos.setResState(kycDTO.getResState());
			kycInfos.setResCountry(kycDTO.getResCountry());
			kycInfos.setSaleType(kycDTO.getSaleType());
			kycInfos.setEmailId(kycDTO.getEmailId());
			kycInfos.setIdPath(kycDTO.getIdPath());
			kycInfos.setSchoolName(kycDTO.getSchoolName());
			kycInfos.setKycStatus("0");
			if (kycDTO.getSaleType().equals("PUBLIC")) {
				kycInfos.setInvEntityType(kycDTO.getEntityType());
				kycInfos.setEntityName(kycDTO.getEntityName());
				kycInfos.setHowAccredited(kycDTO.getHowAccredited());
				kycInfos.setInvAmount(kycDTO.getInvAmount());
				// kycInfos.setIsVesting(kycDTO.getIsVesting());
				// kycInfos.setSchoolName(kycDTO.getSchoolName());
			}
			kycInfoRepository.save(kycInfos);
			RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(kycDTO.getEmailId());
			if (registerInfo != null) {
				registerInfo.setKycStatus("1");
				registerInfoRepository.save(registerInfo);
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	@Override
	public String userDocumentUpload(MultipartFile uploadedFileRef, String emailId, String docType) throws IOException {

		FileInputStream reader = null;
		FileOutputStream writer = null;
		String path = null;
		String dbPath = null;
		Integer userRegId = null;

		System.out.println("Mail Id--------->" + emailId);
		LOG.info("In  userDocumentUpload : start");
		System.out.println("User Register ID---------->" + userRegId);
		try {
			Config configInfo = configInfoRepository.findConfigByConfigKey("location");
			String basePath = configInfo.getConfigValue();

			RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(emailId);
			if (registerInfo != null) {
				userRegId = registerInfo.getId();
				System.out.println("USerRegisterId----------->" + registerInfo.getId());
			}
			// LOG.info(" Base path from config table : " + basePath);

			String uploadingdir = basePath + env.getProperty("dynamic.file.path.kyc") + userRegId + File.separator
					+ docType;

			String databasedir = env.getProperty("dynamic.file.path.kyc") + userRegId + File.separator + docType;

			LOG.info(" uploadingdir : " + uploadingdir);

			File file = new File(uploadingdir);

			if (file.exists()) {
				file.delete();
			}
			if (!file.exists()) {
				LOG.info(" In mkdir : " + uploadingdir);
				file.mkdirs();
			}
			LOG.info(" uploadingdir : " + uploadingdir);

			String fileType = Files.getFileExtension(uploadedFileRef.getOriginalFilename());
			String fileName = Files.getNameWithoutExtension(uploadedFileRef.getOriginalFilename());

			long fileSizeInBytes = file.length();
			// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
			long fileSizeInKB = fileSizeInBytes / 1024;
			// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
			long fileSizeInMB = fileSizeInKB / 1024;

			double fileSize = uploadedFileRef.getSize() / 1024;
			double allowedFileSize = Long.parseLong("5096");

			System.out.println("File size in MB----------->" + fileSize);
			System.out.println("File Type----------->" + fileType);

			if (fileType.equalsIgnoreCase("jpeg") || fileType.equalsIgnoreCase("png")
					|| fileType.equalsIgnoreCase("jpg") || fileType.equalsIgnoreCase("pdf")) {

				System.out.println("Uploaded File Name-------->" + fileName);
				path = uploadingdir + File.separator + userRegId + "." + fileType;
				dbPath = env.getProperty("dynamic.file.path.kyc") + userRegId + File.separator + docType
						+ File.separator + userRegId + "." + fileType;
				LOG.info(" file path : " + path);
				LOG.info("dbPath : " + dbPath);
				byte[] buffer = new byte[1000];
				File outputFile = new File(path);

				int totalBytes = 0;
				outputFile.createNewFile();
				reader = (FileInputStream) uploadedFileRef.getInputStream();
				writer = new FileOutputStream(outputFile);

				int bytesRead = 0;
				while ((bytesRead = reader.read(buffer)) != -1) {
					writer.write(buffer);
					totalBytes += bytesRead;
				}
				dbPath = dbPath.replace(File.separator, "/");
				/*
				 * KycInfo kycUpdate = kycInfoRepository.findKycInfoById(userRegId);
				 * if(docType.equals("panId")) {
				 * 
				 * kycUpdate.setPanPath(path); kycInfoRepository.save(kycUpdate);
				 * 
				 * } if(docType.equals("aadharId")) { kycUpdate.setAadharPath(path);
				 * kycInfoRepository.save(kycUpdate); }
				 */

				RegisterInfo regInfos = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(emailId);
				if (regInfos != null) {
					regInfos.setKycStatus("1");
					registerInfoRepository.save(regInfos);
				}
				reader.close();
				writer.close();
			} else {
				path = null;
				dbPath = null;
			}

		} catch (IOException e) {
			path = null;
			dbPath = null;
			reader.close();
			writer.close();
			LOG.error("Problem in userDocumentUpload file path : " + path);
			e.printStackTrace();
		} finally {

		}
		LOG.info("In  userDocumentUpload : end : dbPath : " + dbPath);
		return dbPath;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean resendVerificationMail(UserRegisterDTO userRegisterDTO) throws Exception {

		RegisterInfo UserInfos = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId().trim());
		if (UserInfos != null) {

			userRegisterDTO.setEmailId(UserInfos.getEmailId());
			String decryptPassword = EncryptDecrypt.decrypt(UserInfos.getPassword());
			UserInfos.setPassword(decryptPassword);
			boolean isEmailSent = emailNotificationService.resendEmailforRegistration(UserInfos.getEmailId(),
					"Email verification info from Remco Tokens", UserInfos.getFirstName());
			if (!isEmailSent) {
				boolean isEmail = emailNotificationService.sendEmailforMailError(env.getProperty("admin1.email"),
						"Email Alert Info from REMCO", "");
			}
			return true;
		}
		return false;
	}

	@Override
	public List<UserRegisterDTO> userList(UserRegisterDTO userRegisterDTO) throws Exception {

		List<UserRegisterDTO> regiUserList = new ArrayList<UserRegisterDTO>();
		Sort sort = new Sort(new Sort.Order(Direction.ASC, "id"));
		Pageable pageable = new PageRequest(userRegisterDTO.getPageNum(), userRegisterDTO.getPageSize(), sort);
		Page<RegisterInfo> userlist = null;

		Integer userCount = registerInfoRepository.countRegisterInfoUserCountByUserType(0);
		userRegisterDTO.setUserCount(userCount);

		if (userRegisterDTO.getSearchTxt() != null && userRegisterDTO.getSearchTxt() != "") {
			if (userRegisterDTO.getStatus() == -1) {
				userlist = registerInfoRepository.getByNames(userRegisterDTO.getSearchTxt(),
						userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(),
						-1, 0, 1, pageable);
			}
			if (userRegisterDTO.getStatus() == 1) {
				userlist = registerInfoRepository.getByNames(userRegisterDTO.getSearchTxt(),
						userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(),
						1, 1, 1, pageable);

			}
			if (userRegisterDTO.getStatus() == 0) {
				userlist = registerInfoRepository.getByNames(userRegisterDTO.getSearchTxt(),
						userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(),
						0, 0, 0, pageable);
			}

		} else {

			if (userRegisterDTO.getStatus() == 1) {
				userlist = registerInfoRepository.findRegisterInfoByUserTypeAndActive(0, pageable, 1);
			}
			if (userRegisterDTO.getStatus() == 0) {
				userlist = registerInfoRepository.findRegisterInfoByUserTypeAndActive(0, pageable, 0);
			}
			if (userRegisterDTO.getStatus() == -1) {
				userlist = registerInfoRepository.findRegisterInfoByUserType(0, pageable);
			}
		}
		// Page<RegisterInfo> userlist =
		// registerInfoRepository.findRegisterInfoByUserTypeAndActiveOrActive(0,
		// pageable, "1","0");
		// List<RegisterInfo> userlist =
		// registerInfoRepository.findRegisterInfoByUserType(0);
		userRegisterDTO.setTotalPages(userlist.getTotalPages());
		userRegisterDTO.setTotalElements(userlist.getTotalElements());
		for (RegisterInfo userList : userlist) {
			UserRegisterDTO transaction = remcoUtils.userList(userList);
			regiUserList.add(transaction);
		}
		return regiUserList;
	}

	@Override
	public boolean isEmailIdAndPasswordExists(UserRegisterDTO userRegisterDTO, String flag) throws Exception {

		LOG.info("Object DTO---" + userRegisterDTO.getEmailId() + userRegisterDTO.getPassword());

		String encryptPassword = EncryptDecrypt.encrypt(userRegisterDTO.getPassword());

		RegisterInfo registerInfo = registerInfoRepository
				.findRemcoUserInfoByEmailIdAndPassword(userRegisterDTO.getEmailId().trim(), encryptPassword);
		// LOGIN Success
		if (registerInfo != null) {
			if (registerInfo.getPromoFlag() != null) {
				if (registerInfo.getPromoFlag().equals("TRUE")) {
					userRegisterDTO.setPromoFlag("PROMO");
				}
			}
			if (flag.equals("LOGIN")) {
				userRegisterDTO.setTwofaStatus(registerInfo.getTwofaStatus());
			}
			return true;
		}
		return false;
	}

	public LoginDTO login(UserRegisterDTO userRegisterDTO) throws Exception {

		LoginDTO responseDTO = new LoginDTO();
		RegisterInfo registerInfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId().trim());
		Config configInfo = configInfoRepository.findConfigByConfigKey("WalletFile");
		LOG.info("Email Id : " + registerInfo);

		if (registerInfo != null && configInfo != null) {

			String refId = EncryptDecrypt.encrypt(registerInfo.getId().toString());
			boolean token = remcoUtils.saveAuthenticationDetails(userRegisterDTO);
			System.out.println("token" + token);

			if (token) {

				if (registerInfo.getPromoFlag() == null) {
					String getBitcoinCashAddress = bitcoinCashUtils.getWalletAddress(userRegisterDTO);
					Double balance = bitcoinCashUtils.getBitcoinCashBalance(userRegisterDTO);
					if (getBitcoinCashAddress != null) {
						responseDTO.setBitcoinCashWalletAddress(getBitcoinCashAddress);
						Double bch = balance / 100000000;
						responseDTO.setBitcoinCashWalletBalance(bch);
					}
					String getbitcoinAddress = bitcoinUtils.getWalletAddress(userRegisterDTO);
					if (getbitcoinAddress != null) {
						responseDTO.setBitcoinWalletAddress(getbitcoinAddress);
					}

				}

				System.out.println("token");
				responseDTO.setEmailId(registerInfo.getEmailId());
				String walletAddress = EncryptDecrypt.decrypt(registerInfo.getWalletAddress());
				System.out.println("walletAddress" + walletAddress);
				String etherWalletAddress = remcoUtils.getWalletAddress(configInfo.getConfigValue(), walletAddress);
				responseDTO.setWalletAddress(etherWalletAddress);
				responseDTO.setFirstName(registerInfo.getFirstName());
				responseDTO.setLastName(registerInfo.getLastName());
				responseDTO.setUserType(registerInfo.getUserType());
				responseDTO.setKycStatus(registerInfo.getKycStatus());
				responseDTO.setReferralLink(env.getProperty("referral.link") + refId);
				if (registerInfo.getPromoFlag() == null) {
					responseDTO.setPromoFlag("FALSE");
				} else {
					responseDTO.setPromoFlag(registerInfo.getPromoFlag());
				}
				AuthorizationInfo authorizationInfo = authorizationInfoRepository
						.findByEmailIdIgnoreCase(userRegisterDTO.getEmailId().trim());
				if (authorizationInfo != null) {
					responseDTO.setAuthToken(authorizationInfo.getAuthToken());
				}

				responseDTO.setStatus("success");
				return responseDTO;
			}
		}
		responseDTO.setStatus("failed");
		return responseDTO;
	}

	@Override
	public boolean isChangeAuthentication(UserRegisterDTO userRegisterDTO) {

		System.out.println("Email Id -------->" + userRegisterDTO.getEmailId());
		RegisterInfo regInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());

		if (regInfo != null) {
			System.out.println("Inside 2FA update not null");
			System.out.println("2fa VALUE----->" + userRegisterDTO.getTwofaStatus());
			regInfo.setTwofaStatus(userRegisterDTO.getTwofaStatus());
			registerInfoRepository.save(regInfo);
			System.out.println("Inside 2FA Afetr UpdATE");
			return true;
		}
		return false;
	}

	public LoginDTO transferSecure(TokenDTO tokenDTO) throws Exception {

		LoginDTO responseDTO = new LoginDTO();
		RegisterInfo registerInfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId().trim());
		LOG.info("Email Id : " + registerInfo);

		if (registerInfo != null) {

			System.out.println("Register Info is not null--");
			long first = registerInfo.getKeyDate().getTime() / 1000;
			long second = new Date().getTime() / 1000;
			long diff = second - first;

			if (diff <= 60) {

				System.out.println("More than 60 seconds Info is not null--");

				System.out.println("Key from user--->" + tokenDTO.getSecuredKey());
				System.out.println("Key from DB--->" + registerInfo.getSecuredKey());

				if (tokenDTO.getSecuredKey().equals(registerInfo.getSecuredKey())) {

					System.out.println("returns success--");
					tokenDTO.setUserType(registerInfo.getUserType());
					tokenDTO.setFromAddress(registerInfo.getWalletFile());
					responseDTO.setStatus("success");
					return responseDTO;
				}
			}
		}
		responseDTO.setStatus("failed");
		return responseDTO;
	}

	@Override
	public boolean validateAdminIp(UserRegisterDTO userRegisterDTO) throws Exception {

		RegisterInfo regInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());
		String ip = regInfo.getIpAddress();
		String[] ips = ip.split(",");

		System.out.println("IP from DB------->" + regInfo.getIpAddress());
		System.out.println("IP from UI------->" + userRegisterDTO.getIpAddress());

		for (int i = 0; i < ips.length; i++) {
			// String encryptip = userRegisterDTO.getIpAddress().trim();
			String encryptip = EncryptDecrypt.encrypt(userRegisterDTO.getIpAddress().trim());
			if (ips[i].toString().equals(encryptip)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean changeAdminIpAddress(UserRegisterDTO userRegisterDTO) throws Exception {

		RegisterInfo regInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());

		if (regInfo != null) {

			if (userRegisterDTO.getIpFlag().equals("VIEW")) {
				System.out.println("Inside user is there");
				String[] ips = regInfo.getIpAddress().split(",");

				for (int i = 0; i < ips.length; i++) {
					System.out.println("Inside for lopp");
					ips[i] = EncryptDecrypt.decrypt(ips[i]);
					// ips[i]=EncryptDecrypt.sha256(ips[i]);
				}

				userRegisterDTO.setIpAddresses(ips);

				userRegisterDTO.setMessage("IP Addresses listed successfully!");

				/*
				 * System.out.println("After for lopp"); String encryptIp = String.join(",",
				 * ips);
				 * 
				 * System.out.println("Encrypted String ip---->"+encryptIp);
				 */

			} else if (userRegisterDTO.getIpFlag().equals("ADD")) {
				String oldIp = regInfo.getIpAddress();
				String[] oldIps = regInfo.getIpAddress().split(",");
				String[] ips = userRegisterDTO.getIpAddress().split(",");

				for (int i = 0; i < ips.length; i++) {

					for (int j = 0; j < oldIps.length; j++) {
						System.out.println("Old IPs ---->" + oldIps[j]);
						oldIps[j] = EncryptDecrypt.decrypt(oldIps[j]);
						if (ips[i].equals(oldIps[j])) {
							userRegisterDTO.setMessage("IP Address " + ips[i] + " is already exist");
							return false;
						}
						oldIps[j] = EncryptDecrypt.encrypt(oldIps[j]);

					}

				}

				for (int i = 0; i < ips.length; i++) {
					System.out.println("Inside for lopp");
					ips[i] = EncryptDecrypt.encrypt(ips[i]);
				}

				String encryptIp = String.join(",", ips);
				System.out.println("Encrytpt Ip-------->" + encryptIp);
				System.out.println("Old IP---->" + oldIp);

				if (StringUtils.isNotBlank(oldIp) && oldIp != "") {
					System.out.println("Insid if------>");
					regInfo.setIpAddress(oldIp + "," + encryptIp);
				} else {
					System.out.println("Insid else------>");
					regInfo.setIpAddress(encryptIp);
				}
			} else if (userRegisterDTO.getIpFlag().equals("DELETE")) {
				String[] ips = regInfo.getIpAddress().split(",");
				for (int i = 0; i < ips.length; i++) {
					System.out.println("Inside for lopp");
					ips[i] = EncryptDecrypt.decrypt(ips[i]);

					if (ips[i].equals(userRegisterDTO.getIpAddress())) {
						List<String> list = new ArrayList<String>(Arrays.asList(ips));
						list.remove(i);
						// list.remove(EncryptDecrypt.encrypt(ips[i]));
						ips = list.toArray(new String[i]);

					} else {
						ips[i] = EncryptDecrypt.encrypt(ips[i]);
					}

				}

				String encryptIp = String.join(",", ips);
				regInfo.setIpAddress(encryptIp);
			}

			registerInfoRepository.save(regInfo);
			return true;

		}
		userRegisterDTO.setMessage("Problem in the updating IP addresses!");
		return false;
	}

	@Override
	public boolean isOldWalletPassword(UserRegisterDTO userRegisterDTO) throws Exception {

		RegisterInfo registerinfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());
		String oldpassword = registerinfo.getWalletPassword();
		String decryptPassword = EncryptDecrypt.decrypt(oldpassword);

		if (decryptPassword.equals(userRegisterDTO.getOldWalletPassword())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isChangeWalletPassword(UserRegisterDTO userRegisterDTO) {

		RegisterInfo registerinfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());
		String confirmChangePassword = userRegisterDTO.getConfirmWalletPassword();
		if (confirmChangePassword != null) {
			try {
				String encryptconfirmPassword = EncryptDecrypt.encrypt(confirmChangePassword);
				if (encryptconfirmPassword != null) {
					registerinfo.setWalletPassword(encryptconfirmPassword);
					registerInfoRepository.save(registerinfo);
				}
			} catch (Exception e) {
				// LOG.error("Problem in Change Password:", e);
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isUserExist(UserRegisterDTO userRegisterDTO, HttpServletRequest request) throws Exception {

		RegisterInfo UserInfos = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId().trim());
		if (UserInfos != null) {
			userRegisterDTO.setEmailId(UserInfos.getEmailId());
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean sendWalletPassword(UserRegisterDTO userRegisterDTO) throws Exception {
		/*
		 * HttpSession session = SessionCollector.find(userRegisterDTO.getSessionId());
		 * String email = (String) session.getAttribute("emailId");
		 */
		RegisterInfo registerinfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());
		if (registerinfo != null) {
			String WalletPassword = EncryptDecrypt.decrypt(registerinfo.getWalletPassword());
			if (WalletPassword != null) {

				boolean isEmailSent = emailNotificationService.sendEmailforgotReset(userRegisterDTO.getEmailId(),
						"Ether Wallet Password info from REMCO", WalletPassword, registerinfo.getFirstName());
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean createWallet(UserRegisterDTO userRegisterDTO) throws Exception {

		List<RegisterInfo> userList = registerInfoRepository.findAll();

		Integer i = 0;
		for (RegisterInfo user : userList) {
			String walletPassword = EncryptDecrypt.decrypt(user.getWalletPassword());
			userRegisterDTO.setEmailId(user.getEmailId());
			userRegisterDTO.setWalletPassword(walletPassword);

			BitcoinInfo bitcoin = bitcoinInfoRepository.findByEmailId(user.getEmailId());
			BitcoinCashInfo bitcoinCash = bitcoinCashInfoRepository.findByEmailId(user.getEmailId());

			if (bitcoin == null && bitcoinCash == null) {

				boolean loginCrypto = bitcoinCashUtils.loginCrypto(userRegisterDTO);
				if (loginCrypto) {
					boolean status1 = bitcoinCashUtils.generateBitcoinCashWallet(userRegisterDTO);
					boolean status2 = bitcoinUtils.createBitcoinWallet(userRegisterDTO);
					i = i + 1;

					if (status1 && status2) {
					}
					if (i > 150) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<UserRegisterDTO> viewUserDetails(UserRegisterDTO userRegisterDTO) {

		try {

			List<UserRegisterDTO> userList = new ArrayList<UserRegisterDTO>();

			RegisterInfo regInfo = registerInfoRepository
					.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getUserEmailId());

			if (regInfo != null) {
				userRegisterDTO.setFirstName(regInfo.getFirstName());
				userRegisterDTO.setLastName(regInfo.getLastName());
				userRegisterDTO.setMobileno(regInfo.getMobileNo());
				userRegisterDTO.setId(regInfo.getId());
				userRegisterDTO.setUserType(regInfo.getUserType());
				userRegisterDTO.setStatus(new Integer(regInfo.getActive()));
				userRegisterDTO.setWalletAddress(regInfo.getWalletFile());
				userRegisterDTO.setUserName(regInfo.getFirstName() + " " + regInfo.getLastName());
				userRegisterDTO.setKycStatus(regInfo.getKycStatus());
				userRegisterDTO.setUserCity(regInfo.getCity());
				userRegisterDTO.setUserCountry(regInfo.getCountry());
				userRegisterDTO.setCreatedDate(regInfo.getCreatedDate());
				userRegisterDTO.setEmailId(null);

				userList.add(userRegisterDTO);

				return userList;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@Override
	public List<UserRegisterDTO> listAirdropUers(UserRegisterDTO userRegisterDTO) {

		try {

			List<UserRegisterDTO> userList = new ArrayList<UserRegisterDTO>();

			List<AirDropUserBonus> regInfo = (List<AirDropUserBonus>) airDropUserBonusRepository.findAll();

			for (AirDropUserBonus info : regInfo) {

				UserRegisterDTO list = remcoUtils.airDropUserList(info);
				userList.add(list);

			}
			return userList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<UserRegisterDTO> listAirdropUersWithFilter(UserRegisterDTO userRegisterDTO) {

		try {
			List<UserRegisterDTO> userList = new ArrayList<UserRegisterDTO>();

			Sort sort = new Sort(new Sort.Order(Direction.ASC, "id"));
			Pageable pageable = new PageRequest(userRegisterDTO.getPageNum(), userRegisterDTO.getPageSize(), sort);

			Page<AirDropUserBonus> regInfo = null;

			if (userRegisterDTO.getSearchTxt() != null && userRegisterDTO.getSearchTxt() != ""
					&& userRegisterDTO.getAllocationType().equals("ALL")) {
				regInfo = airDropUserBonusRepository.getByNames(userRegisterDTO.getSearchTxt(),
						userRegisterDTO.getSearchTxt(), userRegisterDTO.getTokenStatus(), pageable);
			} else if (userRegisterDTO.getSearchTxt() != null && userRegisterDTO.getSearchTxt() != ""
					&& !userRegisterDTO.getAllocationType().equals("ALL")) {
				regInfo = airDropUserBonusRepository.getByNamesAndAllocation(userRegisterDTO.getSearchTxt(),
						userRegisterDTO.getSearchTxt(), userRegisterDTO.getTokenStatus(),
						userRegisterDTO.getAllocationType(), pageable);
			} else if (userRegisterDTO.getSearchTxt() == null && !userRegisterDTO.getAllocationType().equals("ALL")) {
				regInfo = airDropUserBonusRepository.findByTokenTransferStatusAndAllocationType(
						userRegisterDTO.getTokenStatus(), userRegisterDTO.getAllocationType(), pageable);
			} else if (userRegisterDTO.getSearchTxt() == null && userRegisterDTO.getAllocationType().equals("ALL")) {
				regInfo = airDropUserBonusRepository.findByTokenTransferStatus(userRegisterDTO.getTokenStatus(),
						pageable);
			}

			userRegisterDTO.setTotalPages(regInfo.getTotalPages());
			userRegisterDTO.setTotalElements(regInfo.getTotalElements());
			for (AirDropUserBonus info : regInfo) {
				UserRegisterDTO list = remcoUtils.airDropUserList(info);
				userList.add(list);
			}
			return userList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean userRegistration(List<UserRegisterDTO> userList) throws Exception {

		try {
			for (UserRegisterDTO userDTO : userList) {

				boolean isEmailExists = this.isAccountExistCheckByEmailId(userDTO.getEmailId());
				System.out.println("Email ID check");
				if (!isEmailExists) {

					userDTO.setWalletPassword(remcoUtils.randomPassword().toString());
					userDTO.setConfirmPassword(remcoUtils.randomPassword().toString());
					userDTO.setPassword(remcoUtils.randomPassword().toString());
					userDTO.setTwofaStatus(new Integer(1));

					userDTO.setMailFlag("AIRDROP");
					this.savePromoAccountInfo(userDTO);

				}

			}
			return true;
		}

		catch (Exception e) {
			System.out.println("Exception : " + e.toString());
			return false;
		}

	}

	@Override
	public boolean registerPromoUser(UserRegisterDTO userRegisterDTO) {

		try {
			String loginPassword = remcoUtils.randomPassword().toString();
			String walletPassword = remcoUtils.randomPassword().toString();

			userRegisterDTO.setPassword(loginPassword);
			userRegisterDTO.setWalletPassword(walletPassword);
			userRegisterDTO.setMailFlag("AIRDROP");
			userRegisterDTO.setPromoFlag("VTN");
			this.savePromoAccountInfo(userRegisterDTO);
			// emailNotificationService.sendEmailforAirdropRegistration(userRegisterDTO.getEmailId(),
			// "WELCOME TO REMCO WALLET", userRegisterDTO.getFirstName(),
			// userRegisterDTO.getPassword(), userRegisterDTO.getWalletAddress());
			String refId = EncryptDecrypt.encrypt(userRegisterDTO.getId().toString());
			userRegisterDTO.setReferralLink(env.getProperty("referral.link") + refId);

			return true;

		} catch (Exception e) {
			return false;

		}

	}

	@Override
	public boolean checkPromotionStatus(UserRegisterDTO userRegisterDTO) {

		PromoUsersInfo promo = promoUsersInfoRepository
				.findByEmailIdIgnoreCaseAndPromoType(userRegisterDTO.getEmailId(), userRegisterDTO.getReferSite());
		if (promo != null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean addPromo(UserRegisterDTO userRegisterDTO) {
		try {
			Config config = null;
			if (userRegisterDTO.getReferSite() != null && userRegisterDTO.getReferSite().equalsIgnoreCase("VTN")) {

				config = configInfoRepository.findConfigByConfigKey("vtnpromotoken");

			} else if (userRegisterDTO.getReferSite() != null
					&& userRegisterDTO.getReferSite().equalsIgnoreCase("QUIZ")) {

				config = configInfoRepository.findConfigByConfigKey("promotoken");

			}
			if (config != null) {
				userRegisterDTO.setPromoToken(Double.parseDouble(config.getConfigValue()));
			}
			PromoUsersInfo promo = new PromoUsersInfo();
			promo.setEmailId(userRegisterDTO.getEmailId());
			promo.setTokenCount(userRegisterDTO.getPromoToken());
			promo.setTokenStatus(0);
			promo.setAddress(userRegisterDTO.getUserAddress());
			promo.setCity(userRegisterDTO.getUserCity());
			promo.setCountry(userRegisterDTO.getUserCountry());
			promo.setFirstName(userRegisterDTO.getFirstName());
			promo.setLastName(userRegisterDTO.getLastName());
			promo.setMobileNo(userRegisterDTO.getMobileno());
			promo.setZipcode(userRegisterDTO.getUserZipcode());
			promo.setCreatedDate(new Date());
			promo.setPromoType(userRegisterDTO.getReferSite());
			promoUsersInfoRepository.save(promo);
			userRegisterDTO.setPromoId(promo.getId());

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<UserRegisterDTO> listPromoUsers(UserRegisterDTO userRegisterDTO) {
		try {

			Sort sort = new Sort(new Sort.Order(Direction.ASC, "id"));
			Pageable pageable = new PageRequest(userRegisterDTO.getPageNum(), userRegisterDTO.getPageSize(), sort);

			List<UserRegisterDTO> userList = new ArrayList<UserRegisterDTO>();

			Page<PromoUsersInfo> promoUserList = null;

			if (userRegisterDTO.getSearchTxt() != null && userRegisterDTO.getPromoFlag().equals("ALL")) {
				promoUserList = promoUsersInfoRepository.getByNames(userRegisterDTO.getSearchTxt(),
						userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(), pageable);
			} else if (userRegisterDTO.getSearchTxt() != null && !userRegisterDTO.getPromoFlag().equals("ALL")) {
				promoUserList = promoUsersInfoRepository.getByNamesAndPromoType(userRegisterDTO.getSearchTxt(),
						userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(), userRegisterDTO.getPromoFlag(),
						pageable);
			} else if (userRegisterDTO.getSearchTxt() == null && !userRegisterDTO.getPromoFlag().equals("ALL")) {
				promoUserList = promoUsersInfoRepository.findByPromoType(userRegisterDTO.getPromoFlag(), pageable);
			} else if (userRegisterDTO.getSearchTxt() == null && userRegisterDTO.getPromoFlag().equals("ALL")) {
				promoUserList = promoUsersInfoRepository.findAll(pageable);

			}
			for (PromoUsersInfo info : promoUserList) {
				UserRegisterDTO list = remcoUtils.promoList(info);
				userList.add(list);
			}
			userRegisterDTO.setTotalPages(promoUserList.getTotalPages());
			userRegisterDTO.setTotalElements(promoUserList.getTotalElements());
			return userList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unused")
	@Override
	public boolean savePromoAccountInfo(UserRegisterDTO userRegisterDTO) throws Exception {

		RegisterInfo registerInfo = new RegisterInfo();
		String psw = userRegisterDTO.getPassword();

		if (userRegisterDTO.getReferralId() == null) {

			String encryptPassword = EncryptDecrypt.encrypt(userRegisterDTO.getPassword());
			registerInfo.setPassword(encryptPassword);
			registerInfo.setCreatedDate(new Date());
			registerInfo.setEmailId(userRegisterDTO.getEmailId());
			registerInfo.setMobileNo(userRegisterDTO.getMobileno());
			registerInfo.setFirstName(userRegisterDTO.getFirstName());
			registerInfo.setLastName(userRegisterDTO.getLastName());
			registerInfo.setAddress(userRegisterDTO.getUserAddress());
			registerInfo.setCity(userRegisterDTO.getUserCity());
			registerInfo.setZipcode(userRegisterDTO.getUserZipcode());
			registerInfo.setCountry(userRegisterDTO.getUserCountry());
			registerInfo.setTwofaStatus(userRegisterDTO.getTwofaStatus());
			registerInfo.setPromoFlag("TRUE");
			registerInfo.setReferSite(userRegisterDTO.getReferSite());
			registerInfo.setActive(0);
			registerInfo.setReferralId(0);
			registerInfo.setKycStatus("0");
			registerInfoRepository.save(registerInfo);
			userRegisterDTO.setId(registerInfo.getId());
		} else {

			String refId = EncryptDecrypt.decrypt(userRegisterDTO.getReferralId());
			String encryptPassword = EncryptDecrypt.encrypt(userRegisterDTO.getPassword());
			registerInfo.setPassword(encryptPassword);
			registerInfo.setCreatedDate(new Date());
			registerInfo.setEmailId(userRegisterDTO.getEmailId());
			registerInfo.setMobileNo(userRegisterDTO.getMobileno());
			registerInfo.setFirstName(userRegisterDTO.getFirstName());
			registerInfo.setLastName(userRegisterDTO.getLastName());
			registerInfo.setAddress(userRegisterDTO.getUserAddress());
			registerInfo.setCity(userRegisterDTO.getUserCity());
			registerInfo.setZipcode(userRegisterDTO.getUserZipcode());
			registerInfo.setCountry(userRegisterDTO.getUserCountry());
			registerInfo.setTwofaStatus(userRegisterDTO.getTwofaStatus());
			registerInfo.setPromoFlag("TRUE");
			registerInfo.setReferSite(userRegisterDTO.getReferSite());
			registerInfo.setKycStatus("0");
			registerInfo.setActive(0);
			registerInfo.setReferralId(Integer.parseInt(refId));
			registerInfoRepository.save(registerInfo);
			userRegisterDTO.setId(registerInfo.getId());
		}

		System.out.println("Password with space from the UI is--------->" + userRegisterDTO.getPassword() + "---");
		int id = registerInfo.getId();
		String dynamicQRFolder = Integer.toString(id);
		if (registerInfo != null) {
			boolean status = isWalletCreate(userRegisterDTO);

			System.out.println("Wallet Creation Result------>" + status);
			if (status) {

				String WalletAddress = userRegisterDTO.getWalletAddress();
				String WalletPassword = userRegisterDTO.getWalletPassword();
				String encryptwalletAddress = EncryptDecrypt.encrypt(WalletAddress);
				String encryptwalletPassword = EncryptDecrypt.encrypt(WalletPassword);
				registerInfo.setWalletAddress(encryptwalletAddress);
				registerInfo.setWalletPassword(encryptwalletPassword);

				Config config = configInfoRepository.findConfigByConfigKey("walletfile");

				String walletAddr = remcoUtils.getWalletAddress(config.getConfigValue(),
						userRegisterDTO.getWalletAddress());
				userRegisterDTO.setWalletAddress(walletAddr);
				registerInfo.setWalletFile(walletAddr);

				String qrFileLocation = null;

				registerInfoRepository.save(registerInfo);
				if (userRegisterDTO.getReferralId() != null && userRegisterDTO.getReferralId() != "") {

					String refId2 = EncryptDecrypt.decrypt(userRegisterDTO.getReferralId());

					RegisterInfo regInfo = registerInfoRepository.findOne(Integer.parseInt(refId2));
					if (regInfo != null) {
						UserReferralInfo referralInfo = userReferralInfoRepository
								.findByUserEmailAndNewuserEmail(regInfo.getEmailId(), userRegisterDTO.getEmailId());
						if (referralInfo != null) {
							referralInfo.setStatus(1);
							referralInfo.setRegisteredDate(new Date());
							userReferralInfoRepository.save(referralInfo);
						}
						boolean isEmailSent = emailNotificationService.sendEmailforReferralRegister(
								regInfo.getEmailId(), "Referral Info from REMCO !", regInfo.getFirstName(),
								userRegisterDTO.getEmailId());
						if (!isEmailSent) {
							boolean isEmail = emailNotificationService.sendEmailforMailError(
									env.getProperty("admin1.email"), "Email Alert Info from REMCO", "");
						}
					}
				}
				boolean isEmailSent;
				if (userRegisterDTO.getMailFlag() == "AIRDROP") {
					isEmailSent = emailNotificationService.sendEmailforAirdropRegistration(userRegisterDTO.getEmailId(),
							"WELCOME TO THE REMCO WALLET !", registerInfo.getFirstName(), userRegisterDTO.getPassword(),
							walletAddr);

				} else {
					isEmailSent = emailNotificationService.sendEmailforRegistration(userRegisterDTO.getEmailId(),
							"WELCOME TO THE REMCO WALLET !", registerInfo.getFirstName(),
							registerInfo.getFirstName() + " " + registerInfo.getLastName(), walletAddr);

				}
				if (!isEmailSent) {
					boolean isEmail = emailNotificationService.sendEmailforMailError(env.getProperty("admin1.email"),
							"Email Alert Info from REMCO", "");
				}
				return true;

			}
			registerInfoRepository.delete(registerInfo.getId());
			return false;
		}

		return false;
	}

	@Override
	public boolean verifyAccessTokenForRegistration(UserRegisterDTO userRegisterDTO) {
		try {

			String url = env.getProperty("vtn.register.verify.url");
			URL object = new URL(url);

			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestMethod("POST");

			JSONObject reqbody = new JSONObject();

			System.out.println("Label" + userRegisterDTO.getEmailId());

			reqbody.put("accessToken", userRegisterDTO.getAccessToken());
			reqbody.put("firstName", userRegisterDTO.getFirstName());
			reqbody.put("lastName", userRegisterDTO.getLastName());
			reqbody.put("emailId", userRegisterDTO.getEmailId());
			reqbody.put("mobileNo", userRegisterDTO.getMobileno());
			reqbody.put("userAddress", userRegisterDTO.getUserAddress());
			reqbody.put("userCity", userRegisterDTO.getUserCity());
			reqbody.put("userZipcode", userRegisterDTO.getUserZipcode());
			reqbody.put("userCountry", userRegisterDTO.getUserCountry());
			reqbody.put("hashSecret", userRegisterDTO.getHashSecret());

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(reqbody.toString());
			wr.flush();

			System.out.println("Request body----->" + reqbody.toString());
			StringBuilder sb = new StringBuilder();
			int HttpResult = con.getResponseCode();

			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				System.out.println("::::::::::" + sb.toString());
				JSONObject json = new JSONObject(sb.toString());

				// BitcoinCashInfo bitcoinCashInfo = new BitcoinCashInfo();

				System.out.println("myResponse" + json.get("status"));
				System.out.println("myResponse" + json.get("message"));

				return true;

				// Ecrypting the Wallet Details :

			} else {
				System.out.println(con.getResponseMessage());
				System.out.println("Not Getting Response");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception is------>" + e.toString());
		}

		return false;
	}

	@Override
	public boolean verifyAccessTokenForBalance(UserRegisterDTO userRegisterDTO) {

		try {

			String url = env.getProperty("vtn.user.verify.url");
			URL object = new URL(url);

			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestMethod("POST");

			JSONObject reqbody = new JSONObject();

			System.out.println("Label" + userRegisterDTO.getEmailId());

			reqbody.put("accessToken", userRegisterDTO.getAccessToken());
			reqbody.put("id", userRegisterDTO.getId());
			reqbody.put("emailId", userRegisterDTO.getEmailId());
			reqbody.put("hashSecret", userRegisterDTO.getHashSecret());

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(reqbody.toString());
			wr.flush();

			StringBuilder sb = new StringBuilder();
			int HttpResult = con.getResponseCode();

			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				JSONObject json = new JSONObject(sb.toString());

				// BitcoinCashInfo bitcoinCashInfo = new BitcoinCashInfo();

				System.out.println("myResponse" + json.get("status"));
				System.out.println("myResponse" + json.get("message"));

				return true;

				// Ecrypting the Wallet Details :

			} else {
				System.out.println("HttpResult : " + HttpResult);
				System.out.println(con.getResponseMessage());
				System.out.println("Not Getting Response");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception is------>" + e.toString());
		}

		return false;
	}

	@Override
	public List<UserRegisterDTO> listAirdropUsers(UserRegisterDTO userRegisterDTO) {
		try {

			List<UserRegisterDTO> userList = new ArrayList<UserRegisterDTO>();

			List<AirDropUserBonus> regInfo = (List<AirDropUserBonus>) airDropUserBonusRepository.findAll();

			for (AirDropUserBonus info : regInfo) {
				if (info.getAllocationType().equals("AIRDROP")) {

				} else {
					UserRegisterDTO list = remcoUtils.airDropUserList(info);
					userList.add(list);
				}

			}
			return userList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Integer checkUserExistWithEmailAndMobile(UserRegisterDTO userRegisterDTO) {

		RegisterInfo emailInfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());

		List<RegisterInfo> mobileInfo = registerInfoRepository.findByMobileNo(userRegisterDTO.getMobileno());

		if (emailInfo != null && mobileInfo.size() == 0) {

			PromoUsersInfo promoEmail = promoUsersInfoRepository
					.findByEmailIdIgnoreCaseAndPromoType(userRegisterDTO.getEmailId(), userRegisterDTO.getPromoFlag());

			PromoUsersInfo promoMobile = promoUsersInfoRepository
					.findByMobileNoAndPromoType(userRegisterDTO.getMobileno(), userRegisterDTO.getPromoFlag());

			if (promoEmail != null || promoMobile != null) {
				userRegisterDTO.setMessage(
						"Email ID and Mobile No. are already exist. You have already availed earned your tokens! ");
				return 2;
			} else {
				userRegisterDTO.setMessage("Email ID already exist. You have not earned tokens!");
				return 3;
			}
		}

		if (emailInfo == null && mobileInfo.size() != 0) {

			PromoUsersInfo promoEmail = promoUsersInfoRepository
					.findByEmailIdIgnoreCaseAndPromoType(userRegisterDTO.getEmailId(), userRegisterDTO.getPromoFlag());

			PromoUsersInfo promoMobile = promoUsersInfoRepository
					.findByMobileNoAndPromoType(userRegisterDTO.getMobileno(), userRegisterDTO.getPromoFlag());

			if (promoEmail != null || promoMobile != null) {
				userRegisterDTO.setMessage(
						"Email ID and Mobile No. are already exist. You have already availed earned your tokens! ");
				return 2;
			} else {
				userRegisterDTO
						.setMessage("Mobile number exist under a different email. Please use your existing email! ");
				return 4;
			}
		}

		if (emailInfo != null && mobileInfo.size() != 0) {

			PromoUsersInfo promoEmail = promoUsersInfoRepository
					.findByEmailIdIgnoreCaseAndPromoType(userRegisterDTO.getEmailId(), userRegisterDTO.getPromoFlag());

			PromoUsersInfo promoMobile = promoUsersInfoRepository
					.findByMobileNoAndPromoType(userRegisterDTO.getMobileno(), userRegisterDTO.getPromoFlag());

			if (promoEmail != null || promoMobile != null) {
				userRegisterDTO.setMessage(
						"Email ID and Mobile No. are already exist. You have already availed earned your tokens! ");
				return 2;
			} else {
				userRegisterDTO.setMessage("Please complete the form to Earn your free tokens! ");
				return 0;
			}
		} else {
			userRegisterDTO.setMessage(
					"Welcome to REMCO. You will be registered with our Remittancetoken.io site. Please complete the form to Earn your free tokens! ");
			return 1;

		}
	}

	public boolean isAccountExistCheckByEmailIdForRegistrationViaVtnOrQuiz(UserRegisterDTO userRegisterDTO) {
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId().trim());
		if(registerInfo != null) {
			if(registerInfo.getFirstName() != null || registerInfo.getLastName() != null || registerInfo.getCity() != null
					|| registerInfo.getCountry()!= null) {
				try {
					registerInfo.setFirstName(userRegisterDTO.getFirstName());
					registerInfo.setLastName(userRegisterDTO.getLastName());
					registerInfo.setAddress(userRegisterDTO.getUserAddress());
					registerInfo.setCity(userRegisterDTO.getUserCity());
					registerInfo.setCountry(userRegisterDTO.getUserCountry());
					if(registerInfo.getMobileNo() != null) {
						registerInfo.setMobileNo(userRegisterDTO.getMobileno());
					}
					registerInfo.setReferSite(userRegisterDTO.getReferSite());
					registerInfo.setZipcode(userRegisterDTO.getUserZipcode());
					registerInfoRepository.save(registerInfo);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}
}
