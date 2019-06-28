package com.remco.ico.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.remco.ico.DTO.KycDTO;
import com.remco.ico.DTO.LoginDTO;
import com.remco.ico.DTO.SaleTypeDTO;
import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.TokenTransferDTO;
import com.remco.ico.DTO.UserRegisterDTO;
import com.remco.ico.model.AirDropUserBonus;
import com.remco.ico.model.AuthorizationInfo;
import com.remco.ico.model.BitcoinCashInfo;
import com.remco.ico.model.BitcoinInfo;
import com.remco.ico.model.Config;
import com.remco.ico.model.GasFeeInfo;
import com.remco.ico.model.KycInfo;
import com.remco.ico.model.PromoUsersInfo;
import com.remco.ico.model.PurchaseTokenInfo;
import com.remco.ico.model.RegisterInfo;
import com.remco.ico.model.TokenDiscountInfo;
import com.remco.ico.model.TokenRateInfo;
import com.remco.ico.model.VTNPaymentInfo;
import com.remco.ico.repository.AirDropUserBonusRepository;
import com.remco.ico.repository.AuthorizationInfoRepository;
import com.remco.ico.repository.BitcoinCashInfoRepository;
import com.remco.ico.repository.BitcoinInfoRepository;
import com.remco.ico.repository.ConfigInfoRepository;
import com.remco.ico.repository.GasFeeInfoRepository;
import com.remco.ico.repository.PurchaseTokenInfoRepository;
import com.remco.ico.repository.RegisterInfoRepository;
import com.remco.ico.repository.TokenRateInfoRepository;
import com.remco.ico.repository.VTNPaymentInfoRepository;
import com.remco.ico.service.EmailNotificationService;
import com.remco.ico.service.TokenUserService;
import com.remco.ico.solidityHandler.SolidityHandler;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@Service
public class RemcoUtils {

	static final Logger LOG = LoggerFactory.getLogger(RemcoUtils.class);

	static final String regex = "[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

	static final long ONE_MINUTE_IN_MILLIS = 60000;// millisecs
	

	@Autowired
	private RegisterInfoRepository registerInfoRepository;
	@Autowired
	private AuthorizationInfoRepository authorizationInfoRepository;
	@Autowired
	private EmailNotificationService emailNotificationService;
	@Autowired
	private Environment env;
	@Autowired
	private TokenUserService tokenUserService;
	@Autowired
	private SolidityHandler solidityHandler;
	@Autowired
	private TokenRateInfoRepository tokenRateInfoRepository;
	@Autowired
	private BitcoinCashUtils bitcoinCashUtils;
	@Autowired
	private CurrencyRateUtils currencyRateUtils;
	@Autowired
	private BitcoinUtils bitcoinUtils;
	@Autowired
	private BitcoinCashInfoRepository bitcoinCashInfoRepository;
	@Autowired
	private BitcoinInfoRepository bitcoinInfoRepository;
	@Autowired
	private VTNPaymentInfoRepository vtnPaymentInfoRepository;
	@Autowired
	private GasFeeInfoRepository gasFeeInfoRepository;
	@Autowired
	private PurchaseTokenInfoRepository purchaseTokenInfoRepository;
	@Autowired
	private ConfigInfoRepository configInfoRepository;
	@Autowired
	private AirDropUserBonusRepository airDropUserBonusRepository;


