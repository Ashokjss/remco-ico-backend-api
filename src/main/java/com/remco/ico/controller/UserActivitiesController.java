package com.remco.ico.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.remco.ico.DTO.KycDTO;
import com.remco.ico.DTO.LoginDTO;
import com.remco.ico.DTO.StatusResponseDTO;
import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.UserRegisterDTO;
import com.remco.ico.service.AdminService;
import com.remco.ico.service.TokenUserService;
import com.remco.ico.service.UserRegisterService;
import com.remco.ico.solidityHandler.SolidityHandler;
import com.remco.ico.utils.BitcoinCashUtils;
import com.remco.ico.utils.BitcoinUtils;
import com.remco.ico.utils.RemcoUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/remco/api/user")
@CrossOrigin
public class UserActivitiesController {

	private static final Logger LOG = LoggerFactory.getLogger(UserActivitiesController.class);

	@Autowired
	private Environment env;
	@Autowired
	private RemcoUtils remcoUtils;
	@Autowired
	private UserRegisterService userRegisterService;
	@Autowired
	private BitcoinCashUtils bitcoinCashUtils;
	@Autowired
	private BitcoinUtils bitcoinUtils;
	@Autowired
	private TokenUserService tokenUserService;
	@Autowired
	private SolidityHandler solidityHandler;
	@Autowired
	private AdminService adminService;

