package com.remco.ico.serviceImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.web3j.crypto.CipherException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import com.remco.ico.DTO.KycDTO;
import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.UserRegisterDTO;
import com.remco.ico.model.EmailContentInfo;
import com.remco.ico.model.ICOTokenInfo;
import com.remco.ico.model.KycInfo;
import com.remco.ico.model.ReferralInfo;
import com.remco.ico.model.RegisterInfo;
import com.remco.ico.model.SaleDatesInfo;
import com.remco.ico.model.UserReferralInfo;
import com.remco.ico.repository.EmailContentInfoRepo;
import com.remco.ico.repository.ICOTokenInfoRepository;
import com.remco.ico.repository.KycInfoRepository;
import com.remco.ico.repository.ReferralInfoRepository;
import com.remco.ico.repository.RegisterInfoRepository;
import com.remco.ico.repository.SaleDatesInfoRepository;
import com.remco.ico.repository.UserReferralInfoRepository;
import com.remco.ico.service.AdminService;
import com.remco.ico.service.EmailNotificationService;
import com.remco.ico.solidityHandler.SolidityHandler;
import com.remco.ico.utils.RemcoUtils;

@Service
public class AdminServiceImpl implements AdminService {

	static final Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);

	// private final Web3j web3j = Web3j.build(new
	// HttpService("https://rinkeby.infura.io/"));

	@SuppressWarnings("unused")
	private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io"));

	@Autowired
	private RegisterInfoRepository registerInfoRepository;
	@Autowired
	private KycInfoRepository kycInfoRepository;
	@Autowired
	private RemcoUtils remcoUtils;
	@Autowired
	private Environment env;
	@Autowired
	private EmailNotificationService emailNotificationService;
	@Autowired
	private SolidityHandler solidityHandler;
	@Autowired
	private ICOTokenInfoRepository icoTokenInfoRepository;
	@Autowired
	private SaleDatesInfoRepository saleDatesInfoRepository;
	@Autowired
	private ReferralInfoRepository referralInfoRepository;
	@Autowired
	private UserReferralInfoRepository userReferralInfoRepository;
	@Autowired
	private EmailContentInfoRepo emailContentInfoRepo;

	@Override
	public boolean validateAdmin(String emailId) {

		RegisterInfo UserInfos = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(emailId);

		try {

			System.out.println("User type is-------->" + UserInfos.getUserType());
			if (UserInfos != null && UserInfos.getUserType() == 1) {

				return true;
			}
		} catch (Exception e) {

			return false;
		}

		return false;
	}

	@Override
	public List<KycDTO> kycList(KycDTO kycDTO) {

		List<KycDTO> kycList = new ArrayList<KycDTO>();
		Sort sort = new Sort(new Sort.Order(Direction.ASC, "id"));
		Pageable pageable = new PageRequest(kycDTO.getPageNum(), kycDTO.getPageSize(), sort);
		// Page<KycInfo> kycInfo = kycInfoRepository.findAll(pageable);
		Page<KycInfo> kycInfo = null;

		if (kycDTO.getSearchTxt() != null && kycDTO.getSearchTxt() != "") {
			if (kycDTO.getKycStatus().equals("-1")) {
				kycInfo = kycInfoRepository.getByNames(kycDTO.getSearchTxt(), kycDTO.getSearchTxt(),
						kycDTO.getSearchTxt(), kycDTO.getSearchTxt(), "-1", "-1", "-1", pageable);
			}
			if (kycDTO.getKycStatus().equals("1")) {
				kycInfo = kycInfoRepository.getByNames(kycDTO.getSearchTxt(), kycDTO.getSearchTxt(),
						kycDTO.getSearchTxt(), kycDTO.getSearchTxt(), "1", "1", "1", pageable);
			}
			if (kycDTO.getKycStatus().equals("0")) {
				kycInfo = kycInfoRepository.getByNames(kycDTO.getSearchTxt(), kycDTO.getSearchTxt(),
						kycDTO.getSearchTxt(), kycDTO.getSearchTxt(), "0", "0", "0", pageable);
			}
			if (kycDTO.getKycStatus().equals("2")) {
				kycInfo = kycInfoRepository.getByNames(kycDTO.getSearchTxt(), kycDTO.getSearchTxt(),
						kycDTO.getSearchTxt(), kycDTO.getSearchTxt(), "1", "0", "-1", pageable);
			}
		} else {

			if (kycDTO.getKycStatus().equals("-1")) {
				kycInfo = kycInfoRepository.findRegisterInfoByKycStatus("-1", pageable);
			}
			if (kycDTO.getKycStatus().equals("1")) {
				kycInfo = kycInfoRepository.findRegisterInfoByKycStatus("1", pageable);

			}
			if (kycDTO.getKycStatus().equals("0")) {
				kycInfo = kycInfoRepository.findRegisterInfoByKycStatus("0", pageable);

			}
			if (kycDTO.getKycStatus().equals("2")) {
				kycInfo = kycInfoRepository.findAll(pageable);
			}
		}

		kycDTO.setTotalPages(kycInfo.getTotalPages());
		kycDTO.setTotalElements(kycInfo.getTotalElements());
		for (KycInfo info : kycInfo) {
			KycDTO list = remcoUtils.listKYC(info);
			kycList.add(list);
		}
		return kycList;
	}

	@Override
	public KycDTO getKycDetails(KycDTO kycDTO) {

		KycInfo kycInfo = kycInfoRepository.findKycInfoById(kycDTO.getId());

		System.out.println("USer KYC ID------------->" + kycDTO.getId());
		if (kycInfo != null) {
			kycDTO.setFirstName(kycInfo.getFirstName());
			kycDTO.setMiddleName(kycInfo.getMiddleName());
			kycDTO.setLastName(kycInfo.getLastName());
			kycDTO.setEmailId(kycInfo.getEmailId());
			kycDTO.setEntityName(kycInfo.getEntityName());
			kycDTO.setEntityType(kycInfo.getInvEntityType());
			kycDTO.setIdentityType(kycInfo.getIdentityType());
			kycDTO.setIdentityNo(kycInfo.getIdentityNo());
			kycDTO.setCountryOfIssue(kycInfo.getCountryOfIssue());
			kycDTO.setDateOfIssue(kycInfo.getDateOfIssue());
			kycDTO.setDateOfExpiry(kycInfo.getDateOfExpiry());
			kycDTO.setResState(kycInfo.getResState());
			kycDTO.setResCountry(kycInfo.getResCountry());
			kycDTO.setSaleType(kycInfo.getSaleType());
			kycDTO.setHowAccredited(kycInfo.getHowAccredited());
			// kycDTO.setInvAccreditation(kycInfo);
			kycDTO.setIdPath(env.getProperty("apache.server") + kycInfo.getIdPath());
			kycDTO.setIsVesting(kycInfo.getIsVesting());
			kycDTO.setSchoolName(kycInfo.getSchoolName());
			kycDTO.setId(kycInfo.getId());
			kycDTO.setKycStatus(kycInfo.getKycStatus());
			kycDTO.setReasonForRejection(kycInfo.getReasonForRejection());

			return kycDTO;
		}

		return null;
	}

	@Override
	public String validateRequestStatus(KycDTO kycDTO) {

		KycInfo kycInfo = kycInfoRepository.findKycInfoById(kycDTO.getId());
		if (kycInfo != null) {
			if (kycInfo.getKycStatus().equals("1")) {
				kycDTO.setMessage("User Kyc Request has been Already Approved");
				return "User Kyc Request has been Already Approved";
			} else if (kycInfo.getKycStatus().equals("-1")) {
				kycDTO.setMessage("User Kyc Request has been Already Rejected");
				return "User Kyc Request has been Already Rejected";
			} else if (kycInfo.getKycStatus().equals("0")) {
				kycDTO.setMessage("Success");
				return "success";
			}
		} else {
			return null;
		}
		return null;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean updateKYC(KycDTO kycDTO) {

		KycInfo kycInfo = kycInfoRepository.findKycInfoById(kycDTO.getId());

		if (kycInfo != null) {
			kycInfo.setKycStatus(kycDTO.getKycStatus());
			if (kycDTO.getKycStatus().equals("-1")) {
				kycInfo.setReasonForRejection(kycDTO.getReasonForRejection());
			}
			kycInfoRepository.save(kycInfo);

			if (kycDTO.getKycStatus().equals("1")) {
				kycDTO.setMessage("User KYC Details has been Approved Successfully!");
			} else if (kycDTO.getKycStatus().equals("-1")) {
				kycDTO.setMessage("User KYC Details has been Rejected!");
			}

			RegisterInfo regInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(kycInfo.getEmailId());
			if (regInfo != null) {
				regInfo.setKycStatus(kycDTO.getKycStatus());
				registerInfoRepository.save(regInfo);
			}
			boolean isEmailSent;
			if (kycDTO.getKycStatus().equals("-1")) {
				isEmailSent = emailNotificationService.sendEmailforKYC(kycInfo.getEmailId(), "KYC info from REMCO Coin",
						kycDTO.getKycStatus(), kycInfo.getFirstName() + " " + kycInfo.getLastName(),
						kycDTO.getReasonForRejection());
			} else {
				isEmailSent = emailNotificationService.sendEmailforKYC(kycInfo.getEmailId(), "KYC info from REMCO Coin",
						kycDTO.getKycStatus(), kycInfo.getFirstName() + " " + kycInfo.getLastName(), "");
			}
			if (!isEmailSent) {
				boolean isEmail = emailNotificationService.sendEmailforMailError(env.getProperty("admin1.email"),
						"Email Alert Info from REMCO", "");
			}
			return true;

		}

		return false;
	}

	@Override
	public boolean tokenPurchase(TokenDTO tokenDTO) throws IOException, CipherException, Exception {

		boolean isValid = solidityHandler.tokenPurchase(tokenDTO);
		if (isValid) {
			return true;
		}
		return false;
	}

	@Override
	public List<TokenDTO> purchaseLists(TokenDTO tokenDTO) throws Exception {

		List<TokenDTO> isList = solidityHandler.purchaseLists(tokenDTO);
		if (isList != null) {
			return isList;
		}
		return null;
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public String validateTokenBalanceForPurchase(TokenDTO tokenDTO) throws Exception {
		// Config configInfo = configInfoRepository.findConfigByConfigKey("walletFile");

		// RegisterInfo registerInfo =
		// registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());

		// String walletPassword = registerInfo.getWalletPassword();

		// String dwalletPassword = EncryptDecrypt.decrypt(walletPassword);
		// LOG.info("Decrypted wallet Password" + dwalletPassword);
		// String fromAddress = remcoUtils.getWalletAddress(configInfo.getConfigValue(),
		// EncryptDecrypt.decrypt(registerInfo.getWalletAddress()));
		/*
		 * Credentials credentials =
		 * WalletUtils.loadCredentials(env.getProperty("credentials.password"),
		 * env.getProperty("credentials.address")); RemcoToken assetToken =
		 * RemcoToken.load(this.env.getProperty("token.address"), web3j, credentials,
		 * Contract.GAS_PRICE, Contract.GAS_LIMIT);
		 * 
		 * BigInteger amount =
		 * assetToken.balanceOf(env.getProperty("main.address")).send();
		 */

		RegisterInfo regInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
		DecimalFormat df = new DecimalFormat("0.00000000");

		ICOTokenInfo icoTokenInfo = icoTokenInfoRepository.findIcoTokenInfoByIcoKey("ICOToken");
		Double crowdsaleBal = icoTokenInfo.getIcoAvail().doubleValue();

		Double token = tokenDTO.getTokenAmount();
		if (BigInteger.ZERO.equals(token)) {
			return "Please enter valid token";
		}

		SaleDatesInfo saleDatesInfo = saleDatesInfoRepository.findOne(1);
		if (saleDatesInfo != null)

		{
			Date currDate = new Date();

			if (currDate.before(saleDatesInfo.getPrivateStartDate())) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				String strDate = formatter.format(saleDatesInfo.getPrivateStartDate());
				return "Pre-ICO is Scheduled to Start on " + strDate;
			}

			if (currDate.after(saleDatesInfo.getPublicEndDate())) {
				return "ICO has Ended";
			}

		}
		if (crowdsaleBal <= 0) {

			if (regInfo.getUserType() == 1) {
				return "SOLD OUT!";
			}

			else if (regInfo.getUserType() == 0) {
				return "Insufficient Token Balance!";
			}

		} else {
			int count = crowdsaleBal.compareTo(tokenDTO.getTokenAmount());

			BigDecimal bd = new BigDecimal(df.format(crowdsaleBal));
			crowdsaleBal = bd.doubleValue();
			return count == -1 ? "We have only " + BigDecimal.valueOf(crowdsaleBal).toPlainString() + " tokens left"
					: "success";
		}
		return "failure";
	}

	@Override
	public boolean tokenPurchaseUsingVTN(TokenDTO tokenDTO) throws Exception {

		boolean isValid = solidityHandler.purchaseTokenUsingVTN(tokenDTO);
		if (isValid) {
			return true;
		}
		return false;
	}

	@Override
	public List<TokenDTO> referralPurchaseList(TokenDTO tokenDTO) {
		List<TokenDTO> list = new ArrayList<TokenDTO>();
		Sort sort = new Sort(new Sort.Order(Direction.ASC, "id"));
		Pageable pageable = new PageRequest(tokenDTO.getPageNum(), tokenDTO.getPageSize(), sort);
		Page<ReferralInfo> referralInfo = null;
		if (tokenDTO.getReferralStatus() == 1 || tokenDTO.getReferralStatus() == 0) {
			referralInfo = referralInfoRepository.findReferralInfoByReferralEmailIdAndStatus(tokenDTO.getEmailId(),
					tokenDTO.getReferralStatus(), pageable);
		} else

		{
			referralInfo = referralInfoRepository.findReferralInfoByReferralEmailId(tokenDTO.getEmailId(), pageable);
		}
		tokenDTO.setTotalPages(referralInfo.getTotalPages());
		tokenDTO.setTotalElements(referralInfo.getTotalElements());
		for (ReferralInfo referralList : referralInfo) {
			TokenDTO tokenDTOs = new TokenDTO();
			tokenDTOs.setReferralCommission(referralList.getReferralTokens());
			tokenDTOs.setPurchasedDate(referralList.getReferralPurchaseDate());
			tokenDTOs.setTransferStatus(referralList.getStatus().toString());
			tokenDTOs.setEmailId(referralList.getEmailId());

			RegisterInfo registerInfo = registerInfoRepository
					.findRegisterInfoByEmailIdIgnoreCase(referralList.getEmailId());
			tokenDTOs.setFirstName(registerInfo.getFirstName());
			tokenDTOs.setLastName(registerInfo.getLastName());

			UserReferralInfo userReferralInfo = userReferralInfoRepository
					.findByUserEmailAndNewuserEmail(referralList.getReferralEmailId(), referralList.getEmailId());
			tokenDTOs.setReferredDate(userReferralInfo.getReferredDate());
			tokenDTOs.setRegisteredDate(userReferralInfo.getRegisteredDate());
			list.add(tokenDTOs);
		}

		return list;
	}

	@Override
	public boolean addUsersToEmailList(UserRegisterDTO userRegisterDTO, TokenDTO tokenDTO) {
		int count = 0;
		EmailContentInfo emailContentInfo = new EmailContentInfo();
		emailContentInfo.setDate(new Date());
		emailContentInfo.setSubject(userRegisterDTO.getSubject());
		emailContentInfo.setMessage(userRegisterDTO.getMessage());
		emailContentInfoRepo.save(emailContentInfo);

		List<RegisterInfo> userlist = null;
		if (userRegisterDTO.getSearchTxt() != null && userRegisterDTO.getSearchTxt() != "") {
			if (userRegisterDTO.getStatus() == -1) {
				userlist = registerInfoRepository.filterByNamesOrEmailOrMobileNo(userRegisterDTO.getSearchTxt(),
						userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(),
						-1, 0, 1);
			}
			if (userRegisterDTO.getStatus() == 1) {
				userlist = registerInfoRepository.filterByNamesOrEmailOrMobileNo(userRegisterDTO.getSearchTxt(),
						userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(),
						1, 1, 1);

			}
			if (userRegisterDTO.getStatus() == 0) {
				userlist = registerInfoRepository.filterByNamesOrEmailOrMobileNo(userRegisterDTO.getSearchTxt(),
						userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(), userRegisterDTO.getSearchTxt(),
						0, 0, 0);
			}

		} else {
			userlist = registerInfoRepository.findAll();
		}
		for (RegisterInfo listOfUsers : userlist) {
			if (listOfUsers != null) {
				listOfUsers.setEmailStatus(emailContentInfo.getId());
				registerInfoRepository.save(listOfUsers);
				count++;
			}
		}
		tokenDTO.setUserCount(count);
		return true;
	}
	
	@Override
	public boolean triggerEmail() {
		System.out.println("Entered");
		List<EmailContentInfo> emailContentInfo = emailContentInfoRepo.findByStatus(0);
		for (EmailContentInfo listDetails : emailContentInfo) {
			if(listDetails.getStatus() == 0) {
				String message = urlDecode(listDetails.getMessage());
				if(message != null) {
					
					List<RegisterInfo> registerInfos = registerInfoRepository.findByEmailStatus(listDetails.getId());
					for(RegisterInfo listOfUsers : registerInfos) {
						boolean isEmailSent = emailNotificationService.sendEmailToUsers(listOfUsers.getFirstName(), listOfUsers.getLastName(), listOfUsers.getMobileNo(), listOfUsers.getEmailId(), listDetails.getSubject(), message);
						if(!isEmailSent) {
							System.out.println("Email Sent");
						} else {
							System.out.println("Email Not Sent");
						}	
					}
					listDetails.setStatus(1);
					emailContentInfoRepo.save(listDetails);
				}
			}
		}
		return true;
	}

	public String urlDecode(String message) {
		try {
			if(message != null) {
				String decodeURL = URLDecoder.decode(message, "UTF-8");
				System.out.println("URL decode : " + decodeURL);
				return decodeURL;
			}
			return null;
		} catch (UnsupportedEncodingException e) {
			return "Issue while decoding" + e.getMessage();
		}
	}

}
