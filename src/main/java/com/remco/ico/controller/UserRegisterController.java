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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import com.google.gson.Gson;
import com.remco.ico.DTO.LoginDTO;
import com.remco.ico.DTO.StatusResponseDTO;
import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.UserRegisterDTO;
import com.remco.ico.service.TokenUserService;
import com.remco.ico.service.UserRegisterService;
import com.remco.ico.utils.BitcoinCashUtils;
import com.remco.ico.utils.CurrencyRateUtils;
import com.remco.ico.utils.RemcoUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/remco/api/user")
@CrossOrigin
public class UserRegisterController {

	private static final Logger LOG = LoggerFactory.getLogger(UserRegisterController.class);

	@Autowired
	private Environment env;
	@Autowired
	private RemcoUtils remcoUtils;
	@Autowired
	private UserRegisterService userRegisterService;
	@Autowired
	private BitcoinCashUtils bitcoinCashUtils;
	@Autowired
	private CurrencyRateUtils currencyRateUtils;
	@Autowired
	private TokenUserService tokenUserService;

	@CrossOrigin
	@RequestMapping(value = "/userregister", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "register account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> registerUser(
			@ApiParam(value = "Required user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO)
			throws Exception {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		boolean isValid = remcoUtils.validateRegistrationParam(userRegisterDTO);
		if (!isValid) {
			// User registration validation failed
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}

		boolean isValidEmailId = remcoUtils.validateEmail(userRegisterDTO.getEmailId());
		if (!isValidEmailId) {

			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("register.validate.email"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

		}
		
		boolean isConformPassword = remcoUtils.validateConfirmPassword(userRegisterDTO);
		if (!isConformPassword) {
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("password.exist"));

			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}

		boolean isConformWalletPassword = remcoUtils.validateWalletPassword(userRegisterDTO);
		if (!isConformWalletPassword) {
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("wallet.password.exist"));

			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		
		boolean isLogin = bitcoinCashUtils.loginCrypto(userRegisterDTO);
		if (!isLogin) {
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("login.crypto.failed"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}

		boolean isEmailExists = userRegisterService.isAccountExistCheckByEmailId(userRegisterDTO.getEmailId());
		if (isEmailExists) {
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("email.exist"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

		}

		boolean isMobileExists = userRegisterService.isAccountExistCheckByMobileNo(userRegisterDTO.getMobileno());
		if (isMobileExists) {
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("mobileno.exist"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		try {
			String validReferralId = remcoUtils.checkReferralId(userRegisterDTO);
			if(validReferralId != null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(validReferralId);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isRegister = userRegisterService.saveAccountInfo(userRegisterDTO);
			if (isRegister) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("register.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.failed"));
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
	@RequestMapping(value = "/userregister/activate", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Activate User account", notes = "Need to Activate User Account")
	public synchronized ResponseEntity<String> activateAccount(
			@ApiParam(value = "Required User details", required = true) @RequestBody LoginDTO loginDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValidPassword = remcoUtils.isValidActivateParam(loginDTO);
			if (!isValidPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("value.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isActivate = userRegisterService.activateUser(loginDTO);
			if (isActivate) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("email.verify.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(loginDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			// LOG.error("Problem in forgotpassword : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/login/secure", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "login account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> userLoginSecure(
			@ApiParam(value = "login user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValid = remcoUtils.validateLoginParam(userRegisterDTO);
			if (!isValid) {
				// User login validation failed
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidEmail = remcoUtils.isAccountExistCheckByEmailId(userRegisterDTO);
			if (!isValidEmail) {
				// User login secure validation failed
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("invalid.emailId"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			if (userRegisterDTO.getUserType() == 1) {
				
				System.out.println("UserType =======>"+userRegisterDTO.getUserType());
				boolean isValidIp = userRegisterService.validateAdminIp(userRegisterDTO);
				if (!isValidIp) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("ip.invalid"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
			}
			boolean isValidEmailAct = remcoUtils.validateEmailActivation(userRegisterDTO.getEmailId(), userRegisterDTO);
			if (!isValidEmailAct) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(userRegisterDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			LoginDTO responseDTO = null;
			if (userRegisterDTO.getTwofaStatus() == 1) {
				responseDTO = userRegisterService.isEmailAndPasswordExit(userRegisterDTO);
				if (responseDTO.getStatus().equalsIgnoreCase("failed")) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("login.failed"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

				}
			} else {
				
				if(userRegisterDTO.getPromoFlag()==null)
				{
					boolean isLogin = bitcoinCashUtils.loginCrypto(userRegisterDTO);
					if (!isLogin) {
						statusResponseDTO.setStatus(env.getProperty("failure"));
						statusResponseDTO.setMessage(env.getProperty("login.crypto.failed"));
						return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
					}
				}
				
				responseDTO = userRegisterService.login(userRegisterDTO);
				if (responseDTO.getStatus().equalsIgnoreCase("failed")) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("login.key.failure"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("login.success"));
			statusResponseDTO.setLoginInfo(responseDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in login : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "login account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> login(
			@ApiParam(value = "login user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValid = remcoUtils.validatesecureKeyParam(userRegisterDTO);
			if (!isValid) {
				// User login validation failed
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			/*boolean userDTO = userRegisterService.isEmailIdAndPasswordExists(userRegisterDTO, "LOGIN");
			if (!userDTO) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("login.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}*/
			boolean isLogin = bitcoinCashUtils.loginCrypto(userRegisterDTO);
			if (!isLogin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("login.crypto.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			LoginDTO responseDTO = userRegisterService.loginSecure(userRegisterDTO);
			if (responseDTO.getStatus().equalsIgnoreCase("failed")) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(userRegisterDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("login.success"));
			statusResponseDTO.setLoginInfo(responseDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in login : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/forgot/password", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Forgot password", notes = "Need to update the new password")
	public synchronized ResponseEntity<String> forgotPassword(
			@ApiParam(value = "Required User details", required = true) @RequestBody UserRegisterDTO userRegisterDTO,
			HttpServletRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try {

			boolean isvalid = remcoUtils.validateForgotPwdParam(userRegisterDTO);
			if (!isvalid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.forgot.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidEmail = remcoUtils.validateEmailActivation(userRegisterDTO.getEmailId(), userRegisterDTO);
			if (!isValidEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(userRegisterDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isEmailIdExist = userRegisterService.isEmailIdExist(userRegisterDTO, request);
			if (!isEmailIdExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("user.notexist"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("password.update"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in forgotpassword  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/forgot/password/reset", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Forgot password Reset", notes = "Need to get forgot password")
	public synchronized ResponseEntity<String> forgotPasswordReset(
			@ApiParam(value = "Required User details", required = true) @RequestBody UserRegisterDTO userregisterdto) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValidPassword = remcoUtils.isValidResetPwdParam(userregisterdto);
			if (!isValidPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("value.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);

			}
			boolean isValidEmail = remcoUtils.validateEmailActivationForLink(userregisterdto.getEmailId(),
					userregisterdto);
			if (!isValidEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(userregisterdto.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean changePassword = userRegisterService.saveNewPasswordInfo(userregisterdto);
			if (changePassword) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("reset.password.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("reset.password.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			// LOG.error("Problem in forgotpassword : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/resend/verifylink", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Forgot password Reset", notes = "Need resend Email Verfication link")
	public synchronized ResponseEntity<String> resendEmailVerificationLink(
			@ApiParam(value = "Required User details", required = true) @RequestBody UserRegisterDTO userregisterdto) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isvalid = remcoUtils.validateForgotPwdParam(userregisterdto);
			if (!isvalid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.forgot.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidEmail = remcoUtils.validateEmailActivation(userregisterdto.getEmailId(), userregisterdto);
			if (isValidEmail) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("email.verify.done"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}
			boolean changePassword = userRegisterService.resendVerificationMail(userregisterdto);
			if (changePassword) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("resend.link.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("resend.link.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			// LOG.error("Problem in forgotpassword : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/reset/walletpassword", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Reset Password", notes = "Need to Reset Password")
	public synchronized ResponseEntity<String> resetWalletPassword(
			@ApiParam(value = "Required user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO,
			HttpServletRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isEmailIdExist = userRegisterService.isUserExist(userRegisterDTO, request);
			if (!isEmailIdExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("user.notexist"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isOldPassword = userRegisterService.isOldWalletPassword(userRegisterDTO);
			if (!isOldPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.oldwallet.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isConformPassword = remcoUtils.validateResetWalletConfirmPassword(userRegisterDTO);
			if (!isConformPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("wallet.password.exist"));

				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValidPassword = remcoUtils.validateResetWalletPassword(userRegisterDTO);
			if (!isValidPassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("change.validate.walletpassword"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean changePassword = userRegisterService.isChangeWalletPassword(userRegisterDTO);
			if (!changePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("change.walletpassword.failure"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("change.walletpassword.success"));
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

	/*
	 * @CrossOrigin
	 * 
	 * @RequestMapping(value = "/vtn/callback", method = RequestMethod.POST,
	 * produces = { "application/json", "application/xml" ,
	 * MediaType.APPLICATION_XML_VALUE})
	 * 
	 * @ApiOperation(value = "Token Purchase VTN callback Function", notes =
	 * "Need to get user token purchase VTN callback Function") public
	 * synchronized ResponseEntity<String> tokenPurchaseUsingVTN(
	 * 
	 * @ApiParam(value = "Required user token purchase VTN callback Function",
	 * required = true) String ipn_refno) { StatusResponseDTO statusResponseDTO
	 * = new StatusResponseDTO(); try {
	 * 
	 * System.out.println("Input XML---------->"+ipn_refno);
	 * System.out.println(ipn_refno); boolean isvalid =
	 * remcoUtils.validateVTNCallback(ipn_refno);
	 * System.out.println("VTN API Response------*******isvalid********"); if
	 * (!isvalid) { statusResponseDTO.setStatus(env.getProperty("failure"));
	 * statusResponseDTO.setMessage(env.getProperty("validate.ipn.failed"));
	 * return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
	 * HttpStatus.PARTIAL_CONTENT); }
	 * 
	 * statusResponseDTO.setStatus(env.getProperty("success"));
	 * statusResponseDTO.setMessage(env.getProperty("vtn.success")); return new
	 * ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
	 * HttpStatus.OK);
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * LOG.error("Problem in Token Purchase  : ", e);
	 * statusResponseDTO.setStatus(env.getProperty("failure"));
	 * statusResponseDTO.setMessage(env.getProperty("server.problem")); return
	 * new ResponseEntity<String>(new Gson().toJson(statusResponseDTO),
	 * HttpStatus.CONFLICT); } }
	 */

	@RequestMapping(path = "/vtn/callback", method = RequestMethod.POST)
	public ResponseEntity<String> process(WebRequest request) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		LOG.info(request.getParameter("ipn_refno"));
		String ipn_refno = request.getParameter("ipn_refno");
		try {
			if (ipn_refno != null && ipn_refno != "") {
				System.out.println("Input XML---------->" + ipn_refno);
				boolean isvalid = remcoUtils.validateVTNCallback(ipn_refno);
				if (!isvalid) {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("validate.ipn.failed"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}

				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("vtn.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

			}
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("get.ipn.failed"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Purchase  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/current/values/token", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Getting Current values for tokens", notes = "Need to Getting Current values for tokens")
	public synchronized ResponseEntity<String> gettingCurrentValuesForTokens(
			@ApiParam(value = "Required Getting Current values for tokens", required = true) @RequestBody UserRegisterDTO userRegisterDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			System.out.println("DTO : " + userRegisterDTO.getTypeOfPurchase());
			
			boolean isValid = currencyRateUtils.validateParams(userRegisterDTO);
			if(!isValid){
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			if(userRegisterDTO.getTypeOfPurchase() != null && userRegisterDTO.getNoOfTokens() != null) {
				
				boolean isUpdated = currencyRateUtils.getCurrencyRateForTokensInPurchase(userRegisterDTO);
				if (isUpdated) {
					statusResponseDTO.setStatus(env.getProperty("success"));
					statusResponseDTO.setMessage(env.getProperty("get.success"));
					statusResponseDTO.setAmount(userRegisterDTO);
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
				} else {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("get.failed"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
				
			} else if(userRegisterDTO.getAmount() != null && userRegisterDTO.getTypeOfPurchase() != null) {
				
				boolean isUpdated = currencyRateUtils.getCurrencyRateForTokensInPurchaseFromCrypto(userRegisterDTO);
				if (isUpdated) {
					statusResponseDTO.setStatus(env.getProperty("success"));
					statusResponseDTO.setMessage(env.getProperty("get.success"));
					statusResponseDTO.setNoOfTokens(userRegisterDTO);
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
				} else {
					statusResponseDTO.setStatus(env.getProperty("failure"));
					statusResponseDTO.setMessage(env.getProperty("get.failed"));
					return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
				}
			}
			
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("invalid.details.failed"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in registration  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/create/wallet", method = RequestMethod.POST, produces = { "application/json",
			"application/xml" })
	@ApiOperation(value = "Logout", notes = "Need to Logout")
	public synchronized ResponseEntity<String> createBTCandBCHwallet(
			@ApiParam(value = "Required user details", required = true) UserRegisterDTO userRegisterDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isUpdated = userRegisterService.createWallet(userRegisterDTO);
			if (isUpdated) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("wallet.success"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("wallet.failed"));
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
	@RequestMapping(value = "/get/soldtokens", method = RequestMethod.GET, produces = { "application/json",
			"application/xml" })
	@ApiOperation(value = "Logout", notes = "Need to Logout")
	public synchronized ResponseEntity<String> getSoldTokens(
			@ApiParam(value = "Required user details", required = true) TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			boolean isValid = tokenUserService.adminSoldBalance(tokenDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("balance.success"));
			statusResponseDTO.setTokenBalance(tokenDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in registration  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}
	
	@CrossOrigin
	@RequestMapping(value = "/register/airdropusers", method = RequestMethod.GET, produces = { "application/json",
			"application/xml" })
	@ApiOperation(value = "Logout", notes = "Need to Logout")
	public synchronized ResponseEntity<String> registerAirDropUsers(
			@ApiParam(value = "Required user details", required = true) UserRegisterDTO userDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			List<UserRegisterDTO> userList = userRegisterService.listAirdropUsers(userDTO);
			if (userList==null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isUsersRegistered = userRegisterService.userRegistration(userList);
			if(!isUsersRegistered)
			{
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage("Airdrop users Registration failed!");
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("Airdrop users registration Success!"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);


		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in registration  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}
	
	
	@CrossOrigin
	@RequestMapping(value = "/list/airdropusers", method = RequestMethod.POST, produces = { "application/json",
			"application/xml" })
	@ApiOperation(value = "Logout", notes = "Need to Logout")
	public synchronized ResponseEntity<String> ListAirDropUsers(
			@ApiParam(value = "Required user details", required = true)  @RequestBody UserRegisterDTO userDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			List<UserRegisterDTO> userList = userRegisterService.listAirdropUersWithFilter(userDTO);
			
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("success"));
			statusResponseDTO.setUserList(userList);
			statusResponseDTO.setTotalPages(userDTO.getTotalPages());
			statusResponseDTO.setTotalElements(userDTO.getTotalElements());
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in registration  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}
	
	@CrossOrigin
	@RequestMapping(value = "/earn/token", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "register account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> earnToken(
			@ApiParam(value = "Required user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO)
			throws Exception {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		
		boolean isValid = remcoUtils.ValidateInputs(userRegisterDTO);
		if(!isValid) {
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		
		boolean isValidEmailId = remcoUtils.validateEmail(userRegisterDTO.getEmailId());
		if (!isValidEmailId) {

			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("register.validate.email"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		boolean isMobileExists = userRegisterService.isAccountExistCheckByMobileNoForRegistrationViaVtnOrQuiz(userRegisterDTO.getMobileno(), userRegisterDTO.getReferSite());
		if (isMobileExists) {
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("mobileno.exist.for.register.vtnOrQuiz"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		boolean isEmailExists = userRegisterService.isAccountExistCheckByEmailIdForRegistrationViaVtnOrQuiz(userRegisterDTO);
		if (!isEmailExists) {
			boolean isRegister = userRegisterService.registerPromoUser(userRegisterDTO);
			if(!isRegister)
			{
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
		}
		else
		{
			remcoUtils.getUserDetails(userRegisterDTO);
		}
		boolean promoStatus = userRegisterService.checkPromotionStatus(userRegisterDTO);
		if(!promoStatus)
		{
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("promo.exist"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		
		boolean addPromo = userRegisterService.addPromo(userRegisterDTO);
		if(!addPromo)
		{
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("promo.exist"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}

		try {
			boolean isRegister = tokenUserService.promoTokenTransfer(userRegisterDTO);
			if (isRegister) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("promo.user.success"));
				statusResponseDTO.setReferralLink(userRegisterDTO.getReferralLink());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("promo.user.failed"));
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
	@RequestMapping(value = "/register/vtn", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "register account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> RegisterVTNUser(
			@ApiParam(value = "Required user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO)
			throws Exception {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		boolean isValidEmailId = remcoUtils.validateEmail(userRegisterDTO.getEmailId());
		if (!isValidEmailId) {

			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("register.validate.email"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		boolean isVerifiedPartner = userRegisterService.verifyAccessTokenForRegistration(userRegisterDTO);
		if(!isVerifiedPartner)
		{
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage("Invaid Partner!");
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		boolean isMobileExists = userRegisterService.isAccountExistCheckByMobileNoForRegistrationViaVtnOrQuiz(userRegisterDTO.getMobileno(), userRegisterDTO.getReferSite());
		if (isMobileExists) {
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("mobileno.exist.for.register.vtnOrQuiz"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		boolean isEmailExists = userRegisterService.isAccountExistCheckByEmailIdForRegistrationViaVtnOrQuiz(userRegisterDTO);
		if (!isEmailExists) {
			boolean isRegister = userRegisterService.registerPromoUser(userRegisterDTO);
			if(!isRegister)
			{
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
		}
		else
		{
			remcoUtils.getUserDetails(userRegisterDTO);
		}
		boolean promoStatus = userRegisterService.checkPromotionStatus(userRegisterDTO);
		if(!promoStatus)
		{
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("promo.exist"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		
		boolean addPromo = userRegisterService.addPromo(userRegisterDTO);
		if(!addPromo)
		{
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("promo.exist"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}

		try {
			boolean isRegister = tokenUserService.promoTokenTransfer(userRegisterDTO);
			if (isRegister) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("promo.user.success"));
				statusResponseDTO.setId(userRegisterDTO.getId());
				statusResponseDTO.setWalletAddress(userRegisterDTO.getWalletAddress());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("promo.user.failed"));
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
	@RequestMapping(value = "/token/balance/vtn", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "register account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> RetrieveTokenBalanceVTNUser(
			@ApiParam(value = "Required user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO)
			throws Exception {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try{
			TokenDTO tokenDTO = new TokenDTO();
		boolean isValidEmailId = remcoUtils.validateEmail(userRegisterDTO.getEmailId());
		if (!isValidEmailId) {

			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("register.validate.email"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		boolean isVerifiedPartner = userRegisterService.verifyAccessTokenForBalance(userRegisterDTO);
		if(!isVerifiedPartner)
		{
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage("Invaid Partner!");
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		boolean isEmailExists = userRegisterService.isAccountExistCheckByEmailId(userRegisterDTO.getEmailId());
		if (!isEmailExists) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("user.notexist"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		tokenDTO.setEmailId(userRegisterDTO.getEmailId());
		Double isValid = tokenUserService.balanceTokens(tokenDTO);
		if (isValid == null) {
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("balance.failed"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		statusResponseDTO.setStatus(env.getProperty("success"));
		statusResponseDTO.setMessage(env.getProperty("balance.success"));
		statusResponseDTO.setTokenBalance(tokenDTO);
		return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in registration  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}
	
	@CrossOrigin
	@RequestMapping(value = "/validate/user", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "register account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> validateUser(
			@ApiParam(value = "Required user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO)
			throws Exception {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();

		try{
		boolean isValid = remcoUtils.validateUserCheckParam(userRegisterDTO);
		if (!isValid) {
			// User registration validation failed
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
		}
		Integer isValidEmailId = userRegisterService.checkUserExistWithEmailAndMobile(userRegisterDTO);

			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setUserStatus(isValidEmailId);
			statusResponseDTO.setMessage(userRegisterDTO.getMessage());
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);


		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in registration  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}

	}

}