	@CrossOrigin
	@RequestMapping(value = "/logout", method = RequestMethod.GET, produces = { "application/json" })
	@ApiOperation(value = "Logout", notes = "Need to Logout")
	public synchronized ResponseEntity<String> logout(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isUpdated = remcoUtils.logoutParam(token);
			if (isUpdated) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("logout.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("logout.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in registration  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/reset/password", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Reset Password", notes = "Need to Reset Password")
	public synchronized ResponseEntity<String> resetPassword(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			TokenDTO tokenDTO = new TokenDTO();

			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());
			boolean isOldPassword = userRegisterService.isOldPassword(userRegisterDTO);
			if (!isOldPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.old.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isConformPassword = remcoUtils.validateResetConfirmPassword(userRegisterDTO);
			if (!isConformPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("password.exist"));

				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidPassword = remcoUtils.validateResetPassword(userRegisterDTO);
			if (!isValidPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("change.validate.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean changePassword = userRegisterService.isChangePassword(userRegisterDTO);
			if (!changePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("change.password.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("change.password.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in ResetPassword  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/token/purchase", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Token Purchase", notes = "Need to get user token purchase")
	public synchronized ResponseEntity<String> tokenPurchase(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user token purchase", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());
			boolean isValidInputs = remcoUtils.validatePurchaseParams(tokenDTO);
			if (!isValidInputs) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("purchase.params.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidateVesting = remcoUtils.validateVestingParams(tokenDTO);
			if (!isValidateVesting) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("vesting.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isPasswordWrong = remcoUtils.validatePasswordParam(tokenDTO);
			if (!isPasswordWrong) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.wall.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isPending = remcoUtils.isPendingCheck(tokenDTO);
			if (!isPending) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("pending.status.check"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			
			if (!tokenDTO.getTypeOfPurchase().equalsIgnoreCase("ETH")) {
				boolean isLogin = bitcoinCashUtils.loginCrypto(userRegisterDTO);
				if (!isLogin) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("login.crypto.failed"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
			}

			boolean isBalance = remcoUtils.balanceCheckForCryptoInPurchaseCoins(tokenDTO);
			if (!isBalance) {
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("balance.less"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			/*boolean withFee = remcoUtils.balanceCheckForCryptoInPurchaseCoinsWithGasFee(tokenDTO);
			if (!withFee) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.less"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}*/
			
			String isValidTokenBalance = adminService.validateTokenBalanceForPurchase(tokenDTO);
			if (isValidTokenBalance != "success") {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(isValidTokenBalance);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			String isTokensValid = remcoUtils.validateMinimumContribution(tokenDTO);
			if (isTokensValid != "success") {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(isTokensValid);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidTokens = adminService.tokenPurchase(tokenDTO);
			if (!isValidTokens) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.purchase.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("token.purchase.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Purchase  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/token/purchase/vtn", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Token Purchase using VTN", notes = "Need to get user token purchase  using VTN")
	public synchronized ResponseEntity<String> tokenPurchaseUsingVTN(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user token purchase  using VTN", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			Double tokenAmount = tokenDTO.getTokenAmount();
			UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());

			boolean isValidInputs = remcoUtils.validatePurchaseParamsForVTN(tokenDTO);
			if (!isValidInputs) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("purchase.params.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidateVesting = remcoUtils.validateVestingParams(tokenDTO);
			if (!isValidateVesting) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("vesting.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			String isValidTokenBalance = adminService.validateTokenBalanceForPurchase(tokenDTO);
			if (isValidTokenBalance != "success") {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(isValidTokenBalance);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			String isTokensValid = remcoUtils.validateMinimumContribution(tokenDTO);
			if (isTokensValid != "success") {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(isTokensValid);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidTokens = adminService.tokenPurchaseUsingVTN(tokenDTO);
			if (!isValidTokens) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.purchase.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("token.purchase.success"));
			statusResponseDTO.setPg_ActionURL(env.getProperty("pg.action.url"));
			statusResponseDTO.setTypeOfPurchase(env.getProperty("vtn.type"));
			statusResponseDTO.setNumberOfTokens(tokenAmount);
			statusResponseDTO.setPurchaseTokenId(tokenDTO.getCustom_remarks());
			statusResponseDTO.setPg_formFields(tokenDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Purchase  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/token/purchase/vtn/cancel", method = RequestMethod.POST, produces = {
			"application/json" })
	@ApiOperation(value = "Token Purchase using VTN", notes = "Need to get user token purchase  using VTN")
	public synchronized ResponseEntity<String> tokenPurchaseCanceledInVTN(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user token purchase  using VTN", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());

			boolean isValid = remcoUtils.validatePurchaseId(tokenDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.tokenId.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isCancel = tokenUserService.tokenPurchaseCancellationInVTN(tokenDTO);
			if (!isCancel) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("cancel.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("cancel.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Purchase  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/referral", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Refer the person", notes = "Need to refer the person")
	public synchronized ResponseEntity<String> referPerson(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Refer the person", required = true) @RequestBody UserRegisterDTO userRegisterDTO)
			throws InterruptedException {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			TokenDTO tokenDTO = new TokenDTO();

			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());

			boolean isValidEmail = remcoUtils.isValidReferralParam(userRegisterDTO);
			if (!isValidEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isEmailExist = userRegisterService
					.isAccountExistCheckByEmailId(userRegisterDTO.getReferralEmailId().toLowerCase());
			if (isEmailExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("email.exist.refer"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValid = userRegisterService.referral(userRegisterDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(userRegisterDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("refer.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in User Referral  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/upload/kyc", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Reset Password", notes = "Need to Reset Password")
	public synchronized ResponseEntity<String> uploadKyc(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestParam(name = "userInfo", value = "userInfo", required = true) String kycDTOStr,
			@ApiParam(value = "Required file attachment", required = true) @RequestParam(name = "idProofDoc", value = "idProofDoc", required = true) MultipartFile idProofDoc) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {

			ObjectMapper mapper = new ObjectMapper();
			KycDTO kycDTO = null;

			try {
				kycDTO = mapper.readValue(kycDTOStr, KycDTO.class);
			} catch (Exception e) {
				System.out.println("Catch block---->" + e.toString());
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("read.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			kycDTO.setEmailId(tokenDTO.getEmailId());
			boolean isValidPrivate = remcoUtils.validatePrivateKycParam(kycDTO);
			if (!isValidPrivate) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			if (kycDTO.getSaleType().equals("PUBLIC")) {
				boolean isValidPublic = remcoUtils.validatePublicKycParam(kycDTO);
				if (!isValidPublic) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
			}
			if (idProofDoc != null) {
				String idProofPath = userRegisterService.userDocumentUpload(idProofDoc, kycDTO.getEmailId(),
						"idProofId");

				LOG.info("panIdFilePath : " + idProofPath);
				if (idProofPath == null) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage("Please Upload the Required Document");

					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				} else {
					kycDTO.setIdPath(idProofPath);
				}

			}
			boolean isValidProof = remcoUtils.validateKycDate(kycDTO);
			if (isValidProof) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.proof"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isSaved = userRegisterService.saveKycInfo(kycDTO);
			if (!isSaved) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("kyc.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("kyc.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Uploading KYC  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/bch/balance", method = RequestMethod.GET, produces = { "application/json" })
	@ApiOperation(value = "BCH Balance", notes = "Need to Get BCH balances")
	public synchronized ResponseEntity<String> bchBalance(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token) {
		// @ApiParam(value = "BCH Balance", required = true) @RequestBody
		// UserRegisterDTO userRegisterDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			TokenDTO tokenDTO = new TokenDTO();
			UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());
			boolean isLogin = bitcoinCashUtils.loginCrypto(userRegisterDTO);
			if (!isLogin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("login.crypto.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			Double balance = bitcoinCashUtils.getBitcoinCashBalance(userRegisterDTO);
			if (balance == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("bch.balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("bch.balance.success"));
			statusResponseDTO.setBchBalance(balance);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in BCH balance : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/btc/balance", method = RequestMethod.GET, produces = { "application/json" })
	@ApiOperation(value = "BTH Balance", notes = "Need to Get BTH balances")
	public synchronized ResponseEntity<String> btcBalance(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token) {
		// @ApiParam(value = "BTH Balance", required = true) @RequestBody
		// UserRegisterDTO userRegisterDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			TokenDTO tokenDTO = new TokenDTO();
			UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());

			boolean isLogin = bitcoinCashUtils.loginCrypto(userRegisterDTO);
			if (!isLogin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("login.crypto.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			Double balance = bitcoinUtils.getBitcoinBalance(userRegisterDTO);
			if (balance == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("btc.balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("btc.balance.success"));
			statusResponseDTO.setBtcBalance(balance);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in BTC Balance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/transfer", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "transfer", notes = "Need to Logout")
	public synchronized ResponseEntity<String> logout(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isTokenTransfer = remcoUtils.validateTransferTokenPrams(tokenDTO);
			if (!isTokenTransfer) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isvalidWallet = tokenUserService.checkWalletAddressEquals(tokenDTO);
			if (!isvalidWallet) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("address.invalid"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean tokenValue = remcoUtils.validateTokenBalance(tokenDTO.getRequestToken().doubleValue());
			if (!tokenValue) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.blc"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isValidPassword = userRegisterService.isValidWalletPassword(tokenDTO);
			if (!isValidPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.wall.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isValidateEthAddress = remcoUtils.isValidateEthAddress(tokenDTO);
			if (!isValidateEthAddress) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.eth.address"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isValidETH = remcoUtils.isValidateEthBalance(tokenDTO);
			if (!isValidETH) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.less"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}			
			String vestingBalance = tokenUserService.validateVestingTokens(tokenDTO);
			if (vestingBalance != "success") {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(vestingBalance);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			String isValidTokenBal = solidityHandler.isValidTokenBalForCrowdsale(tokenDTO);
			if (isValidTokenBal != "success") {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(isValidTokenBal);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			String securedKey = remcoUtils.keyGeneraterForTransfer(tokenDTO);
			if (securedKey != null) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("admin.transfer.success.secure"));
				statusResponseDTO.setSecuredKey(securedKey);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin.transfer.failed.secure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in tokencreation  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/change/2fa", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Reset Password", notes = "Need to Reset Password")
	public synchronized ResponseEntity<String> update2faAuth(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			TokenDTO tokenDTO = new TokenDTO();

			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());
			boolean isTokenTransfer = remcoUtils.validateChange2FAParams(userRegisterDTO);
			if (!isTokenTransfer) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean userDTO = userRegisterService.isEmailIdAndPasswordExists(userRegisterDTO, "UPDATE");
			if (!userDTO) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean changePassword = userRegisterService.isChangeAuthentication(userRegisterDTO);
			if (!changePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.change.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("auth.change.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in ResetPassword  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/initiate/transfer", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Reset Password", notes = "Need to Reset Password")
	public synchronized ResponseEntity<String> initiateTransfer(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isTokenTransfer = remcoUtils.validateSecureTokenParams(tokenDTO);
			if (!isTokenTransfer) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			LoginDTO responseDTO = userRegisterService.transferSecure(tokenDTO);
			if (responseDTO.getStatus().equalsIgnoreCase("failed")) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("login.key.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			if (tokenDTO.getUserType() == 0) {
				boolean isTransfer = tokenUserService.TransferCoin(tokenDTO);
				if (isTransfer) {

					statusResponseDTO.setStatus(env.getProperty("success"));
					statusResponseDTO.setMessage(env.getProperty("user.transfer.success"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
				} else {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("user.transfer.failed"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

				}
			} else if (tokenDTO.getUserType() == 1) {
				boolean isTransfer = tokenUserService.saveTokenDetails(tokenDTO);
				if (isTransfer) {

					statusResponseDTO.setStatus(env.getProperty("success"));
					statusResponseDTO.setMessage(env.getProperty("admin.transfer.success"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
				} else {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("admin.transfer.failed"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

				}
			}
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("secure.transfer.failed"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in ResetPassword  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/token/balance", method = RequestMethod.GET, produces = { "application/json" })
	@ApiOperation(value = "Token Balance", notes = "Need to show Token Balance")
	public synchronized ResponseEntity<String> tokenBalance(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token) {
		// @ApiParam(value = "Token Balance", required = true) @RequestBody
		// TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			Double isValid = tokenUserService.balanceTokens(tokenDTO);
			if (isValid == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("balance.success"));
			statusResponseDTO.setTokenBalance(tokenDTO);
			statusResponseDTO.setTokenBalanceString(tokenDTO.getTokenAmountString());
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in token balance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/token/purchase/list", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Token Purchase list", notes = "Need to get user token purchase list")
	public synchronized ResponseEntity<String> tokenPurchaseList(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user token purchase", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());

			List<TokenDTO> isValidTokens = adminService.purchaseLists(tokenDTO);
			if (isValidTokens == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.purchase.list.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("token.purchase.list.success"));
			statusResponseDTO.setTotalPages(tokenDTO.getTotalPages());
			statusResponseDTO.setTotalElements(tokenDTO.getTotalElements());
			statusResponseDTO.setPurchaseListInfo(isValidTokens);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Purchase list : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/referral/purchase/list", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "referral Purchase list", notes = "Need to get user referral purchase list")
	public synchronized ResponseEntity<String> referralPurchaseList(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user token purchase", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<TokenDTO> isValidTokens = adminService.referralPurchaseList(tokenDTO);
			if (isValidTokens == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("referral.purchase.list.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("referral.purchase.list.success"));
			statusResponseDTO.setPurchaseListInfo(isValidTokens);
			statusResponseDTO.setTotalPages(tokenDTO.getTotalPages());
			statusResponseDTO.setTotalElements(tokenDTO.getTotalElements());
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Purchase list : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/forgot/walletpassword", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Reset Password", notes = "Need to Reset Password")
	public synchronized ResponseEntity<String> resetWalletPassword(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO,
			HttpServletRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());
			boolean emailVerify = remcoUtils.validateUserRefEmail(userRegisterDTO);
			if (!emailVerify) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.validate.email"));

				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean changePassword = userRegisterService.sendWalletPassword(userRegisterDTO);
			if (!changePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("wallet.password.notupdate"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("wallet.password.update"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in ResetPassword  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/encrypt", method = RequestMethod.POST, produces = { "application/json",
			"application/xml" })
	@ApiOperation(value = "Logout", notes = "Need to Logout")
	public synchronized ResponseEntity<String> encrypt(
			@ApiParam(value = "Required user details", required = true) TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isUpdated = remcoUtils.updateEncryption();
			if (isUpdated) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage("Encryption Success");
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage("Encryption Failed");
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in registration  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}

}
