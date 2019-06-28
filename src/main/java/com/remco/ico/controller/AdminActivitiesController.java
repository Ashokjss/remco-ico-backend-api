package com.remco.ico.controller;

import java.math.BigDecimal;
import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.remco.ico.DTO.KycDTO;
import com.remco.ico.DTO.SaleTypeDTO;
import com.remco.ico.DTO.StatusResponseDTO;
import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.UserRegisterDTO;
import com.remco.ico.service.AdminService;
import com.remco.ico.service.TokenUserService;
import com.remco.ico.service.UserRegisterService;
import com.remco.ico.solidityHandler.SolidityHandler;
import com.remco.ico.utils.RemcoUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/remco/api/admin")
@CrossOrigin
public class AdminActivitiesController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminActivitiesController.class);

	@Autowired
	private Environment env;
	@Autowired
	private RemcoUtils remcoUtils;
	@Autowired
	private TokenUserService tokenUserService;
	@Autowired
	private UserRegisterService userRegisterService;
	@Autowired
	private SolidityHandler solidityHandler;
	@Autowired
	private AdminService adminService;

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
			String isValidTokenBal = solidityHandler.isValidTokenBalForCrowdsale(tokenDTO);
			if (isValidTokenBal != "success") {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(isValidTokenBal);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			String securedKey = remcoUtils.keyGeneraterForTransfer(tokenDTO);
			if (securedKey!=null) {
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
	@RequestMapping(value = "/aprove/token/transfer", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "transfer", notes = "Need to Logout")
	public synchronized ResponseEntity<String> transferTokenApproval(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			
			KycDTO kycDTO = new KycDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isTokenTransfer = remcoUtils.validateApptoveTokenPrams(tokenDTO);
			if (!isTokenTransfer) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			kycDTO.setEmailId(tokenDTO.getEmailId());
			boolean isAdmin = adminService.validateAdmin(kycDTO.getEmailId());
			if (!isAdmin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.failed.admin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isTokenApproved = tokenUserService.isTokenTransApproved(tokenDTO);
			if (!isTokenApproved) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin.transfer.falied"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			String isValidTokenBal = solidityHandler.isValidTokenBalForCrowdsale(tokenDTO);
			if (isValidTokenBal != "success") {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(isValidTokenBal);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isTransfer = solidityHandler.ApproveTransfer(tokenDTO);
			   if (isTransfer) {

			    statusResponseDTO.setStatus(env.getProperty("success"));
			    statusResponseDTO.setMessage(env.getProperty("token.transfer.success"));
			    return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			   } else {
			    statusResponseDTO.setStatus(env.getProperty("failure"));
			    statusResponseDTO.setMessage(env.getProperty("transfer.token.failed"));
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
	@RequestMapping(value = "/list/kyc", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "transfer", notes = "Need to Logout")
	public synchronized ResponseEntity<String> listKyc(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestBody KycDTO kycDTO){
		// @ApiParam(value = "Required user details", required = true)
		// @RequestBody KycDTO kycDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			TokenDTO tokenDTO = new TokenDTO();

			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			kycDTO.setEmailId(tokenDTO.getEmailId());

			boolean isAdmin = adminService.validateAdmin(kycDTO.getEmailId());
			if (!isAdmin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.failed.admin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			List<KycDTO> kycList = adminService.kycList(kycDTO);
			if (kycList != null) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("kyc.list"));
				statusResponseDTO.setTotalPages(kycDTO.getTotalPages());
				statusResponseDTO.setTotalElements(kycDTO.getTotalElements());
				statusResponseDTO.setKycList(kycList);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("kyc.not.exist"));
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
	@RequestMapping(value = "/view/kyc", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "transfer", notes = "Need to Logout")
	public synchronized ResponseEntity<String> viewKyc(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestBody KycDTO kycDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			kycDTO.setEmailId(tokenDTO.getEmailId());
			boolean isAdmin = adminService.validateAdmin(kycDTO.getEmailId());
			if (!isAdmin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.failed.admin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			KycDTO kycInfo = adminService.getKycDetails(kycDTO);
			System.out.println("Kyc Value--------->" + kycInfo);
			if (kycInfo != null) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("kyc.user.success"));
				statusResponseDTO.setKycUserInfo(kycInfo);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("kyc.user.failure"));
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
	@RequestMapping(value = "/update/kyc", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "transfer", notes = "Need to Logout")
	public synchronized ResponseEntity<String> updateKyc(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestBody KycDTO kycDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isUpdateKyc = remcoUtils.validateUpdateKYCParams(kycDTO);
			if (!isUpdateKyc) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			kycDTO.setEmailId(tokenDTO.getEmailId());
			boolean isAdmin = adminService.validateAdmin(kycDTO.getEmailId());
			if (!isAdmin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.failed.admin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			/*String isStatusUpdated = adminService.validateRequestStatus(kycDTO);
			if (isStatusUpdated != "success") {

				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(kycDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}*/
			boolean isUpdated = adminService.updateKYC(kycDTO);
			if (isUpdated) {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(kycDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			} else {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("kyc.update.failure"));
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
	@RequestMapping(value = "/transaction/list", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "transaction History", notes = "Need to show Transaction History")
	public synchronized ResponseEntity<String> transactionList(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "transaction History", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			List<TokenDTO> transactionLists = tokenUserService.transactionHistory(tokenDTO,
					tokenDTO.getWalletAddress());
			if (transactionLists == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("history.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("history.success"));
			statusResponseDTO.setTotalPages(tokenDTO.getTotalPages());
			statusResponseDTO.setTotalElements(tokenDTO.getTotalElements());
			statusResponseDTO.setTransactionHistoryInfo(transactionLists);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in token transaction history  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/token/balance", method = RequestMethod.GET, produces = { "application/json" })
	@ApiOperation(value = "Token Balance", notes = "Need to show Token Balance")
	public synchronized ResponseEntity<String> tokenBalance(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token){
		//	@ApiParam(value = "Token Balance", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			KycDTO kycDTO = new KycDTO();
			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			kycDTO.setEmailId(tokenDTO.getEmailId());
			boolean isAdmin = adminService.validateAdmin(kycDTO.getEmailId());
			if (!isAdmin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.failed.admin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isValid = tokenUserService.adminTokenBalance(tokenDTO);
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
			LOG.error("Problem in token balance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/ether/balance", method = RequestMethod.GET, produces = { "application/json" })
	@ApiOperation(value = "Ether Balance", notes = "Need to show Ether Balance")
	public synchronized ResponseEntity<String> etherBalance(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token){
	//		@ApiParam(value = "Ether Balance", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			BigDecimal isValid = tokenUserService.etherBalance(tokenDTO);
			if (isValid == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("etherBalance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("etherBalance.success"));
			statusResponseDTO.setEtherBalanceInfo(tokenDTO);
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in ether balance  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/burn/token", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Token Burn", notes = "Need to show Token Burn")
	public synchronized ResponseEntity<String> burnTokens(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Token burn", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean validateAdmin = remcoUtils.validateAdmin(tokenDTO);
			if (!validateAdmin) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("validate.admin"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isSamePassword = remcoUtils.validatePasswordParam(tokenDTO);
			if (!isSamePassword) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("same.password"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean etherValidation = remcoUtils.etherValidation(tokenDTO);
			if (!etherValidation) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("ether.balance.burn"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean isValid = remcoUtils.ValidateTokenBalanceForBurn(tokenDTO);
			if (!isValid) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("balance.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			String isValids = tokenUserService.burnTokens(tokenDTO);
			if (isValids == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("burn.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("burn.success"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in Token Burn  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}


	@CrossOrigin
	@RequestMapping(value = "/get/saledates", method = RequestMethod.GET, produces = { "application/json" })
	@ApiOperation(value = "transfer", notes = "Need to Logout")
	public synchronized ResponseEntity<String> getSaleDates(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			TokenDTO tokens = tokenUserService.getSaleDates();
			if (tokens == null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("sale.date.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("sale.date.success"));
				statusResponseDTO.setGetSaleDates(tokens);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in updating the Token Value  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/update/saledates", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Update account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> updateSaleDates(
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

			boolean isAdminExist = adminService.validateAdmin(tokenDTO.getEmailId());
			if (!isAdminExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin.validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean status = tokenUserService.updateSaleDates(tokenDTO);
			if (!status) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("sale.update.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("sale.update.success"));
				statusResponseDTO.setGetSaleDates(tokenDTO);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in updating the Token Value  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/users/list", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Update account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> usersList(
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
			boolean isAdminExist = adminService.validateAdmin(tokenDTO.getEmailId());
			if (!isAdminExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin.validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			userRegisterDTO.setEmailId(tokenDTO.getEmailId());
			List<UserRegisterDTO> useList = userRegisterService.userList(userRegisterDTO);
			statusResponseDTO.setStatus(env.getProperty("success"));
			statusResponseDTO.setMessage(env.getProperty("user.list"));
			statusResponseDTO.setTotalPages(userRegisterDTO.getTotalPages());
			statusResponseDTO.setTotalElements(userRegisterDTO.getTotalElements());
			statusResponseDTO.setUserList(useList);
			statusResponseDTO.setTotalUserCount(userRegisterDTO.getUserCount());
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in updating the Token Value  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/get/offers", method = RequestMethod.GET, produces = { "application/json" })
	@ApiOperation(value = "Update account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> getOffers(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token){
			//@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			List<SaleTypeDTO>  tokenDTOo= tokenUserService.getTokenValues(tokenDTO);
			if (tokenDTOo==null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.rate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("token.rate.success"));
				statusResponseDTO.setGetTokenValue(tokenDTO);
				statusResponseDTO.setTokenRateInfo(tokenDTOo);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in updating the Token Value  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/update/offers", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Update account", notes = "Need to get user details")
	public synchronized ResponseEntity<String> updateOffers(
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

			boolean isAdminExist = adminService.validateAdmin(tokenDTO.getEmailId());
			if (!isAdminExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin.validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}

			boolean status = tokenUserService.updateOffers(tokenDTO);
			if (!status) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("token.update.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("token.update.success"));
				statusResponseDTO.setGetSaleDates(tokenDTO);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Problem in updating the Token Value  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/change/ip", method = RequestMethod.POST, produces = { "application/json" })
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
			boolean isTokenTransfer = remcoUtils.validateChangeIpParams(userRegisterDTO);
			if (!isTokenTransfer) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean changeIp = userRegisterService.changeAdminIpAddress(userRegisterDTO);
			if (!changeIp) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(userRegisterDTO.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("ip.change.success"));
				if(userRegisterDTO.getIpFlag().equals("VIEW"))
				{
				statusResponseDTO.setIpAddresses(userRegisterDTO.getIpAddresses());}
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
	@RequestMapping(value = "/view/user", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Reset Password", notes = "Need to Reset Password")
	public synchronized ResponseEntity<String> viewUserDetails(
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
			List<UserRegisterDTO> userList = userRegisterService.viewUserDetails(userRegisterDTO);
			if (userList==null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("register.validate.email"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("view.user.success"));
				statusResponseDTO.setUserList(userList);
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
	@RequestMapping(value = "/multiple/token/transfer", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Reset Password", notes = "Need to Reset Password")
	public synchronized ResponseEntity<String> MultipleTokenTranfer(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestBody TokenDTO tokenListDTO ) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			boolean isValidUser = remcoUtils.validateSession(token, tokenListDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isAdminExist = adminService.validateAdmin(tokenListDTO.getEmailId());
			if (!isAdminExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin.validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isValidParam = remcoUtils.validateMultipleTransferTokenPrams(tokenListDTO);
			if (!isValidParam) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("incorrectDetails"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			
			boolean isTokenTransfer = tokenUserService.multipleTokenTransfer(tokenListDTO);
			if(!isTokenTransfer)
			{
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("user.transfer.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			else
			{
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("user.transfer.success"));
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
	@RequestMapping(value = "/promo/list", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Reset Password", notes = "Need to Reset Password")
	public synchronized ResponseEntity<String> promoUserList(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			
			@RequestBody UserRegisterDTO userDTO){
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {

			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isAdminExist = adminService.validateAdmin(tokenDTO.getEmailId());
			if (!isAdminExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin.validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			List<UserRegisterDTO> userList  = userRegisterService.listPromoUsers(userDTO);
			if (userList==null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("promo.user.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			} else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("promo.user.list"));
				statusResponseDTO.setUserList(userList);
				statusResponseDTO.setTotalPages(userDTO.getTotalPages());
				statusResponseDTO.setTotalElements(userDTO.getTotalElements());
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
	@RequestMapping(value = "/manage/promotoken", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Reset Password", notes = "Need to Reset Password")
	public synchronized ResponseEntity<String> ManagePromoToken(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "Required user details", required = true) @RequestBody UserRegisterDTO userRegisterDTO ) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			
			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isAdminExist = adminService.validateAdmin(tokenDTO.getEmailId());
			if (!isAdminExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin.validate.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			String promoToken = remcoUtils.updateTokenValue(userRegisterDTO);
			if (promoToken==null) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("promo.token.failed "));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("promo.token.success"));
				statusResponseDTO.setPromoTokenValue(promoToken);
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LOG.error("Problem in getting tokenValue  : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/send/email/users", method = RequestMethod.POST, produces = { "application/json" })
	@ApiOperation(value = "Sending email to users", notes = "Sending email to users")
	public synchronized ResponseEntity<String> sendingEmailToUsers(
			@ApiParam(value = "Required authToken", required = true) @RequestHeader(value = "authToken") String token,
			@ApiParam(value = "user Id", required = true) @RequestBody UserRegisterDTO userRegisterDTO) {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			
			TokenDTO tokenDTO = new TokenDTO();
			boolean isValidUser = remcoUtils.validateSession(token, tokenDTO);
			if (!isValidUser) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("auth.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			boolean isAdminExist = adminService.addUsersToEmailList(userRegisterDTO, tokenDTO);
			if (!isAdminExist) {
				statusResponseDTO.setStatus(env.getProperty("failure"));
				statusResponseDTO.setMessage(env.getProperty("admin.email.details.saved.failed"));
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.PARTIAL_CONTENT);
			}
			else {
				statusResponseDTO.setStatus(env.getProperty("success"));
				statusResponseDTO.setMessage(env.getProperty("admin.email.details.saved.success"));
				statusResponseDTO.setNoOfUsers(tokenDTO.getUserCount());
				return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.OK);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			LOG.error("Problem in saving emails : ", e);
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(env.getProperty("server.problem"));
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.CONFLICT);
		}
	}		
}