	public boolean validateRegistrationParam(UserRegisterDTO userRegisterDTO) {

		if (userRegisterDTO.getEmailId() != null && StringUtils.isNotBlank(userRegisterDTO.getEmailId())
				&& userRegisterDTO.getPassword() != null && StringUtils.isNotBlank(userRegisterDTO.getPassword())
				&& userRegisterDTO.getConfirmPassword() != null
				&& StringUtils.isNotBlank(userRegisterDTO.getConfirmPassword())
				&& userRegisterDTO.getWalletPassword() != null
				&& StringUtils.isNotBlank(userRegisterDTO.getWalletPassword()) && userRegisterDTO.getFirstName() != null
				&& StringUtils.isNotBlank(userRegisterDTO.getFirstName()) && userRegisterDTO.getLastName() != null
				&& StringUtils.isNotBlank(userRegisterDTO.getLastName()) && userRegisterDTO.getUserAddress() != null
				&& StringUtils.isNotBlank(userRegisterDTO.getUserAddress()) && userRegisterDTO.getUserCity() != null
				&& StringUtils.isNotBlank(userRegisterDTO.getUserCity()) && userRegisterDTO.getUserCountry() != null
				&& StringUtils.isNotBlank(userRegisterDTO.getUserCountry())
				&& userRegisterDTO.getTwofaStatus() != null
				&& StringUtils.isNotBlank(userRegisterDTO.getTwofaStatus().toString())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateEmail(String emailId) {
		emailId = emailId.replaceFirst("^ *", "");
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(emailId);
		LOG.info(emailId + " : " + matcher.matches());
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateConfirmPassword(UserRegisterDTO userRegisterDTO) {
		if (userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
			return true;
		}
		return false;
	}

	public boolean validateWalletPassword(UserRegisterDTO userRegisterDTO) {
		if (userRegisterDTO.getWalletPassword().equals(userRegisterDTO.getConfirmWalletPassword())) {
			return true;
		}
		return false;
	}

	public String getWalletAddress(String fileLocation, String fileName)
			throws FileNotFoundException, IOException, ParseException {
		fileLocation = fileLocation.replace("/", "\\");
		LOG.info("WalletCreated : " + fileLocation);
		LOG.info("FileName : " + fileName);

		JSONParser parser = new JSONParser();
		Object object;
		object = parser.parse(new FileReader(fileLocation + "//" + fileName));
		JSONObject jsonObject = (JSONObject) object;
		String address = "0x" + (String) jsonObject.get("address");
		LOG.info("FileName" + fileName);
		LOG.info("Wallet Address" + address);
		return address;
	}

	public boolean validateLoginParam(UserRegisterDTO userRegisterDTO) {

		if (userRegisterDTO.getEmailId() != null && StringUtils.isNotBlank(userRegisterDTO.getEmailId())
				&& userRegisterDTO.getPassword() != null && StringUtils.isNotBlank(userRegisterDTO.getPassword())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isAccountExistCheckByEmailId(UserRegisterDTO userRegisterDTO) {

		RegisterInfo registerInfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());
		if (registerInfo != null) {
			userRegisterDTO.setUserType(registerInfo.getUserType());
			userRegisterDTO.setUserName(registerInfo.getFirstName()+" "+registerInfo.getLastName());
			userRegisterDTO.setTwofaStatus(registerInfo.getTwofaStatus());
			return true;
		}
		return false;
	}

	public boolean validatePasswordForLogin(UserRegisterDTO userRegisterDTO) throws Exception {

		String email = userRegisterDTO.getEmailId();

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(email);

		String password = EncryptDecrypt.decrypt(registerInfo.getPassword());
		LOG.info(password);
		if (password.equals(userRegisterDTO.getPassword())) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	public String keyGenerater(UserRegisterDTO userRegisterDTO) {

		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		final GoogleAuthenticatorKey key = gAuth.createCredentials();
		String secretKey = key.getKey();

		Integer code = gAuth.getTotpPassword(secretKey);
		userRegisterDTO.setSecuredKey(code);

		try {
			RegisterInfo registerInfo = registerInfoRepository
					.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());
			if (registerInfo != null) {
				registerInfo.setSecuredKey(code);
				registerInfo.setKeyDate(new Date());
				registerInfoRepository.save(registerInfo);

			}
			 boolean isEmailSent = emailNotificationService.sendEmailforOTP(userRegisterDTO.getEmailId(),
				"Login info from Remco Tokens", userRegisterDTO.getSecuredKey().toString(), userRegisterDTO.getUserName(), userRegisterDTO.getIpAddress());
				System.out.println("Email sent Status------------->"+isEmailSent);
				if(!isEmailSent)
				{
					boolean isEmail = emailNotificationService.sendEmailforMailError(env.getProperty("admin1.email"), "Email Alert Info from REMCO", "");
				}

			return code.toString();
		} catch (Exception e) {
			return null;
		}

	}

	public boolean validatesecureKeyParam(UserRegisterDTO userRegisterDTO) {
		if (userRegisterDTO.getEmailId() != null && StringUtils.isNotBlank(userRegisterDTO.getEmailId())
				&& userRegisterDTO.getSecuredKey() != null
				&& StringUtils.isNotBlank(userRegisterDTO.getSecuredKey().toString())) {
			return true;
		}
		return false;
	}

	public boolean saveAuthenticationDetails(UserRegisterDTO userRegisterDTO) {
		String token = UUID.randomUUID().toString();
		String mailId = userRegisterDTO.getEmailId();
		if (token != null && mailId != null) {
			AuthorizationInfo authorizationInfo = authorizationInfoRepository.findByEmailIdIgnoreCase(mailId);
			if (authorizationInfo == null) {
				System.out.println(authorizationInfo);
				AuthorizationInfo authorizationInfo2 = new AuthorizationInfo();
				authorizationInfo2.setAuthToken(token);
				authorizationInfo2.setEmailId(mailId);
				authorizationInfo2.setCreatedDate(new Date());
				authorizationInfo2.setStatus("Active");
				Calendar date = Calendar.getInstance();
				long t = date.getTimeInMillis();
				Date afterAddingThreeMins = new Date(t + (5 * ONE_MINUTE_IN_MILLIS));
				authorizationInfo2.setExpiryDate(afterAddingThreeMins);
				authorizationInfoRepository.save(authorizationInfo2);
				return true;
			} else {
				System.out.println(authorizationInfo);
				authorizationInfo.setAuthToken(token);
				authorizationInfo.setEmailId(mailId);
				authorizationInfo.setCreatedDate(new Date());
				authorizationInfo.setStatus("Active");
				Calendar date = Calendar.getInstance();
				long t = date.getTimeInMillis();
				Date afterAddingThreeMins = new Date(t + (5 * ONE_MINUTE_IN_MILLIS));
				authorizationInfo.setExpiryDate(afterAddingThreeMins);
				authorizationInfoRepository.save(authorizationInfo);
				return true;
			}
		}
		return false;
	}

	public boolean isValidActivateParam(LoginDTO loginDTO) {
		if (loginDTO.getEmailId() != null && StringUtils.isNotBlank(loginDTO.getEmailId())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateForgotPwdParam(UserRegisterDTO userRegisterDTO) {
		if (userRegisterDTO.getEmailId() != null && StringUtils.isNotBlank(userRegisterDTO.getEmailId())) {
			return true;
		}
		return false;
	}

	public boolean validateEmailActivation(String emailId, UserRegisterDTO userRegisterDTO) throws Exception {
		
		RegisterInfo regInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(emailId);

		if (regInfo != null) {
			
			userRegisterDTO.setEmailId(emailId);
			if (regInfo.getActive() == 1) {
				return true;
			} else {
				userRegisterDTO.setMessage("Email Verification is Still Pending!");
				return false;
			}

		} else {
			userRegisterDTO.setMessage("Email ID does Not Exist!");
			return false;
		}
	}
	
public boolean validateEmailActivationForLink(String emailId, UserRegisterDTO userRegisterDTO) throws Exception {
		
		String email = emailId.replaceAll("\\s", "+");
		email =  EncryptDecrypt.decrypt(email);
		RegisterInfo regInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(email);

		if (regInfo != null) {
			
			userRegisterDTO.setEmailId(email);
			if (regInfo.getActive() == 1) {
				return true;
			} else {
				userRegisterDTO.setMessage("Email Verification is Still Pending!");
				return false;
			}

		} else {
			userRegisterDTO.setMessage("Email ID does Not Exist!");
			return false;
		}
	}

	public boolean isValidResetPwdParam(UserRegisterDTO userregisterDTO) {
		if (userregisterDTO.getNewPassword() != null && StringUtils.isNotBlank(userregisterDTO.getNewPassword())
				&& userregisterDTO.getConfirmPassword() != null
				&& StringUtils.isNotBlank(userregisterDTO.getConfirmPassword()) && userregisterDTO.getEmailId() != null
				&& StringUtils.isNotBlank(userregisterDTO.getEmailId())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateResetConfirmPassword(UserRegisterDTO userRegisterDTO) {
		if (userRegisterDTO.getNewPassword().equals(userRegisterDTO.getConfirmPassword())) {
			return true;
		}
		return false;
	}

	public boolean validateResetPassword(UserRegisterDTO userregisterdto) {
		boolean status = false;
		Pattern pattern = Pattern.compile("[a-z A-Z 0-9 @#$%&*~^!+-_~:;><?].{5,15}");
		Matcher matcher = pattern.matcher(userregisterdto.getNewPassword());
		LOG.info(userregisterdto.getNewPassword() + " : " + matcher.matches());
		if (matcher.matches()) {
			status = true;
			return status;
		} else {
			return status;
		}
	}
	
	public boolean logoutParam(String token) {
		if(token != null) {
			System.out.println(token);
			AuthorizationInfo authorizationInfo = authorizationInfoRepository.findByAuthToken(token);
			if(authorizationInfo != null) {
				authorizationInfo.setStatus("InActive");
				authorizationInfoRepository.save(authorizationInfo);
				return true;
			}
		}		
		return false;
	}
	
	public boolean validateTransferTokenPrams(TokenDTO tokenDTO) {
		if (tokenDTO.getToAddress() != null && StringUtils.isNotBlank(tokenDTO.getToAddress())
				&& tokenDTO.getRequestToken() != null && StringUtils.isNotBlank(tokenDTO.getRequestToken().toString())
				&& tokenDTO.getWalletPassword() != null && StringUtils.isNotBlank(tokenDTO.getWalletPassword().toString())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean validateTokenBalance(Double tokenCount)
	{				
		if(tokenCount>0)
		{
			return true;
		}
		return false;
		
	}
	public boolean isValidateEthAddress(TokenDTO tokenDTO) {
		Pattern pattern = Pattern.compile("^0x.{40}$");
		Matcher matcher = pattern.matcher(tokenDTO.getToAddress());
		if (matcher.matches()) {

			return true;
		}

		return false;
	}
	
	public boolean validateSession(String token, TokenDTO tokenDTO) {
		if(token != null) {
			System.out.println(token);
			AuthorizationInfo authorizationInfo = authorizationInfoRepository.findByAuthToken(token);
			if(authorizationInfo != null) {
				tokenDTO.setEmailId(authorizationInfo.getEmailId());
				return true;
			}
		}		
		return false;
	}
	
	public boolean validatePrivateKycParam(KycDTO kycDTO)
	{
		if (kycDTO.getEmailId() != null && StringUtils.isNotBlank(kycDTO.getEmailId())
				&& kycDTO.getFirstName() != null && StringUtils.isNotBlank(kycDTO.getFirstName())
				&& kycDTO.getLastName() != null && StringUtils.isNotBlank(kycDTO.getLastName())
				&& kycDTO.getIdentityType() != null && StringUtils.isNotBlank(kycDTO.getIdentityType())
				&& kycDTO.getIdentityNo() != null && StringUtils.isNotBlank(kycDTO.getIdentityNo())
				&& kycDTO.getCountryOfIssue() != null && StringUtils.isNotBlank(kycDTO.getCountryOfIssue())
				&& kycDTO.getDateOfIssue() != null && StringUtils.isNotBlank(kycDTO.getDateOfIssue().toString())
				&& kycDTO.getDateOfExpiry() != null && StringUtils.isNotBlank(kycDTO.getDateOfExpiry().toString())
				&& kycDTO.getResState() != null && StringUtils.isNotBlank(kycDTO.getResState())
				&& kycDTO.getResCountry() != null && StringUtils.isNotBlank(kycDTO.getResCountry())
				&& kycDTO.getSaleType() != null && StringUtils.isNotBlank(kycDTO.getSaleType())
				&& kycDTO.getSchoolName() != null && StringUtils.isNotBlank(kycDTO.getSchoolName())) {
			return true;
		}
		
		return false;
		
	}
	
	public boolean validatePublicKycParam(KycDTO kycDTO)
	{
		if (kycDTO.getEntityType() != null && StringUtils.isNotBlank(kycDTO.getEntityType())
				&& kycDTO.getEntityName() != null && StringUtils.isNotBlank(kycDTO.getEntityName())
				&& kycDTO.getHowAccredited() != null && StringUtils.isNotBlank(kycDTO.getHowAccredited())
				&& kycDTO.getInvAmount() != null && StringUtils.isNotBlank(kycDTO.getInvAmount().toString())) {
			return true;
		}
		
		return false;
		
	}
	
	public boolean validateKycDate(KycDTO kycDTO)
	{
		if(kycDTO.getDateOfExpiry().before(new Date()))
		{
			return true;
		}
		
		return false;
		
	}
	
	@SuppressWarnings("unused")
	public KycDTO listKYC(KycInfo kycInfo) {
		DecimalFormat df = new DecimalFormat("#.####");
		KycDTO kycList = new KycDTO();
		kycList.setId(kycInfo.getId());
		kycList.setFirstName(kycInfo.getFirstName());
		kycList.setMiddleName(kycInfo.getMiddleName());
		kycList.setLastName(kycInfo.getLastName());
		kycList.setEmailId(kycInfo.getEmailId());
		kycList.setEntityName(kycInfo.getEntityName());
		kycList.setEntityType(kycInfo.getInvEntityType());
		kycList.setIdentityType(kycInfo.getIdentityType());
		kycList.setIdentityNo(kycInfo.getIdentityNo());
		kycList.setCountryOfIssue(kycInfo.getCountryOfIssue());
		kycList.setDateOfIssue(kycInfo.getDateOfIssue());
		kycList.setDateOfExpiry(kycInfo.getDateOfExpiry());
		kycList.setResState(kycInfo.getResState());
		kycList.setResCountry(kycInfo.getResCountry());
		kycList.setSaleType(kycInfo.getSaleType());
		kycList.setKycStatus(kycInfo.getKycStatus());
		kycList.setHowAccredited(kycInfo.getHowAccredited());
	//	kycList.setInvAccreditation(kycInfo);
		kycList.setIdPath(env.getProperty("apache.server")+kycInfo.getIdPath());
		kycList.setIsVesting(kycInfo.getIsVesting());
		kycList.setSchoolName(kycInfo.getSchoolName());
		kycList.setReasonForRejection(kycInfo.getReasonForRejection());
		
		return kycList;
	}
	
	public boolean validateUpdateKYCParams(KycDTO kycDTO)
	{
		if(kycDTO.getId()!=null && StringUtils.isNotBlank(kycDTO.getId().toString())
				&& kycDTO.getKycStatus()!=null && StringUtils.isNotBlank(kycDTO.getKycStatus().toString()))
		{
			return true;
		}
		return false;
		
	}
	
//	@SuppressWarnings("unused")
//	public TokenDTO tokenValue(TokenRate tokenRate) {
//		TokenDTO tokenDto = new TokenDTO();
//		if (tokenRate != null) {
//			
//			System.out.println("Token value inside tokenValue------->"+tokenRate.getTokenValue());
//			DecimalFormat df = new DecimalFormat("#.###"); 
//			tokenDto.setTokenName(tokenRate.getTokenName());
//			tokenDto.setTokenValue(tokenRate.getTokenValue());
//			tokenDto.setTokenValueString(tokenDto.getTokenValue().toPlainString());
//			///*Double val = tokenDto.getTokenValue().doubleValue();
//			//tokenDto.setTokenValueString(val);
//			tokenDto.setTokenValueString(tokenDto.getTokenValueString().indexOf(".") < 0 ? tokenDto.getTokenValueString() : tokenDto.getTokenValueString().replaceAll("0*$", "").replaceAll("\\.$", ""));
//			
//		//	String dec = tokenDto.getTokenValue().toString().indexOf(".") < 0 ? tokenDto.getTokenValue().toString() : tokenDto.getTokenValue().toString().replaceAll("0*$", "").replaceAll("\\.$", "");
//			
//		}
//		return tokenDto;
//	}

public boolean validateAdmin(TokenDTO tokenDTO) {
		
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
		if (registerInfo.getUserType() == 1) {
			return true;
		}
		return false;
	}
	
	public boolean isSamePassword(TokenDTO tokenDTO) throws Exception {
		
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
		LOG.info("EncryptDecrypt.decrypt(registerInfo.getWalletPassword())"
				+ EncryptDecrypt.decrypt(registerInfo.getWalletPassword()));
		LOG.info("tokenDTO.getEtherWalletPassword()" + EncryptDecrypt.decrypt(registerInfo.getWalletPassword()));
		if (EncryptDecrypt.decrypt(registerInfo.getWalletPassword()).equals(tokenDTO.getWalletPassword())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean etherValidation(TokenDTO tokenDTO) throws Exception {

		BigDecimal balance = tokenUserService.etherBalance(tokenDTO);
		LOG.info("balance :: Ether :: " + balance);

		if (balance.doubleValue() >= 0.01) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean ValidateTokenBalanceForBurn(TokenDTO tokenDTO) throws Exception {
		

		Double balance = tokenUserService.balanceTokens(tokenDTO);

		if (tokenDTO.getTokens() != null && tokenDTO.getTokens().toString().trim() != "") {

			double burningAmount = tokenDTO.getTokens().doubleValue();

			LOG.info("Amount to Transfer :" + burningAmount);
			if (burningAmount > 0 && burningAmount <= balance.doubleValue()) {
				LOG.info("Inside Token burn Validation");
				return true;
			}
			return false;
		}
		return false;
	}
	
	public UserRegisterDTO userList(RegisterInfo userList) throws Exception {
		UserRegisterDTO registelist = new UserRegisterDTO();
		registelist.setEmailId(userList.getEmailId());
		registelist.setFirstName(userList.getFirstName());
		registelist.setLastName(userList.getLastName());
		registelist.setId(userList.getId());
		registelist.setUserType(userList.getUserType());
		registelist.setStatus(new Integer(userList.getActive()));
		registelist.setWalletAddress(userList.getWalletFile());
		registelist.setUserName(userList.getFirstName()+" "+userList.getLastName());
		registelist.setKycStatus(userList.getKycStatus());
		registelist.setCreatedDate(userList.getCreatedDate());
		if(userList.getMobileNo() != null) {
			registelist.setMobileno(userList.getMobileNo());
		} else {
			registelist.setMobileno("");
		}
		if(userList.getCity() != null) {
			registelist.setUserCity(userList.getCity());
		} else {
			registelist.setUserCity("");
		}
		if(userList.getCountry() != null) {
			registelist.setUserCountry(userList.getCountry());
		} else {
			registelist.setUserCountry("");
		}
		/*if(registelist.getStatus()==1)
		{
			registelist.setStatusVal("Active");
		}
		else if(registelist.getStatus()==0)
		{
			registelist.setStatusVal("Inactive");
		}*/

		return registelist;
	}
	
	@SuppressWarnings("unused")
	public List<SaleTypeDTO> tokenValues(TokenDiscountInfo tokenRate, TokenDTO tokenDTO) {
		List<SaleTypeDTO> saleTypes = new ArrayList<SaleTypeDTO>(); 
		if (tokenRate != null) {
			DecimalFormat df = new DecimalFormat("#.###"); 
			tokenDTO.setTokenName(tokenRate.getTokenName());
			tokenDTO.setReferralBonus(tokenRate.getReferralDisc().toPlainString());
			tokenDTO.setVestingDisc(tokenRate.getVestingDisc().toPlainString());
			tokenDTO.setNgnValue(tokenRate.getNgnValue().toPlainString());
		//	tokenDTO.setTokenValueString(tokenDTO.getTokenValueString().indexOf(".") < 0 ? tokenDTO.getTokenValueString() : tokenDTO.getTokenValueString().replaceAll("0*$", "").replaceAll("\\.$", ""));
			tokenDTO.setNgnValue(tokenDTO.getNgnValue().indexOf(".") < 0 ? tokenDTO.getNgnValue() : tokenDTO.getNgnValue().replaceAll("0*$", "").replaceAll("\\.$", ""));

		//	String dec = tokenDto.getTokenValue().toString().indexOf(".") < 0 ? tokenDto.getTokenValue().toString() : tokenDto.getTokenValue().toString().replaceAll("0*$", "").replaceAll("\\.$", "");
			
		List<TokenRateInfo> tokenInfo = tokenRateInfoRepository.findBySaletypeOrSaletypeOrSaletype("PRIVATE", "PREPUBLIC", "PUBLIC");
		
		if(tokenInfo!=null)
		{
			for(TokenRateInfo token :tokenInfo )
			{
				SaleTypeDTO sale = new SaleTypeDTO();
				sale.setSaleType(token.getSaletype());
				sale.setDiscount(token.getDiscount().toPlainString());
				sale.setTokenValue(token.getTokenValue().toPlainString());
				sale.setMinContribute(token.getMinContribute().toPlainString());
				sale.setTokenValue(sale.getTokenValue().indexOf(".") < 0 ? tokenDTO.getTokenValueString() : sale.getTokenValue().replaceAll("0*$", "").replaceAll("\\.$", ""));

				saleTypes.add(sale);
			}
		}
		}
		return saleTypes;
	}
	
	public boolean isValidReferralParam(UserRegisterDTO userRegisterDTO) {
		if (userRegisterDTO.getReferralEmailId() != null && StringUtils.isNotBlank(userRegisterDTO.getReferralEmailId())
				&& userRegisterDTO.getRefUserName() != null && StringUtils.isNotBlank(userRegisterDTO.getRefUserName())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean balanceCheckForCryptoInPurchaseCoins(TokenDTO tokenDTO) throws Exception {
	
		TokenRateInfo tokenRateInfo = tokenRateInfoRepository.findBySaletype(tokenDTO.getSaleType());
		if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("eth.payment"))) {
			
			Double currentetherValue = currencyRateUtils.getCryptoRateFromDollar(tokenDTO);
			Double oneTokenRate = currentetherValue * tokenRateInfo.getTokenValue().doubleValue();
				
			Double value = tokenDTO.getTokenAmount() * oneTokenRate;

//			GasFeeInfo gasFeeInfo = gasFeeInfoRepository.findGasFeeInfoById(1);
//			
//			Double withFee = value + gasFeeInfo.getEther();
			
			BigDecimal isEther = solidityHandler.etherBalance(tokenDTO);
			System.out.println("Ether Balance : " + isEther);
			
			if (isEther != null && (isEther.doubleValue() > value)) {
				return true;
			} else {
				return false;
			}
		} else if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("bch.payment"))) {
			UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());
			Double currentBchValue = currencyRateUtils.getCryptoRateFromDollar(tokenDTO);
			
			Double oneTokenRate = currentBchValue * tokenRateInfo.getTokenValue().doubleValue();
			System.out.println("currentBchValue : " + currentBchValue);
	
			Double value = tokenDTO.getTokenAmount() * oneTokenRate;

			System.out.println("currentBchValue : " + value);
			
//			GasFeeInfo gasFeeInfo = gasFeeInfoRepository.findGasFeeInfoById(1);
//			
//			Double withFee = value + gasFeeInfo.getBitcoinCash();
			
			Double isBch = bitcoinCashUtils.getBitcoinCashBalance(userRegisterDTO);
			System.out.println("BCH Balance : " + isBch);
			if (isBch != null && isBch > value) {
				return true;
			} else {
				return false;
			}
		} else if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("btc.payment"))) {
			UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());
			Double currentBtcValue = currencyRateUtils.getCryptoRateFromDollar(tokenDTO);
			
			Double oneTokenRate = currentBtcValue * tokenRateInfo.getTokenValue().doubleValue();
			System.out.println("currentBchValue : " + oneTokenRate);

			Double btcAmount = tokenDTO.getTokenAmount() * oneTokenRate;
			
//			GasFeeInfo gasFeeInfo = gasFeeInfoRepository.findGasFeeInfoById(1);
//			
//			Double withFee = btcAmount + gasFeeInfo.getBitcoin();
			
			Double isBtc = bitcoinUtils.getBitcoinBalance(userRegisterDTO);
			System.out.println("BTC Balance : " + isBtc);	
			if (isBtc != null && isBtc > btcAmount) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	public boolean balanceCheckForCryptoInPurchaseCoinsWithGasFee(TokenDTO tokenDTO) throws Exception {
		
		TokenRateInfo tokenRateInfo = tokenRateInfoRepository.findBySaletype(tokenDTO.getSaleType());
		if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("eth.payment"))) {
			
			Double currentetherValue = currencyRateUtils.getCryptoRateFromDollar(tokenDTO);
			Double oneTokenRate = currentetherValue * tokenRateInfo.getTokenValue().doubleValue();
				
			Double value = tokenDTO.getTokenAmount() * oneTokenRate;

			GasFeeInfo gasFeeInfo = gasFeeInfoRepository.findGasFeeInfoById(1);
			
			Double withFee = value + gasFeeInfo.getEther();
			
			BigDecimal isEther = solidityHandler.etherBalance(tokenDTO);
			System.out.println("Ether Balance : " + isEther);
			
			if (isEther != null && (isEther.doubleValue() > withFee)) {
				return true;
			} else {
				return false;
			}
		} else if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("bch.payment"))) {
			UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());
			Double currentBchValue = currencyRateUtils.getCryptoRateFromDollar(tokenDTO);
			
			Double oneTokenRate = currentBchValue * tokenRateInfo.getTokenValue().doubleValue();
			System.out.println("currentBchValue : " + currentBchValue);
	
			Double value = tokenDTO.getTokenAmount() * oneTokenRate;

			System.out.println("currentBchValue : " + value);
			
			GasFeeInfo gasFeeInfo = gasFeeInfoRepository.findGasFeeInfoById(1);
			
			Double withFee = value + gasFeeInfo.getBitcoinCash();
			
			Double isBch = bitcoinCashUtils.getBitcoinCashBalance(userRegisterDTO);
			System.out.println("BCH Balance : " + isBch);
			if (isBch != null && isBch > withFee) {
				return true;
			} else {
				return false;
			}
		} else if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("btc.payment"))) {
			UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());
			Double currentBtcValue = currencyRateUtils.getCryptoRateFromDollar(tokenDTO);
			
			Double oneTokenRate = currentBtcValue * tokenRateInfo.getTokenValue().doubleValue();
			System.out.println("currentBchValue : " + oneTokenRate);

			Double btcAmount = tokenDTO.getTokenAmount() * oneTokenRate;
			
			GasFeeInfo gasFeeInfo = gasFeeInfoRepository.findGasFeeInfoById(1);
			
			Double withFee = btcAmount + gasFeeInfo.getBitcoin();
			
			Double isBtc = bitcoinUtils.getBitcoinBalance(userRegisterDTO);
			System.out.println("BTC Balance : " + isBtc);	
			if (isBtc != null && isBtc > withFee) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	public boolean validatePurchaseParams(TokenDTO tokenDTO) {
		if(tokenDTO.getWalletPassword() != null && StringUtils.isNotBlank(tokenDTO.getWalletPassword())
				&& tokenDTO.getTokenAmount() != null && StringUtils.isNotBlank(tokenDTO.getTokenAmount().toString())
				&& tokenDTO.getTypeOfPurchase() != null && StringUtils.isNotBlank(tokenDTO.getTypeOfPurchase().toString())
				&& tokenDTO.getIsVesting() != null && StringUtils.isNotBlank(tokenDTO.getIsVesting().toString())
				&& tokenDTO.getSaleType() != null && StringUtils.isNotBlank(tokenDTO.getSaleType().toString())) {
			return true;
		}
		return false;
	}
	
	public boolean validateVestingParams(TokenDTO tokenDTO) {
		
		if(tokenDTO.getIsVesting() == 1) {
			if(tokenDTO.getDateOfBirth() != null && StringUtils.isNotBlank(tokenDTO.getDateOfBirth().toString())
				&& tokenDTO.getSchoolName() != null && StringUtils.isNotBlank(tokenDTO.getSchoolName())) {
				return true;
			}
			return false;
		} 
			return true;	
	}
	
	public boolean validateApptoveTokenPrams(TokenDTO tokenDTO) {
		if (tokenDTO.getTokenTransId() != null && StringUtils.isNotBlank(tokenDTO.getTokenTransId().toString())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean validateChange2FAParams(UserRegisterDTO userDTO) {
		if(userDTO.getPassword() != null && StringUtils.isNotBlank(userDTO.getPassword())
				&& userDTO.getTwofaStatus() != null && StringUtils.isNotBlank(userDTO.getTwofaStatus().toString())) {
			return true;
		}
		return false;
	}
	
	public boolean validateSecureTokenParams(TokenDTO tokenDTO) {
		if (tokenDTO.getToAddress() != null && StringUtils.isNotBlank(tokenDTO.getToAddress())
				&& tokenDTO.getRequestToken() != null && StringUtils.isNotBlank(tokenDTO.getRequestToken().toString())
				&& tokenDTO.getWalletPassword() != null && StringUtils.isNotBlank(tokenDTO.getWalletPassword().toString())
				&&  tokenDTO.getSecuredKey() != null && StringUtils.isNotBlank(tokenDTO.getSecuredKey().toString())) {
			return true;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("unused")
	public String keyGeneraterForTransfer(TokenDTO tokenDTO) {

		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		final GoogleAuthenticatorKey key = gAuth.createCredentials();
		String secretKey = key.getKey();

		Integer code = gAuth.getTotpPassword(secretKey);
		tokenDTO.setSecuredKey(code);

		try {
			RegisterInfo registerInfo = registerInfoRepository
					.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
			if (registerInfo != null) {
				registerInfo.setSecuredKey(code);
				registerInfo.setKeyDate(new Date());
				registerInfoRepository.save(registerInfo);

			}
			 boolean isEmailSent = emailNotificationService.sendEmailforOTPforTransfer(tokenDTO.getEmailId(),
				"Secured Transfer info from REMCO", tokenDTO.getSecuredKey().toString(), registerInfo.getFirstName()+" "+registerInfo.getLastName());
				System.out.println("Email sent Status------------->"+isEmailSent);
				if(!isEmailSent)
				{
					boolean isEmail = emailNotificationService.sendEmailforMailError(env.getProperty("admin1.email"), "Email Alert Info from REMCO", "");
				}

			return code.toString();
		} catch (Exception e) {
			return null;
		}

	}
	
	public boolean validateChangeIpParams(UserRegisterDTO userDTO) {
		if(userDTO.getIpAddress()!= null && StringUtils.isNotBlank(userDTO.getIpAddress())) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	public boolean updateEncryption() throws Exception
	{
		List<BitcoinCashInfo> cash = (List<BitcoinCashInfo>) bitcoinCashInfoRepository.findAll();
	
		for(BitcoinCashInfo bitcash:cash)
		{
			BitcoinCashInfo cashid = bitcoinCashInfoRepository.findOne(bitcash.getId());
			if(cashid!=null)
			{
				try{
					String tokenType = EncryptDecrypt.decrypt(bitcash.getTokenType());
				}
				catch(Exception e)
				{
					if(cashid.getWalletId()!=null)
					{
						cashid.setWalletId(EncryptDecrypt.encrypt(cashid.getWalletId()));
					}
					if(cashid.getBchWalletAddress()!=null)
					{
						cashid.setBchWalletAddress(EncryptDecrypt.encrypt(cashid.getBchWalletAddress()));
					}
					if(cashid.getBchWalletPassword()!=null)
					{
						cashid.setBchWalletPassword(EncryptDecrypt.encrypt(cashid.getBchWalletPassword()));
					}
					if(cashid.getTokenType()!=null)
					{
						cashid.setTokenType(EncryptDecrypt.encrypt(cashid.getTokenType()));
					}
					bitcoinCashInfoRepository.save(cashid);
				}
			}
		}
		
		List<BitcoinInfo> coin = (List<BitcoinInfo>) bitcoinInfoRepository.findAll();
		
		for(BitcoinInfo bitcoin:coin)
		{
			BitcoinInfo coinid = bitcoinInfoRepository.findOne(bitcoin.getId());
			if(coinid!=null)
			{
				try{
					String token = EncryptDecrypt.decrypt(bitcoin.getTokenType());
				}
				catch(Exception e)
				{
					
					if(coinid.getWalletId()!=null)
					{
						coinid.setWalletId(EncryptDecrypt.encrypt(coinid.getWalletId()));
					}
					if(coinid.getBtcWalletAddress()!=null)
					{
						coinid.setBtcWalletAddress(EncryptDecrypt.encrypt(coinid.getBtcWalletAddress()));
					}
					if(coinid.getBtcWalletPassword()!=null)
					{
						coinid.setBtcWalletPassword(EncryptDecrypt.encrypt(coinid.getBtcWalletPassword()));
					}
					if(coinid.getTokenType()!=null)
					{
						coinid.setTokenType(EncryptDecrypt.encrypt(coinid.getTokenType()));
					}
					
					bitcoinInfoRepository.save(coinid);
				}
			}
		}
		
		return false;
		
	}
	
	public boolean validatePasswordParam(TokenDTO tokenDTO) {
		RegisterInfo userInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
		try {
			if (userInfo != null) {
				String decryptWalletPassword = EncryptDecrypt.decrypt(userInfo.getWalletPassword());
				System.out.println("decryptWalletPassword::"+decryptWalletPassword);
				if (decryptWalletPassword.equals(tokenDTO.getWalletPassword())) {
					System.out.println("decryptWalletPassword::"+decryptWalletPassword);

					tokenDTO.setWalletPassword(tokenDTO.getWalletPassword());
					return true;
				}
				return false;
			}

		} catch (Exception e) {
		}
		return false;
	}
	
	public boolean validateResetWalletConfirmPassword(UserRegisterDTO userRegisterDTO) {
		if (userRegisterDTO.getNewWalletPassword().equals(userRegisterDTO.getConfirmWalletPassword())) {
			return true;
		}
		return false;
	}
	
	public boolean validateResetWalletPassword(UserRegisterDTO userregisterdto) {
		boolean status = false;
		Pattern pattern = Pattern.compile("[a-z A-Z 0-9 @#$%&*~^!+-_~:;><?].{5,15}");
		Matcher matcher = pattern.matcher(userregisterdto.getNewWalletPassword());
		LOG.info(userregisterdto.getNewWalletPassword() + " : " + matcher.matches());
		if (matcher.matches()) {
			status = true;
			return status;
		} else {
			return status;
		}
	}
	
	public boolean validateUserRefEmail(UserRegisterDTO userRegisterDTO)
	{
		if(userRegisterDTO.getEmailId().equalsIgnoreCase(userRegisterDTO.getRefEmailId()))
		{
			return true;
		}
		return false;
	}
	
	public boolean validatePurchaseParamsForVTN(TokenDTO tokenDTO) {
		if(tokenDTO.getTokenAmount() != null && StringUtils.isNotBlank(tokenDTO.getTokenAmount().toString())
				&& tokenDTO.getIsVesting() != null && StringUtils.isNotBlank(tokenDTO.getIsVesting().toString())
				&& tokenDTO.getSaleType() != null && StringUtils.isNotBlank(tokenDTO.getSaleType().toString())
				&& tokenDTO.getTypeOfPurchase() != null && StringUtils.isNotBlank(tokenDTO.getTypeOfPurchase().toString())) {
			return true;
		}
		return false;
	}
	
	public boolean validateVTNCallback(String ipn_refno) throws JSONException, IOException, java.text.ParseException {
		
		LOG.info("IPN Reference Numbver from VTN is------->"+ipn_refno);
		if(ipn_refno != null) {
			VTNPaymentInfo vtnPaymentInfo = new VTNPaymentInfo();
			vtnPaymentInfo.setIpnRefno(ipn_refno);
			vtnPaymentInfoRepository.save(vtnPaymentInfo);
			TokenDTO tokenDTO = new TokenDTO();
			tokenDTO.setIpnRefNumber(ipn_refno);
			
			boolean istrue = solidityHandler.checkPayment(tokenDTO);
			if(!istrue) {
				return true;
			}
			return true;
		}
		return false;
	}
	
	public boolean validatePurchaseId(TokenDTO tokenDTO) {
		
		if(tokenDTO.getPurchaseTokenId() != null && StringUtils.isNotBlank(tokenDTO.getPurchaseTokenId())) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public String validateMinimumContribution(TokenDTO tokenDTO) {
		
		if(tokenDTO.getSaleType() != null) {
			TokenRateInfo tokenRateInfo = tokenRateInfoRepository.findBySaletype(tokenDTO.getSaleType());
			if(!tokenRateInfo.getMinContribute().equals(0.0)) {
				Double rateInDoller = tokenRateInfo.getMinContribute().doubleValue();
				Double noOfTokens = tokenDTO.getTokenAmount();
				Double valueInUSD = noOfTokens * tokenRateInfo.getTokenValue().doubleValue();
				if(valueInUSD > rateInDoller) {
					return "success";
				} else {
					return "Minimum contribution is : $ " + rateInDoller;
				}
			}		
		}
		return "failure";
	}
	
	public boolean isPendingCheck(TokenDTO tokenDTO) throws java.text.ParseException {
		
		String emailId = tokenDTO.getEmailId();
		String status = "pending";
		List<PurchaseTokenInfo> purchaseTokenInfo = purchaseTokenInfoRepository.findByEmailIdAndAsynchStatus(emailId, status);
		if(purchaseTokenInfo.size() > 0) {
			return false;
		}
		return true;
	}
	
	public char[] randomPassword()
	{
		Integer len = 10;
		
		String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
        String Small_chars = "abcdefghijklmnopqrstuvwxyz"; 
        String numbers = "0123456789"; 
                String symbols = "!@#$%^&*_=+-/.?<>)"; 
  
        String values = Capital_chars + Small_chars + 
                        numbers + symbols; 
  
        // Using random method 
        Random rndm_method = new Random(); 
  
        char[] password = new char[len]; 
  
        for (int i = 0; i < len; i++) 
        { 
            // Use of charAt() method : to get character value 
            // Use of nextInt() as it is scanning the value as int 
            password[i] = 
              values.charAt(rndm_method.nextInt(values.length())); 
  
        } 
        return password; 
		
	}
	
	public UserRegisterDTO airDropUserList(AirDropUserBonus regInfo) throws Exception {
		
		UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
		userRegisterDTO.setFirstName(regInfo.getFullName());
		userRegisterDTO.setLastName("");
		userRegisterDTO.setId(regInfo.getId());
		userRegisterDTO.setStatus(regInfo.getTokenTransferStatus());
		userRegisterDTO.setWalletAddress(regInfo.getWalletAddress());
		userRegisterDTO.setUserName(regInfo.getFullName());
		userRegisterDTO.setAllocationType(regInfo.getAllocationType());
		userRegisterDTO.setEarnedToken(regInfo.getEarnedToken());
		userRegisterDTO.setCreatedDate(new Date());
		userRegisterDTO.setEmailId(regInfo.getEmailId());
		userRegisterDTO.setTransferredDate(regInfo.getTransferredDate());
		userRegisterDTO.setEarnedTokenString(regInfo.getEarnedToken().toPlainString());
		

		return userRegisterDTO;
	}
	
	public boolean validateMultipleTransferTokenPrams(TokenDTO tokenDTO) {
		
		for(TokenTransferDTO token : tokenDTO.getTokenList())
		{
			if (token.getToAddress() != null && StringUtils.isNotBlank(token.getToAddress())
					&& token.getRequestToken() != null && StringUtils.isNotBlank(token.getRequestToken().toString())
					&& tokenDTO.getWalletPassword()!=null && StringUtils.isNotBlank(tokenDTO.getWalletPassword().toString())) {
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	public UserRegisterDTO promoList(PromoUsersInfo promoUers) throws Exception
	{
		
		UserRegisterDTO userDTO = new UserRegisterDTO();
		userDTO.setEmailId(promoUers.getEmailId());
		userDTO.setFirstName(promoUers.getFirstName());
		userDTO.setLastName(promoUers.getLastName());
		userDTO.setUserAddress(promoUers.getAddress());
		userDTO.setUserCity(promoUers.getCity());
		userDTO.setUserCountry(promoUers.getCountry());
		userDTO.setUserZipcode(promoUers.getZipcode());
		userDTO.setPromoFlag(promoUers.getPromoType());
		userDTO.setNoOfTokens(promoUers.getTokenCount());
		userDTO.setTokenStatus(promoUers.getTokenStatus());
		userDTO.setCreatedDate(promoUers.getCreatedDate());			
		
		return userDTO;
		
	}
	
	public String updateTokenValue(UserRegisterDTO userRegisterDTO)
	{
		try{
			Config config = configInfoRepository.findConfigByConfigKey("promotoken");
			
			String tokenvalue = config.getConfigValue();
			
			if(userRegisterDTO.getPromoToken()!=null && userRegisterDTO.getPromoToken()>0)
			{
				config.setConfigValue(userRegisterDTO.getPromoToken().toString());
				configInfoRepository.save(config);
				tokenvalue  = userRegisterDTO.getPromoToken().toString();
			}
			
			return tokenvalue;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	public boolean getUserDetails(UserRegisterDTO userRegisterDTO) throws Exception
	{
		RegisterInfo userInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(userRegisterDTO.getEmailId());
		if(userInfo!=null)
		{
			userRegisterDTO.setWalletAddress(userInfo.getWalletFile());
			userRegisterDTO.setId(userInfo.getId());
			String refId = EncryptDecrypt.encrypt(userInfo.getId().toString());
			userRegisterDTO.setReferralLink(env.getProperty("referral.link")+refId);
			return true;

		}
		
		return false;
		
	}
	
	public boolean updateEarTokenCount()
	{
		
		List<AirDropUserBonus> airDrop = (List<AirDropUserBonus>) airDropUserBonusRepository.findAll();
		
		if(airDrop!=null)
		{
			/*for(AirDropUserBonus air : airDrop)
			{
				if((air.getConfirmed().equals("TRUE")) && (air.getConfirmedReferralCount()>0))
				{
					Double refCount = (double) (air.getConfirmedReferralCount()*25); 
					Double earned = 125 + refCount;
					air.setEarnedToken(earned);
					airDropUserBonusRepository.save(air);
				}
				else if((air.getConfirmed().equals("FALSE")) && (air.getConfirmedReferralCount()>0))
				{
					Double refCount = (double) (air.getConfirmedReferralCount()*25); 
					air.setEarnedToken(refCount);
					airDropUserBonusRepository.save(air);
				}
				else if((air.getConfirmed().equals("TRUE")) && (air.getConfirmedReferralCount()<=0))
				{
					Double refCount = 125.0; 
					air.setEarnedToken(refCount);
					airDropUserBonusRepository.save(air);
				}
				else 
				{
					air.setEarnedToken(0.00);
					airDropUserBonusRepository.save(air);
				}
			}*/
		}
		
		return false;
		
	}
	
	public boolean validateUserCheckParam(UserRegisterDTO userRegisterDTO) {

		if (userRegisterDTO.getEmailId() != null && StringUtils.isNotBlank(userRegisterDTO.getEmailId())
				&& userRegisterDTO.getMobileno() != null
				&& StringUtils.isNotBlank(userRegisterDTO.getMobileno()) && userRegisterDTO.getPromoFlag() != null 
				&& StringUtils.isNotBlank(userRegisterDTO.getPromoFlag())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean ValidateInputs(UserRegisterDTO userRegisterDTO) {
		if(userRegisterDTO.getEmailId() != null && StringUtils.isNotBlank(userRegisterDTO.getEmailId())
				&& userRegisterDTO.getMobileno() != null && StringUtils.isNotBlank(userRegisterDTO.getMobileno())
				&& userRegisterDTO.getFirstName() != null && StringUtils.isNotBlank(userRegisterDTO.getFirstName())
				&& userRegisterDTO.getLastName() != null && StringUtils.isNotBlank(userRegisterDTO.getLastName())
				&& userRegisterDTO.getReferSite() != null && StringUtils.isNotBlank(userRegisterDTO.getReferSite())
				&& userRegisterDTO.getTwofaStatus() != null && StringUtils.isNotBlank(userRegisterDTO.getTwofaStatus().toString())) {
			return true;
		}
		return false;
	}
	
	public boolean isValidateEthBalance(TokenDTO tokenDTO) throws Exception {
		
		BigDecimal isEther = solidityHandler.etherBalance(tokenDTO);
		LOG.info("Ether Balance : " + isEther);
		
		if (isEther != null && (isEther.doubleValue() > 0.0001)) {
			return true;
		} else {
			return false;
		}
	}

	public String checkReferralId(UserRegisterDTO userRegisterDTO) throws Exception {
		try {
			String refId = EncryptDecrypt.decrypt(userRegisterDTO.getReferralId());
			if(refId != null) {
				return null;
			}
			return null;
		} catch(Exception e) {
			e.printStackTrace();
			return "You provide valid Referral Id";
		}
	}
	
}
