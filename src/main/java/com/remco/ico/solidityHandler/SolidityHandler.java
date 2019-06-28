package com.remco.ico.solidityHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
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
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.TokenTransferDTO;
import com.remco.ico.DTO.UserRegisterDTO;
import com.remco.ico.model.AirDropUserBonus;
import com.remco.ico.model.BitcoinCashInfo;
import com.remco.ico.model.BitcoinInfo;
import com.remco.ico.model.Config;
import com.remco.ico.model.ICOTokenInfo;
import com.remco.ico.model.PromoUsersInfo;
import com.remco.ico.model.PurchaseTokenInfo;
import com.remco.ico.model.ReferralInfo;
import com.remco.ico.model.RegisterInfo;
import com.remco.ico.model.ReservedTokenInfo;
import com.remco.ico.model.SaleDatesInfo;
import com.remco.ico.model.TokenDiscountInfo;
import com.remco.ico.model.TokenRateInfo;
import com.remco.ico.model.TransactionHistory;
import com.remco.ico.model.VTNPaymentInfo;
import com.remco.ico.repository.AirDropUserBonusRepository;
import com.remco.ico.repository.BitcoinCashInfoRepository;
import com.remco.ico.repository.BitcoinInfoRepository;
import com.remco.ico.repository.ConfigInfoRepository;
import com.remco.ico.repository.ICOTokenInfoRepository;
import com.remco.ico.repository.PromoUsersInfoRepository;
import com.remco.ico.repository.PurchaseTokenInfoRepository;
import com.remco.ico.repository.ReferralInfoRepository;
import com.remco.ico.repository.RegisterInfoRepository;
import com.remco.ico.repository.ReservedTokenInfoRepository;
import com.remco.ico.repository.SaleDatesInfoRepository;
import com.remco.ico.repository.TokenDiscountInfoRepository;
import com.remco.ico.repository.TokenRateInfoRepository;
import com.remco.ico.repository.TransactionInfoRepository;
import com.remco.ico.repository.VTNPaymentInfoRepository;
import com.remco.ico.service.EmailNotificationService;
import com.remco.ico.solidityToJava.RemcoToken;
import com.remco.ico.utils.BitcoinCashUtils;
import com.remco.ico.utils.BitcoinUtils;
import com.remco.ico.utils.CurrencyRateUtils;
import com.remco.ico.utils.EncryptDecrypt;
import com.remco.ico.utils.RemcoUtils;

@Service
public class SolidityHandler {

	@SuppressWarnings("unused")
	private static final int COUNT = 1;
	private TransactionReceipt transactionReceipt;

	// private final Web3j web3j = Web3j.build(new HttpService());

	// private final Web3j web3j = Web3j.build(new
	// HttpService("https://rinkeby.infura.io/"));

//	private final Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io"));

	private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io"));

	
	
	@Autowired
	private Environment env;
	@Autowired
	private RegisterInfoRepository registerInfoRepository;
	@Autowired
	private ConfigInfoRepository configInfoRepository;
	@Autowired
	private ICOTokenInfoRepository icoTokenInfoRepository;
	@Autowired
	private TransactionInfoRepository transactionInfoRepo;
	@Autowired
	private RemcoUtils remcoUtils;
	@Autowired
	private ReferralInfoRepository referralInfoRepository;
	@Autowired
	private PurchaseTokenInfoRepository purchaseTokenInfoRepository;
	@Autowired
	private TokenRateInfoRepository tokenRateInfoRepository;
	@Autowired
	private BitcoinCashUtils bitcoinCashUtils;
	@Autowired
	private BitcoinCashInfoRepository bitcoinCashInfoRepository;
	@Autowired
	private BitcoinUtils bitcoinUtils;
	@Autowired
	private BitcoinInfoRepository bitcoinInfoRepository;
	@Autowired
	private CurrencyRateUtils currencyRateUtils;
	@Autowired
	private ReservedTokenInfoRepository reservedTokenInfoRepository;
	@Autowired
	private VTNPaymentInfoRepository vtnPaymentInfoRepository;
	@Autowired
	private EmailNotificationService emailNotificationService;
	@Autowired
	private SaleDatesInfoRepository saleDatesInfoRepository;
	@Autowired
	private TokenDiscountInfoRepository tokenDiscountInfoRepository;
	@Autowired
	private PromoUsersInfoRepository promoUsersInfoRepository;
	@Autowired
	private AirDropUserBonusRepository airDropUserBonusRepository;

	Hashtable<String, String> parameters;

	private static final Logger LOG = LoggerFactory.getLogger(SolidityHandler.class);

	private static BigDecimal truncateDecimal(double x, int numberofDecimals) {
		if (x > 0) {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
		} else {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
		}
	}

	@SuppressWarnings("unused")
	public String isValidTokenBalForCrowdsale(TokenDTO tokenDTO) throws Exception {

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		DecimalFormat df = new DecimalFormat("0.00000000");

		tokenDTO.setFromAddress(registerInfo.getWalletFile());
		String WalletAddress = EncryptDecrypt.decrypt(registerInfo.getWalletAddress());
		String Password = EncryptDecrypt.decrypt(registerInfo.getWalletPassword());
		tokenDTO.setWalletAddress(WalletAddress);

		Credentials credentials = WalletUtils.loadCredentials(Password,
				config.getConfigValue() + "//" + tokenDTO.getWalletAddress());
		RemcoToken assetToken = RemcoToken.load(env.getProperty("token.address"), web3j, credentials,
				Contract.GAS_PRICE, Contract.GAS_LIMIT);

		BigInteger tokenBig = (BigInteger) assetToken.balanceOf(registerInfo.getWalletFile()).send();

		Double balanceOfTokenUser = tokenBig.doubleValue() / 100000000;

		/*
		 * List<TransactionHistory> txHistory = transactionInfoRepo.
		 * findByPaymentModeAndStatusAndFromAddressAndToAddress("HLTY", "0",
		 * registerInfo.getWalletFile(),tokenDTO.getToAddress());
		 * 
		 * List<TransactionHistory> txHistory1 = transactionInfoRepo.
		 * findByPaymentModeAndTxStatusAndFromAddressAndToAddress("ETH", "0",
		 * registerInfo.getWalletFile(),tokenDTO.getToAddress());
		 * 
		 * List<TransactionHistory> txHistory2 = transactionInfoRepo.
		 * findByPaymentModeAndAdminStatusAndFromAddressAndToAddress("ETH", "0",
		 * registerInfo.getWalletFile(),tokenDTO.getToAddress());
		 * 
		 * List<TransactionHistory> txHistory3 = transactionInfoRepo.
		 * findByPaymentModeAndStatusAndFromAddressAndToAddress("HLTY",
		 * "0",tokenDTO.getToAddress() , registerInfo.getWalletFile());
		 * 
		 * List<TransactionHistory> txHistory4 = transactionInfoRepo.
		 * findByPaymentModeAndTxStatusAndFromAddressAndToAddress("ETH", "0",
		 * tokenDTO.getToAddress() , registerInfo.getWalletFile());
		 * 
		 * List<TransactionHistory> txHistory5 = transactionInfoRepo.
		 * findByPaymentModeAndAdminStatusAndFromAddressAndToAddress("ETH", "0",
		 * tokenDTO.getToAddress() , registerInfo.getWalletFile());
		 * 
		 * 
		 * if(txHistory.size()>0 || txHistory1.size()>0 || txHistory2.size()>0 ||
		 * txHistory3.size()>0 || txHistory4.size()>0 || txHistory5.size()>0) {
		 * System.out.println("Avail ICO token---->"+balanceOfTokenUser); return
		 * "You won't be able to send any ETH or other tokens until the Status transaction gets confirmed"
		 * ;
		 * 
		 * }
		 */

		ICOTokenInfo icoToken = icoTokenInfoRepository.findIcoTokenInfoByIcoKey("ICOToken");
		ReservedTokenInfo reservedTokenInfo = reservedTokenInfoRepository
				.findReservedTokenInfoByReserveKey("ReservedToken");

		Double token = tokenDTO.getRequestToken();

		System.out.println("Token Request----->" + tokenDTO.getRequestToken());
		if (BigInteger.ZERO.equals(token)) {

			return "Please enter valid token";
		}
		if (balanceOfTokenUser <= 0) {
			return "Insufficient Token Balance";
		} else {

			if (registerInfo.getUserType() == 1) {
				if (tokenDTO.getTokenFlag().equals("NONICO")) {
					// balanceOfTokenUser = balanceOfTokenUser -
					// icoToken.getIcoAvail().doubleValue();
				}
				if (tokenDTO.getTokenFlag().equals("RESERVED")) {
					balanceOfTokenUser = balanceOfTokenUser - reservedTokenInfo.getReservedAvail().doubleValue();
				}
			}
			System.out.println("Token Request--tokenBal--->" + balanceOfTokenUser);
			// int count = tokenBal.compareTo(tokenDTO.getRequestToken());

			if (tokenDTO.getRequestToken() > balanceOfTokenUser) {
				BigDecimal bd = truncateDecimal(balanceOfTokenUser, 4);
				balanceOfTokenUser = bd.doubleValue();
				return "You have only " + BigDecimal.valueOf(balanceOfTokenUser).toPlainString() + " tokens left";
			}

			return "success";
		}
	}

	@SuppressWarnings("unused")
	public boolean manualTokenTransfer(TokenDTO tokenDTO) throws Exception {
		transactionReceipt = new TransactionReceipt();
		TransactionHistory transactionHistory = new TransactionHistory();
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		DecimalFormat df = new DecimalFormat("0.00000000");
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
		CompletableFuture.supplyAsync(() -> {
			Credentials credentials;
			try {
				LOG.info("calling blackchain for manual token transfer");
				transactionHistory.setFromAddress(tokenDTO.getFromAddress());
				transactionHistory.setToAddress(tokenDTO.getToAddress());
				transactionHistory.setAmount(new BigDecimal(0));
				transactionHistory.setToken(tokenDTO.getRequestToken());
				transactionHistory.setPaymentMode(env.getProperty("admin.payment"));
				transactionHistory.setTransferDate(new Date());
				transactionHistory.setEmailId(tokenDTO.getEmailId());
				transactionHistory.setStatus(env.getProperty("status.pending"));
				transactionInfoRepo.save(transactionHistory);
				if (transactionHistory != null) {
					credentials = WalletUtils.loadCredentials(EncryptDecrypt.decrypt(registerInfo.getWalletPassword()),
							config.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getWalletAddress()));
					RemcoToken assetToken = RemcoToken.load(env.getProperty("token.address"), web3j, credentials,
							BigInteger.valueOf(12000000000L), BigInteger.valueOf(86245));
//							Contract.GAS_PRICE, Contract.GAS_LIMIT);
					BigDecimal amount = BigDecimal.valueOf(tokenDTO.getRequestToken());

					amount = amount.multiply(new BigDecimal("100000000"));
					BigInteger transVal = amount.toBigInteger();

					// BigInteger transVal =
					// BigDecimal.valueOf(doublevalue).toBigInteger();
					transactionReceipt = assetToken.transfer(tokenDTO.getToAddress(), transVal).sendAsync().get();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
			return "call blackchain";
		}).thenAccept(product -> {
			LOG.info("after calling blackchain then transaction insert into db");
			// if
			// (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success")))
			// {
			if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
				if (transactionHistory != null) {
					transactionHistory.setStatus(env.getProperty("status.success"));
					transactionInfoRepo.save(transactionHistory);
					// return true;
				}
			} else if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
				// else if
				// (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed")))
				// {
				if (transactionHistory != null) {
					transactionHistory.setStatus(env.getProperty("status.failed"));
					transactionInfoRepo.save(transactionHistory);
					// return true;
				}
			}
		});
		return true;
	}

	public List<TokenDTO> transactionHistory(TokenDTO tokenDTO, String etherWalletAddress) throws Exception {

		List<TokenDTO> transactionList = new ArrayList<TokenDTO>();
		Sort sort = new Sort(new Sort.Order(Direction.ASC, "transferDate"));
		Pageable pageable = new PageRequest(tokenDTO.getPageNum(), tokenDTO.getPageSize(), sort);
		Page<TransactionHistory> transactionHistory = transactionInfoRepo
				.findByFromAddressOrToAddressOrderByTransferDateDesc(etherWalletAddress, etherWalletAddress, pageable);
		LOG.info("Transaction History:::::::::::" + transactionHistory.toString());
		tokenDTO.setTotalPages(transactionHistory.getTotalPages());
		tokenDTO.setTotalElements(transactionHistory.getTotalElements());

		if (tokenDTO.getTransactionType() == 0) {

			for (TransactionHistory transactionInfo : transactionHistory) {

				TokenDTO transactions = new TokenDTO();
				transactions.setFromAddress(transactionInfo.getFromAddress());
				transactions.setToAddress(transactionInfo.getToAddress());
				transactions.setTransferAmount(transactionInfo.getAmount().doubleValue());
				transactions.setTransferDate(transactionInfo.getTransferDate());
				// transactions.setTransactionMode(transactionInfo.getTransactionMode());
				transactions.setTransferStatus(transactionInfo.getStatus());
				transactions.setTokens(transactionInfo.getToken());
				transactionList.add(transactions);
			}

		} else if (tokenDTO.getTransactionType() == 1) {

			for (TransactionHistory transactionInfo : transactionHistory) {

				if (tokenDTO.getWalletAddress().equalsIgnoreCase(transactionInfo.getFromAddress())) {

					TokenDTO transactions = new TokenDTO();
					transactions.setFromAddress(transactionInfo.getFromAddress());
					transactions.setToAddress(transactionInfo.getToAddress());
					transactions.setTransferAmount(transactionInfo.getAmount().doubleValue());
					transactions.setTransferDate(transactionInfo.getTransferDate());
					// transactions.setTransactionMode(transactionInfo.getTransactionMode());
					transactions.setTransferStatus(transactionInfo.getStatus());
					transactions.setTokens(transactionInfo.getToken());
					transactionList.add(transactions);
				}
			}

		} else if (tokenDTO.getTransactionType() == 2) {

			for (TransactionHistory transactionInfo : transactionHistory) {

				if (tokenDTO.getWalletAddress().equalsIgnoreCase(transactionInfo.getToAddress())) {

					TokenDTO transactions = new TokenDTO();
					transactions.setFromAddress(transactionInfo.getFromAddress());
					transactions.setToAddress(transactionInfo.getToAddress());
					transactions.setTransferAmount(transactionInfo.getAmount().doubleValue());
					transactions.setTransferDate(transactionInfo.getTransferDate());
					// transactions.setTransactionMode(transactionInfo.getTransactionMode());
					transactions.setTransferStatus(transactionInfo.getStatus());
					transactions.setTokens(transactionInfo.getToken());
					transactionList.add(transactions);
				}
			}
		}
		return transactionList;
	}

	public Double balanceTokens(TokenDTO tokenDTO) throws Exception {

		Config configInfo = configInfoRepository.findConfigByConfigKey("walletFile");

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());

		String walletPassword = registerInfo.getWalletPassword();

		String dwalletPassword = EncryptDecrypt.decrypt(walletPassword);
		LOG.info("Decrypted wallet Password" + dwalletPassword);
		String fromAddress = remcoUtils.getWalletAddress(configInfo.getConfigValue(),
				EncryptDecrypt.decrypt(registerInfo.getWalletAddress()));

		LOG.info("configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getWalletAddress())");

		Credentials credentials = WalletUtils.loadCredentials(dwalletPassword,
				configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getWalletAddress()));
		RemcoToken assetToken = RemcoToken.load(this.env.getProperty("token.address"), web3j, credentials,
				Contract.GAS_PRICE, Contract.GAS_LIMIT);

		System.out.println("the from address---->" + fromAddress);
		BigInteger amount = assetToken.balanceOf(fromAddress).send();

		double amt = amount.doubleValue() / 100000000;

		tokenDTO.setTokenAmount(amt);
		tokenDTO.setId(registerInfo.getId());
		tokenDTO.setWalletAddress(registerInfo.getWalletFile());
		tokenDTO.setTokenAmountString(truncateDecimal(amt, 8).toPlainString());
		return amount.doubleValue() / 100000000;
	}

	public BigDecimal etherBalance(TokenDTO tokenDTO) throws Exception {

		Config configInfo = configInfoRepository.findConfigByConfigKey("walletfile");

		RegisterInfo register = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());

		String walletAddress;
		if (register != null) {
			String decryptWalletAddress = EncryptDecrypt.decrypt(register.getWalletAddress());
			walletAddress = remcoUtils.getWalletAddress(configInfo.getConfigValue(), decryptWalletAddress);

			if (walletAddress == null) {
				return null;
			}
			LOG.info("Ether Address : " + walletAddress);
			EthGetBalance ethGetBalance;
			ethGetBalance = web3j.ethGetBalance(walletAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
			BigInteger wei = ethGetBalance.getBalance();
			LOG.info("ether bal:::::::::::" + wei);
			BigDecimal amountCheck = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
			LOG.info("ether bal:::::::::::" + amountCheck);
			tokenDTO.setEtherBalance(amountCheck);

			return amountCheck;
		} else
			return null;
	}

	public String burnTokens(TokenDTO tokenDTO) {

		transactionReceipt = new TransactionReceipt();

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());

		Config configInfo = configInfoRepository.findConfigByConfigKey("walletFile");

		LOG.info("configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getEtherWalletAddress())");

		CompletableFuture.supplyAsync(() -> {
			Credentials credentials;
			try {

				credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),

						configInfo.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getWalletAddress()));

				RemcoToken assetToken = RemcoToken.load(this.env.getProperty("token.address"), web3j, credentials,
						Contract.GAS_PRICE, Contract.GAS_LIMIT);

				if (assetToken != null) {
					LOG.info("Amount" + tokenDTO.getTokens() * 100000000);

					BigInteger amt = new BigDecimal(tokenDTO.getTokens() * 100000000).toBigInteger();

					transactionReceipt = assetToken.burn(amt).send();

					LOG.info("transactionReceipt ::::::::::::::::::" + ":::::::"
							+ transactionReceipt.getTransactionHash() + transactionReceipt.getGasUsed()
							+ "::::::::::::::::" + transactionReceipt.getStatus() + "::::::::::::::::::"
							+ transactionReceipt.getCumulativeGasUsed());
				}
			} catch (Exception e) {

				e.printStackTrace();
			}

			return "call blackchain";
		}).thenAccept(product -> {
			LOG.info("After calling the callback function");
			if (transactionReceipt.getStatus().equalsIgnoreCase("0x1")) {

				if (tokenDTO.getBurnFlag().equals("ICO")) {
					ICOTokenInfo icoTokenInfo = icoTokenInfoRepository.findIcoTokenInfoByIcoKey("ICOToken");
					{
						Double avail = icoTokenInfo.getIcoAvail() - tokenDTO.getTokens();
						icoTokenInfo.setIcoAvail(avail);
					}
				}

				LOG.info("Burn Success");

			}

		});
		return transactionReceipt.toString();
	}

	@SuppressWarnings("unused")
	public boolean ApproveTransfer(TokenDTO tokenDTO) throws Exception {

		System.out.println("Trasfer coin aPI--------");
		// TokenInfo uvcoinTokenInfos =
		// tokenInfoRepository.findUvcoinUserInfoById(tokenDTO.getTokenTransId());

		System.out.println("Token transfer Id----->" + Integer.parseInt(tokenDTO.getTokenTransId()));

		TransactionHistory transactionHistory = transactionInfoRepo
				.findOne(Integer.parseInt(tokenDTO.getTokenTransId()));
		transactionReceipt = new TransactionReceipt();
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		DecimalFormat df = new DecimalFormat("0.00000000");
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
		CompletableFuture.supplyAsync(() -> {
			Credentials credentials;
			try {
				LOG.info("calling blackchain for manual token transfer");
				if (transactionHistory != null) {
					credentials = WalletUtils.loadCredentials(EncryptDecrypt.decrypt(registerInfo.getWalletPassword()),
							config.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getWalletAddress()));
					RemcoToken assetToken = RemcoToken.load(env.getProperty("token.address"), web3j, credentials,
							Contract.GAS_PRICE, Contract.GAS_LIMIT);
					BigDecimal amount = BigDecimal.valueOf(tokenDTO.getRequestToken());

					amount = amount.multiply(new BigDecimal("100000000"));
					BigInteger transVal = amount.toBigInteger();

					// BigInteger transVal =
					// BigDecimal.valueOf(doublevalue).toBigInteger();
					transactionReceipt = assetToken.transfer(tokenDTO.getToAddress(), transVal).sendAsync().get();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
			return "call blackchain";
		}).thenAccept(product -> {
			LOG.info("after calling blackchain then transaction insert into db");
			// if
			// (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success")))
			// {
			if (transactionReceipt != null) {
				if (transactionHistory != null) {
					transactionHistory.setStatus(env.getProperty("status.success"));
					transactionHistory.setTokenStatus(env.getProperty("status.success"));
					transactionInfoRepo.save(transactionHistory);

					ReservedTokenInfo reserved = reservedTokenInfoRepository.findOne(1);
					Double tokenBuy = reserved.getReservedAvail();
					Double tokenSold = reserved.getReservedSold();

					if (tokenDTO.getTokenFlag().equals("RESERVED")) {
						tokenBuy = reserved.getReservedAvail() - transactionHistory.getToken();
						tokenSold = reserved.getReservedSold() + transactionHistory.getToken();
					}
					reserved.setReservedAvail(tokenBuy);
					reserved.setReservedSold(tokenSold);
					reservedTokenInfoRepository.save(reserved);

					// return true;
				}
			} else if (transactionReceipt != null) {
				// else if
				// (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed")))
				// {
				if (transactionHistory != null) {
					transactionHistory.setStatus(env.getProperty("status.failed"));
					transactionHistory.setTokenStatus(env.getProperty("status.failed"));
					transactionInfoRepo.save(transactionHistory);
					// return true;
				}
			}
		});
		return true;
	}

	public boolean referralTokens(TokenDTO tokenDTO) {

		System.out.println("Token Amount : " + tokenDTO.getTokenAmount());
		Double purchaseCoins = tokenDTO.getTokenAmount();

		TokenDiscountInfo tokenRateInfo = tokenDiscountInfoRepository.findOne(1);
		System.out.println("Percentage : " + tokenRateInfo.getReferralDisc());
		Double percentage = tokenRateInfo.getReferralDisc().doubleValue();

		Double referralAmount = (purchaseCoins * percentage) / 100;
		System.out.println("Referral Amount : " + referralAmount);

		if (percentage != null) {
			RegisterInfo registerInfo = registerInfoRepository
					.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());

			Integer referralId = registerInfo.getReferralId();
			if (referralId != null) {
				RegisterInfo registerInfo1 = registerInfoRepository.findRegisterInfoById(referralId);
				if (registerInfo1 != null) {
					System.out.println("Referral Email Id : " + registerInfo1.getEmailId());

					// ReferralInfo referralInfo1 =
					// referralInfoRepository.findByEmailId(registerInfo1.getEmailId());
					// if (referralInfo1 != null) {
					// referralInfo1.setReferralPercentage(referralInfo1.getReferralPercentage()
					// + percentage);
					// referralInfo1.setReferralTokens(referralInfo1.getReferralTokens()
					// + referralAmount);
					// referralInfoRepository.save(referralInfo1);
					// return true;
					// } else {
					ReferralInfo referralInfo = new ReferralInfo();
					// Email Id of purchased Users
					referralInfo.setEmailId(registerInfo.getEmailId());
					// Email Id of Referred Users
					referralInfo.setReferralEmailId(registerInfo1.getEmailId());
					// Id of Referred Users
					referralInfo.setReferralId(registerInfo.getReferralId());
					referralInfo.setReferralPercentage(percentage);
					referralInfo.setReferralTokens(referralAmount);
					referralInfo.setReferralPurchaseDate(new Date());
					referralInfo.setReferralPurchasedTokens(purchaseCoins);
					referralInfo.setStatus(0);
					referralInfoRepository.save(referralInfo);
					return true;
					// }
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unused")
	public boolean tokenPurchase(TokenDTO tokenDTO) throws IOException, CipherException, Exception {
		transactionReceipt = new TransactionReceipt();
		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());

		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		if (registerInfo != null) {

			String decryptedWalletAddress = EncryptDecrypt.decrypt(registerInfo.getWalletAddress());
			String etherWalletAddress = remcoUtils.getWalletAddress(config.getConfigValue(), decryptedWalletAddress);

			Credentials credentials = WalletUtils.loadCredentials(tokenDTO.getWalletPassword(),
					config.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getWalletAddress()));

			/*************** ETH Purchase ****************/

			if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("eth.payment"))) {
				TokenRateInfo tokenRateInfo = tokenRateInfoRepository.findBySaletype(tokenDTO.getSaleType());

				Double etherAmount = currencyRateUtils.getCryptoRateFromDollar(tokenDTO);
				// Getting Value of the 1 USD rate of Ether

				System.out.println("Current Rate of 1 USD to Ether : " + etherAmount);
				Double etherValueForOneToken = etherAmount * tokenRateInfo.getTokenValue().doubleValue();
				System.out.println("Rate of 1 Tokens in Ether : " + etherValueForOneToken);

				Double etherValue = tokenDTO.getTokenAmount() * etherValueForOneToken;

				System.out.println("current Ether Value : " + etherValue);
				BigDecimal eth = new BigDecimal(etherValue);

				System.out.println(eth);
				DecimalFormat df = new DecimalFormat("#.########");
				BigDecimal ether = new BigDecimal(df.format(eth));

				PurchaseTokenInfo purchaseTokenInfo = new PurchaseTokenInfo();

				CompletableFuture.supplyAsync(() -> {
					try {
						LOG.info("calling blackchain for purchase token transfer");

						purchaseTokenInfo.setEmailId(tokenDTO.getEmailId());
						purchaseTokenInfo.setEtherWalletAddress(etherWalletAddress);
						purchaseTokenInfo.setCryptoAmount(ether.doubleValue());
						purchaseTokenInfo.setPurchaseTokens(tokenDTO.getTokenAmount());
						purchaseTokenInfo.setTypeOfPurchase(env.getProperty("eth.payment"));
						purchaseTokenInfo.setPurchasedDate(new Date());
						purchaseTokenInfo.setIsVesting(tokenDTO.getIsVesting());
						if (tokenDTO.getIsVesting() == 1) {
							purchaseTokenInfo.setSchoolName(tokenDTO.getSchoolName());
							purchaseTokenInfo.setDateOfBirth(tokenDTO.getDateOfBirth());
						}
						purchaseTokenInfo.setTransferType(1);
						purchaseTokenInfo.setBtcWalletAddress("");
						purchaseTokenInfo.setBchWalletAddress("");
						purchaseTokenInfo.setAsynchStatus("pending");
						purchaseTokenInfoRepository.save(purchaseTokenInfo);

						if (purchaseTokenInfo.getId() != null) {

							transactionReceipt = Transfer.sendFunds(web3j, credentials, env.getProperty("main.address"),
									ether, Convert.Unit.ETHER).send();
							LOG.info("TransactionReceipt : " + transactionReceipt.getStatus());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//
					return "call blackchain";
				}).thenAccept(product -> {
					LOG.info("after calling blackchain then transaction insert into db");
					if (transactionReceipt.getStatus().equals("0x1")) {

						if (registerInfo.getReferralId() != null && registerInfo.getReferralId() != 0) {

							boolean referral = referralTokens(tokenDTO);
							if (referral) {
								System.out.println("Referral added");
							} else {
								System.out.println("Referral not added");
							}
						}

						boolean isEmailSent = emailNotificationService.sendEmailforPurchaseRequest(
								tokenDTO.getEmailId(), "Purchase Info from REMCO", registerInfo.getFirstName(),
								tokenDTO.getTokenAmount(), tokenDTO.getTypeOfPurchase());

						purchaseTokenInfo.setAsynchStatus("success");
						purchaseTokenInfoRepository.save(purchaseTokenInfo);
					} else if (transactionReceipt.getStatus().equals("0x0")) {

						purchaseTokenInfo.setAsynchStatus("failed");
						purchaseTokenInfo.setTransferType(-1);
						purchaseTokenInfoRepository.save(purchaseTokenInfo);
						// return true;

					}
				});

				// transactionReceipt = Transfer
				// .sendFunds(web3j, credentials,
				// env.getProperty("main.address"), ether, Convert.Unit.ETHER)
				// .send();
				// LOG.info("TransactionReceipt : " +
				// transactionReceipt.getStatus());

				// if (transactionReceipt.getStatus().equalsIgnoreCase("0x1")) {
				//
				// if (registerInfo.getReferralId() != null && registerInfo.getReferralId() !=
				// 0) {
				//
				// boolean referral = referralTokens(tokenDTO);
				// if (referral) {
				// System.out.println("Referral added");
				// } else {
				// System.out.println("Referral not added");
				// }
				// }

				// PurchaseTokenInfo purchaseTokenInfo1 =
				// purchaseTokenInfoRepository.findByEmailIdAndTypeOfPurchase(tokenDTO.getEmailId(),
				// tokenDTO.getTypeOfPurchase());
				// if (purchaseTokenInfo1 != null &&
				// purchaseTokenInfo1.getTypeOfPurchase() == 1) {
				//
				// purchaseTokenInfo1.setEtherWalletAddress(etherWalletAddress);
				// purchaseTokenInfo1.setTypeOfPurchase(1);
				// purchaseTokenInfo1.setEthAmount(purchaseTokenInfo1.getEthAmount()
				// + ether.doubleValue());
				// purchaseTokenInfo1
				// .setPurchaseTokens(purchaseTokenInfo1.getPurchaseTokens()
				// + tokenDTO.getTokenAmount());
				// purchaseTokenInfo1.setPurchasedDate(new Date());
				//
				// if (tokenRateInfo1.getDiscount() != null) {
				// Double freeCoins = (tokenDTO.getTokenAmount() *
				// tokenRateInfo1.getDiscount().doubleValue()) / 100;
				// purchaseTokenInfo1.setFreeTokens(purchaseTokenInfo1.getFreeTokens()
				// + freeCoins);
				// }
				//
				// purchaseTokenInfo1.setTransferType(1);
				// purchaseTokenInfoRepository.save(purchaseTokenInfo1);
				// return true;
				// } else {

				// PurchaseTokenInfo purchaseTokenInfo = new PurchaseTokenInfo();
				//
				// purchaseTokenInfo.setEmailId(tokenDTO.getEmailId());
				// purchaseTokenInfo.setEtherWalletAddress(etherWalletAddress);
				// purchaseTokenInfo.setCryptoAmount(ether.doubleValue());
				// purchaseTokenInfo.setPurchaseTokens(tokenDTO.getTokenAmount());
				// purchaseTokenInfo.setTypeOfPurchase(env.getProperty("eth.payment"));
				// purchaseTokenInfo.setPurchasedDate(new Date());
				//
				// if (tokenDTO.getIsVesting() == 1) {
				// purchaseTokenInfo.setSchoolName(tokenDTO.getSchoolName());
				// purchaseTokenInfo.setDateOfBirth(tokenDTO.getDateOfBirth());
				// }
				// purchaseTokenInfo.setTransferType(1);
				// purchaseTokenInfo.setBtcWalletAddress("");
				// purchaseTokenInfo.setBchWalletAddress("");
				//
				// purchaseTokenInfoRepository.save(purchaseTokenInfo);
				//
				// boolean isEmailSent =
				// emailNotificationService.sendEmailforPurchaseRequest(tokenDTO.getEmailId(),
				// "Purchase Info from REMCO", registerInfo.getFirstName(),
				// tokenDTO.getTokenAmount(),
				// tokenDTO.getTypeOfPurchase());
				return true;

				// }

				/*************** BCH Purchase ****************/

			} else if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("bch.payment"))) {

				PurchaseTokenInfo purchaseTokenInfo = new PurchaseTokenInfo();
				BitcoinCashInfo bitcoinCashInfo = bitcoinCashInfoRepository.findByEmailId(tokenDTO.getEmailId());

				CompletableFuture.supplyAsync(() -> {
					try {

						LOG.info("calling blackchain for purchase token transfer");

						purchaseTokenInfo.setEmailId(tokenDTO.getEmailId());
						purchaseTokenInfo.setBchWalletAddress(bitcoinCashInfo.getBchWalletAddress());
						purchaseTokenInfo.setEtherWalletAddress(etherWalletAddress);

						purchaseTokenInfo.setPurchaseTokens(tokenDTO.getTokenAmount());
						purchaseTokenInfo.setTypeOfPurchase(env.getProperty("bch.payment"));
						purchaseTokenInfo.setPurchasedDate(new Date());
						// TokenRateInfo tokenRateInfo1 =
						// tokenRateInfoRopository.findById(1);
						// if (tokenRateInfo1.getDiscount() != null) {
						// Double freeCoins = (tokenDTO.getTokenAmount() *
						// tokenRateInfo1.getDiscount().doubleValue()) / 100;
						// purchaseTokenInfo.setFreeTokens(freeCoins);
						// }
						purchaseTokenInfo.setIsVesting(tokenDTO.getIsVesting());
						if (tokenDTO.getIsVesting() == 1) {
							purchaseTokenInfo.setSchoolName(tokenDTO.getSchoolName());
							purchaseTokenInfo.setDateOfBirth(tokenDTO.getDateOfBirth());
						}
						purchaseTokenInfo.setTransferType(2);
						purchaseTokenInfo.setBtcWalletAddress("");
						purchaseTokenInfo.setAsynchStatus("pending");
						purchaseTokenInfoRepository.save(purchaseTokenInfo);

						if (purchaseTokenInfo.getId() != null) {
							String isTransfer = bitcoinCashUtils.bchSendCoins(tokenDTO);
							LOG.info("Status BCH : " + isTransfer);
							if (isTransfer != null) {
								tokenDTO.setStatus("true");
							} else {
								tokenDTO.setStatus("false");
							}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//
					return "call back";
				}).thenAccept(product -> {
					LOG.info("after calling blackchain then transaction insert into db");
					if (tokenDTO.getStatus().equalsIgnoreCase("true")) {

						if (registerInfo.getReferralId() != null && registerInfo.getReferralId() != 0) {

							boolean referral = referralTokens(tokenDTO);
							if (referral) {
								System.out.println("Referral added");
							} else {
								System.out.println("Referral not added");
							}
						}
						boolean isEmailSent = emailNotificationService.sendEmailforPurchaseRequest(
								tokenDTO.getEmailId(), "Purchase Info from REMCO", registerInfo.getFirstName(),
								tokenDTO.getTokenAmount(), tokenDTO.getTypeOfPurchase());
						purchaseTokenInfo.setCryptoAmount(tokenDTO.getBchAmount());
						purchaseTokenInfo.setAsynchStatus("success");
						purchaseTokenInfoRepository.save(purchaseTokenInfo);
					} else if (tokenDTO.getStatus().equalsIgnoreCase("false")) {

						purchaseTokenInfo.setAsynchStatus("failed");
						purchaseTokenInfo.setTransferType(-1);
						purchaseTokenInfoRepository.save(purchaseTokenInfo);
						// return true;
					}
				});

				// if (isTransfer != null) {
				//
				// if (registerInfo.getReferralId() != null && registerInfo.getReferralId() !=
				// 0) {
				//
				// boolean referral = referralTokens(tokenDTO);
				// if (referral) {
				// System.out.println("Referral added");
				// } else {
				// System.out.println("Referral not added");
				// }
				// }
				//
				// BitcoinCashInfo bitcoinCashInfo =
				// bitcoinCashInfoRepository.findByEmailId(tokenDTO.getEmailId());

				// PurchaseTokenInfo purchaseTokenInfo1 =
				// purchaseTokenInfoRepository.findByEmailIdAndTypeOfPurchase(tokenDTO.getEmailId(),
				// tokenDTO.getTypeOfPurchase());
				// if (purchaseTokenInfo1 != null &&
				// purchaseTokenInfo1.getTypeOfPurchase() == 2) {
				// purchaseTokenInfo1.setEtherWalletAddress(etherWalletAddress);
				//
				// purchaseTokenInfo1.setBchWalletAddress(bitcoinCashInfo.getBchWalletAddress());
				// purchaseTokenInfo1.setTypeOfPurchase(2);
				// purchaseTokenInfo1.setBchAmount(purchaseTokenInfo1.getBchAmount()
				// + tokenDTO.getBchAmount());
				// purchaseTokenInfo1
				// .setPurchaseTokens(purchaseTokenInfo1.getPurchaseTokens()
				// + tokenDTO.getTokenAmount());
				// purchaseTokenInfo1.setPurchasedDate(new Date());
				// TokenRateInfo tokenRateInfo1 =
				// tokenRateInfoRopository.findById(1);
				// if (tokenRateInfo1.getDiscount() != null) {
				// Double freeCoins = (tokenDTO.getTokenAmount() *
				// tokenRateInfo1.getDiscount().doubleValue()) / 100;
				// purchaseTokenInfo1.setFreeTokens(purchaseTokenInfo1.getFreeTokens()
				// + freeCoins);
				// }
				// purchaseTokenInfo1.setTransferType(2);
				// purchaseTokenInfoRepository.save(purchaseTokenInfo1);
				// return true;
				// } else {
				return true;
				// }
				// }

				/*************** BTC Purchase ****************/

			} else if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("btc.payment"))) {

				PurchaseTokenInfo purchaseTokenInfo = new PurchaseTokenInfo();
				BitcoinInfo bitcoinInfo = bitcoinInfoRepository.findByEmailId(tokenDTO.getEmailId());

				CompletableFuture.supplyAsync(() -> {
					try {

						LOG.info("calling blackchain for purchase token transfer");

						purchaseTokenInfo.setEmailId(tokenDTO.getEmailId());
						purchaseTokenInfo.setBtcWalletAddress(bitcoinInfo.getBtcWalletAddress());
						purchaseTokenInfo.setEtherWalletAddress(etherWalletAddress);

						purchaseTokenInfo.setPurchaseTokens(tokenDTO.getTokenAmount());
						purchaseTokenInfo.setTypeOfPurchase(env.getProperty("btc.payment"));
						purchaseTokenInfo.setPurchasedDate(new Date());
						// TokenRateInfo tokenRateInfo1 =
						// tokenRateInfoRopository.findById(1);
						// if (tokenRateInfo1.getDiscount() != null) {
						// Double freeCoins = (tokenDTO.getTokenAmount() *
						// tokenRateInfo1.getDiscount().doubleValue())
						// / 100;
						// purchaseTokenInfo.setFreeTokens(freeCoins);
						// }
						purchaseTokenInfo.setIsVesting(tokenDTO.getIsVesting());
						if (tokenDTO.getIsVesting() == 1) {
							purchaseTokenInfo.setSchoolName(tokenDTO.getSchoolName());
							purchaseTokenInfo.setDateOfBirth(tokenDTO.getDateOfBirth());
						}
						purchaseTokenInfo.setTransferType(3);
						purchaseTokenInfo.setBchWalletAddress("");
						purchaseTokenInfo.setAsynchStatus("pending");
						purchaseTokenInfoRepository.save(purchaseTokenInfo);

						if (purchaseTokenInfo.getId() != null) {
							String isTransfer = bitcoinUtils.btcSendCoins(tokenDTO);
							System.out.println("Status : " + isTransfer);
							LOG.info("Status BTC : " + isTransfer);
							if (isTransfer != null) {
								tokenDTO.setStatus("true");
							} else {
								tokenDTO.setStatus("false");
							}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//
					return "call back";
				}).thenAccept(product -> {
					LOG.info("after calling blackchain then transaction insert into db");
					if (tokenDTO.getStatus().equalsIgnoreCase("true")) {

						if (registerInfo.getReferralId() != null && registerInfo.getReferralId() != 0) {

							boolean referral = referralTokens(tokenDTO);
							if (referral) {
								System.out.println("Referral added");
							} else {
								System.out.println("Referral not added");
							}
						}
						boolean isEmailSent = emailNotificationService.sendEmailforPurchaseRequest(
								tokenDTO.getEmailId(), "Purchase Info from REMCO", registerInfo.getFirstName(),
								tokenDTO.getTokenAmount(), tokenDTO.getTypeOfPurchase());
						purchaseTokenInfo.setCryptoAmount(tokenDTO.getBtcAmount());
						purchaseTokenInfo.setAsynchStatus("success");
						purchaseTokenInfoRepository.save(purchaseTokenInfo);
					} else if (tokenDTO.getStatus().equalsIgnoreCase("false")) {
						purchaseTokenInfo.setTransferType(-1);
						purchaseTokenInfo.setAsynchStatus("failed");
						purchaseTokenInfoRepository.save(purchaseTokenInfo);
						// return true;
					}
				});

				// String isTransfer = bitcoinUtils.btcSendCoins(tokenDTO);
				// System.out.println("Status : " + isTransfer);
				// if (isTransfer != null) {
				//
				// if (registerInfo.getReferralId() != null && registerInfo.getReferralId() !=
				// 0) {
				//
				// boolean referral = referralTokens(tokenDTO);
				// if (referral) {
				// System.out.println("Referral added");
				// } else {
				// System.out.println("Referral not added");
				// }
				// }
				//
				// BitcoinInfo bitcoinInfo =
				// bitcoinInfoRepository.findByEmailId(tokenDTO.getEmailId());

				// PurchaseTokenInfo purchaseTokenInfo1 =
				// (PurchaseTokenInfo) purchaseTokenInfoRepository
				// .findByEmailIdAndTypeOfPurchase(tokenDTO.getEmailId(),
				// tokenDTO.getTypeOfPurchase());
				// if (purchaseTokenInfo1 != null &&
				// purchaseTokenInfo1.getTypeOfPurchase() == 3) {
				// purchaseTokenInfo1.setEtherWalletAddress(etherWalletAddress);
				//
				// purchaseTokenInfo1.setBtcWalletAddress(bitcoinInfo.getBtcWalletAddress());
				// purchaseTokenInfo1.setTypeOfPurchase(3);
				// purchaseTokenInfo1.setBtcAmount(purchaseTokenInfo1.getBtcAmount()+
				// tokenDTO.getBtcAmount());
				// purchaseTokenInfo1
				// .setPurchaseTokens(purchaseTokenInfo1.getPurchaseTokens()
				// + tokenDTO.getTokenAmount());
				// purchaseTokenInfo1.setPurchasedDate(new Date());
				// TokenRateInfo tokenRateInfo1 =
				// tokenRateInfoRopository.findById(1);
				// if (tokenRateInfo1.getDiscount() != null) {
				// Double freeCoins = (tokenDTO.getTokenAmount() *
				// tokenRateInfo1.getDiscount().doubleValue()) / 100;
				// purchaseTokenInfo1.setFreeTokens(purchaseTokenInfo1.getFreeTokens()
				// + freeCoins);
				// }
				//
				// purchaseTokenInfo1.setTransferType(3);
				// purchaseTokenInfoRepository.save(purchaseTokenInfo1);
				// return true;
				// } else {
				// PurchaseTokenInfo purchaseTokenInfo = new PurchaseTokenInfo();
				//
				// purchaseTokenInfo.setEmailId(tokenDTO.getEmailId());
				//
				// purchaseTokenInfo.setBtcWalletAddress(bitcoinInfo.getBtcWalletAddress());
				//
				// purchaseTokenInfo.setEtherWalletAddress(etherWalletAddress);
				// purchaseTokenInfo.setCryptoAmount(tokenDTO.getBtcAmount());
				// purchaseTokenInfo.setPurchaseTokens(tokenDTO.getTokenAmount());
				// purchaseTokenInfo.setTypeOfPurchase(env.getProperty("btc.payment"));
				// purchaseTokenInfo.setPurchasedDate(new Date());
				// // TokenRateInfo tokenRateInfo1 =
				// // tokenRateInfoRopository.findById(1);
				// // if (tokenRateInfo1.getDiscount() != null) {
				// // Double freeCoins = (tokenDTO.getTokenAmount() *
				// // tokenRateInfo1.getDiscount().doubleValue())
				// // / 100;
				// // purchaseTokenInfo.setFreeTokens(freeCoins);
				// // }
				// if (tokenDTO.getIsVesting() == 1) {
				// purchaseTokenInfo.setSchoolName(tokenDTO.getSchoolName());
				// purchaseTokenInfo.setDateOfBirth(tokenDTO.getDateOfBirth());
				// }
				// purchaseTokenInfo.setTransferType(3);
				// purchaseTokenInfo.setBchWalletAddress("");
				// purchaseTokenInfoRepository.save(purchaseTokenInfo);
				//
				// boolean isEmailSent =
				// emailNotificationService.sendEmailforPurchaseRequest(tokenDTO.getEmailId(),
				// "Purchase Info from REMCO", registerInfo.getFirstName(),
				// tokenDTO.getTokenAmount(),
				// tokenDTO.getTypeOfPurchase());
				return true;
				// }
				// }
			}
		}
		return false;

	}

	public boolean purchaseTokenUsingVTN(TokenDTO tokenDTO) throws Exception {

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());

		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		if (registerInfo != null) {

			String decryptedWalletAddress = EncryptDecrypt.decrypt(registerInfo.getWalletAddress());
			String etherWalletAddress = remcoUtils.getWalletAddress(config.getConfigValue(), decryptedWalletAddress);
			LOG.info("Ether Wallet Address : " + etherWalletAddress);

			TokenRateInfo tokenRateInfo = tokenRateInfoRepository.findBySaletype(tokenDTO.getSaleType());

			Double rateInUsd = tokenRateInfo.getTokenValue().doubleValue();

			Double oneNgn = currencyRateUtils.getCurrencyRateForNGN();
			Double purchaseRateForOneTokens = oneNgn * rateInUsd;

			Double value = tokenDTO.getTokenAmount() * purchaseRateForOneTokens;

			String merchantEmailId = env.getProperty("vtn.admin.merchant.email");
			String itemName = "Purchase Tokens";

			tokenDTO.setMerchant_email_id(merchantEmailId);
			tokenDTO.setAmount(value);

			tokenDTO.setItem_name(itemName);
			tokenDTO.setCallback_id(env.getProperty("callback.id"));
			PurchaseTokenInfo purchaseTokenInfo = new PurchaseTokenInfo();

			purchaseTokenInfo.setEmailId(tokenDTO.getEmailId());
			purchaseTokenInfo.setBtcWalletAddress("");
			purchaseTokenInfo.setEtherWalletAddress(etherWalletAddress);
			purchaseTokenInfo.setCryptoAmount(value);
			purchaseTokenInfo.setPurchaseTokens(tokenDTO.getTokenAmount());
			purchaseTokenInfo.setTypeOfPurchase(env.getProperty("vtn.payment"));
			purchaseTokenInfo.setPurchasedDate(new Date());
			if (tokenDTO.getIsVesting() == 1) {
				purchaseTokenInfo.setSchoolName(tokenDTO.getSchoolName());
				purchaseTokenInfo.setDateOfBirth(tokenDTO.getDateOfBirth());
			}
			purchaseTokenInfo.setTransferType(-3);
			purchaseTokenInfo.setBchWalletAddress("");
			purchaseTokenInfoRepository.save(purchaseTokenInfo);

			Integer customerRemarks = purchaseTokenInfo.getId();
			tokenDTO.setCustom_remarks(customerRemarks.toString());
			tokenDTO.setCurrency_id("NGN");
			tokenDTO.setReturn_url(env.getProperty("return.url"));
			tokenDTO.setCancel_url(env.getProperty("cancel.url"));
			tokenDTO.setWalletPassword(null);
			tokenDTO.setIsVesting(null);
			tokenDTO.setDateOfBirth(null);
			tokenDTO.setTypeOfPurchase(null);
			tokenDTO.setSchoolName(null);
			tokenDTO.setTokenAmount(null);
			tokenDTO.setEmailId(null);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	public boolean purchaseTokenCanceledInVTN(TokenDTO tokenDTO) {

		Integer userType = Integer.parseInt(tokenDTO.getPurchaseTokenId());
		if (userType != null) {
			PurchaseTokenInfo purchaseTokenInfo = purchaseTokenInfoRepository.findById(userType);
			purchaseTokenInfo.setTransferType(-2);
			purchaseTokenInfoRepository.save(purchaseTokenInfo);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	public boolean checkPayment(TokenDTO tokenDTO) throws IOException, JSONException, ParseException {
		LOG.info("IPN Ref No : " + tokenDTO.getIpnRefNumber());

		URL url;
		HttpURLConnection connection = null;

		// Create connection
		url = new URL(env.getProperty("check.payment.url"));
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		// connection.setRequestProperty("Content-Length", "" +
		// Integer.toString(urlParameters.getBytes().length));
		// connection.setRequestProperty("Content-Language", "en-US");

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		parameters = new Hashtable<String, String>();

		parameters.put("ipn_refno", tokenDTO.getIpnRefNumber());
		// Send request
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		String urlParameters = buildUrlParameters();
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		StringBuilder sb = new StringBuilder();
		int HttpResult = connection.getResponseCode();

		if (HttpResult == HttpURLConnection.HTTP_OK) {
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
		}

		LOG.info("WR" + sb.toString());
		JSONObject xmlJSONObj = XML.toJSONObject(sb.toString());
		LOG.info("xmlJSONObj : " + xmlJSONObj);

		LOG.info("json : " + xmlJSONObj.get("transaction_details"));
		JSONObject json1 = (JSONObject) xmlJSONObj.get("transaction_details");
		LOG.info("transaction_details : " + json1.get("item"));

		VTNPaymentInfo vtnPaymentInfo = vtnPaymentInfoRepository
				.findVTNPaymentInfoByIpnRefno(tokenDTO.getIpnRefNumber());
		if (vtnPaymentInfo != null) {
			vtnPaymentInfo.setAmount(Double.parseDouble(json1.get("amount").toString()));
			vtnPaymentInfo.setCommission(json1.get("commission").toString());
			vtnPaymentInfo.setConvertedAmount(Double.parseDouble(json1.get("converted_amount").toString()));
			vtnPaymentInfo.setCreatedDate(json1.get("created_date").toString());
			vtnPaymentInfo.setCustomRemarks(json1.get("custom_remarks").toString());
			vtnPaymentInfo.setCustomerEmail(json1.get("customer_email").toString());
			// vtnPaymentInfo.setEmailId(json1.get("custom_remarks").toString());
			vtnPaymentInfo.setItem(json1.get("item").toString());
			vtnPaymentInfo.setMessage(json1.get("message").toString());
			vtnPaymentInfo.setStatus(json1.get("status").toString());
			vtnPaymentInfo.setTransactionNumber(json1.get("transaction_number").toString());
			vtnPaymentInfoRepository.save(vtnPaymentInfo);
			PurchaseTokenInfo purchase = purchaseTokenInfoRepository
					.findById(Integer.parseInt(vtnPaymentInfo.getCustomRemarks()));
			if (purchase != null) {
				tokenDTO.setEmailId(purchase.getEmailId());
				LOG.info("User Email ID------>" + tokenDTO.getEmailId());
			}
			RegisterInfo registerInfo = registerInfoRepository
					.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
			if (vtnPaymentInfo.getStatus().equals("1")) {
				LOG.info("Payment Status Success");
				Integer id = Integer.parseInt(vtnPaymentInfo.getCustomRemarks());
				PurchaseTokenInfo purchaseToken = purchaseTokenInfoRepository.findById(id);
				purchaseToken.setTransferType(4);
				purchaseTokenInfoRepository.save(purchaseToken);
				boolean isEmailSent = emailNotificationService.sendEmailforPurchaseRequest(tokenDTO.getEmailId(),
						"Purchase Info from REMCO", registerInfo.getFirstName(), purchaseToken.getPurchaseTokens(),
						purchaseToken.getTypeOfPurchase());

				if (registerInfo.getReferralId() != null && registerInfo.getReferralId() != 0) {

					tokenDTO.setTokenAmount(purchaseToken.getPurchaseTokens());
					boolean referral = referralTokens(tokenDTO);
					if (referral) {
						System.out.println("Referral added");
					} else {
						System.out.println("Referral not added");
					}
				}
			} else {
				LOG.info("Payment Status Failed");
				Integer id = Integer.parseInt(vtnPaymentInfo.getCustomRemarks());
				PurchaseTokenInfo purchaseTokenInfo = purchaseTokenInfoRepository.findById(id);
				purchaseTokenInfo.setTransferType(-1);
				purchaseTokenInfoRepository.save(purchaseTokenInfo);

				boolean isEmail = emailNotificationService.sendEmailforPurchaseRequestFailure(tokenDTO.getEmailId(),
						"Purchase Info from REMCO", registerInfo.getFirstName(), purchaseTokenInfo.getPurchaseTokens(),
						purchaseTokenInfo.getTypeOfPurchase());

			}
			return true;
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	public String buildUrlParameters() {
		String urlParameters = "";

		Enumeration enumeration = parameters.keys();

		String key = null;
		String value = null;
		String pair = null;

		boolean isFirstData = true;

		while (enumeration.hasMoreElements()) {
			key = (String) enumeration.nextElement();
			value = parameters.get(key);

			pair = key + "=" + value;

			if (isFirstData) // isFirstData = false;
			{
				urlParameters += pair;
			} else {
				urlParameters += "&" + pair;
			}

			isFirstData = false;
		}

		return urlParameters;
	}

	@SuppressWarnings("unused")
	public String transferTokensForPurchase()
			throws IOException, CipherException, InterruptedException, ExecutionException {

		try {

			SaleDatesInfo salesDatesInfo = saleDatesInfoRepository.findById(1);
			Date curDate = new Date();
			Date icoEndDate = salesDatesInfo.getPublicEndDate();
			if (curDate.after(icoEndDate)) {

				List<PurchaseTokenInfo> purchaseTokenInfo1 = purchaseTokenInfoRepository
						.findByTransferTypeOrTransferTypeOrTransferTypeOrTransferType(1, 2, 3, 4);

				for (PurchaseTokenInfo purchaseTokenInfo : purchaseTokenInfo1) {

					LOG.info("Ether Wallet Password : " + purchaseTokenInfo.getEtherWalletAddress());

					if (purchaseTokenInfo.getTransferType() != 0) {

						Credentials credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
								env.getProperty("credentials.address"));

						RemcoToken assetToken = RemcoToken.load(env.getProperty("token.address"), web3j, credentials,
								Contract.GAS_PRICE, Contract.GAS_LIMIT);

						RegisterInfo userInfo = registerInfoRepository
								.findRegisterInfoByEmailIdIgnoreCase(purchaseTokenInfo.getEmailId());

						Double purchaseAmount = purchaseTokenInfo.getPurchaseTokens();

						Double amt = purchaseAmount * 100000000;

						BigInteger transVal = new BigInteger(truncateDecimal(amt, 0).toPlainString());

						transactionReceipt = assetToken.transfer(purchaseTokenInfo.getEtherWalletAddress(), transVal)
								.sendAsync().get();

						System.out
								.println("Get transaction List Recipient----------->" + transactionReceipt.getStatus());

						if (transactionReceipt.getStatus().equals("0x1")) {

							TransactionHistory transactionHistory = new TransactionHistory();

							LOG.info("calling blackchain for manual token transfer");
							transactionHistory.setFromAddress(env.getProperty("main.address"));
							transactionHistory.setToAddress(purchaseTokenInfo.getEtherWalletAddress());
							transactionHistory.setPaymentMode(purchaseTokenInfo.getTypeOfPurchase());
							transactionHistory.setToken(purchaseAmount);
							transactionHistory.setTransferDate(new Date());
							transactionHistory.setAmount(truncateDecimal(purchaseTokenInfo.getCryptoAmount(), 8));
							transactionHistory.setStatus(env.getProperty("status.success"));
							transactionInfoRepo.save(transactionHistory);

							PurchaseTokenInfo purchaseTokenInfos = purchaseTokenInfoRepository
									.findOne(purchaseTokenInfo.getId());
							if (purchaseTokenInfos != null) {
								purchaseTokenInfos.setTransferType(0);
								purchaseTokenInfoRepository.save(purchaseTokenInfos);

								boolean isEmailSent = emailNotificationService.sendEmailforPurchaseTransfer(
										purchaseTokenInfo.getEmailId(), "Purchase Info from REMCO",
										userInfo.getFirstName(), purchaseTokenInfo.getPurchaseTokens(),
										purchaseTokenInfo.getTypeOfPurchase());

							}
						}

					}

				}

				List<ReferralInfo> refList = referralInfoRepository.findByStatus(0);
				for (ReferralInfo refInfo : refList) {

				}

			}
		} catch (Exception e) {
			System.out.println("Exception is------------->" + e.toString());
			return null;
		}

		return null;
	}

	public List<TokenDTO> purchaseLists(TokenDTO tokenDTO) throws Exception {
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		List<TokenDTO> list = new ArrayList<TokenDTO>();

		RegisterInfo registerInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
		String WalletAddress = EncryptDecrypt.decrypt(registerInfo.getWalletAddress());

		String etherWalletAddress = remcoUtils.getWalletAddress(config.getConfigValue(), WalletAddress);

		String startPattern = "MM-dd-yyyy 00:00:00";
		String endPattern = "MM-dd-yyyy 23:59:59";

		SimpleDateFormat startDateFormat = new SimpleDateFormat(startPattern);
		SimpleDateFormat endDateFormat = new SimpleDateFormat(endPattern);
		String d1 = startDateFormat.format(tokenDTO.getStartDate());
		String d2 = endDateFormat.format(tokenDTO.getEndDate());
		System.out.println("Start Dat e str------>" + d1);
		System.out.println("Start Dat e str------>" + d2);
		//
		SimpleDateFormat startDateFormat1 = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

		Date startDate = startDateFormat1.parse(d1);
		Date endDate = startDateFormat1.parse(d2);

		if (registerInfo.getUserType() == 0) {
			Sort sort = new Sort(new Sort.Order(Direction.ASC, "purchasedDate"));
			Pageable pageable = new PageRequest(tokenDTO.getPageNum(), tokenDTO.getPageSize(), sort);
			/*
			 * Page<PurchaseTokenInfo> purchaseTokenInfo1 = purchaseTokenInfoRepository
			 * .findPurchaseTokenInfoByEtherWalletAddress(etherWalletAddress, pageable);
			 */

			Page<PurchaseTokenInfo> purchaseTokenInfo1 = null;
			if (tokenDTO.getTransactionType() == 5) {
				purchaseTokenInfo1 = purchaseTokenInfoRepository.getPurchaseList(startDate, endDate, etherWalletAddress,
						pageable);
			} else {
				purchaseTokenInfo1 = purchaseTokenInfoRepository.getPurchaseList(tokenDTO.getTransactionType(),
						startDate, endDate, etherWalletAddress, pageable);
			}
			tokenDTO.setTotalPages(purchaseTokenInfo1.getTotalPages());
			tokenDTO.setTotalElements(purchaseTokenInfo1.getTotalElements());
			for (PurchaseTokenInfo purchaseList : purchaseTokenInfo1) {

				String bchWallet = "";
				String btcWallet = "";

				if (purchaseList.getBchWalletAddress() != null && purchaseList.getBchWalletAddress() != "") {
					bchWallet = EncryptDecrypt.decrypt(purchaseList.getBchWalletAddress());

				}

				if (purchaseList.getBtcWalletAddress() != null && purchaseList.getBtcWalletAddress() != "") {
					btcWallet = EncryptDecrypt.decrypt(purchaseList.getBtcWalletAddress());

				}
				TokenDTO tokenDTOs = new TokenDTO();
				tokenDTOs.setIsVesting(purchaseList.getIsVesting());
				tokenDTOs.setSchoolName(purchaseList.getSchoolName());
				tokenDTOs.setDateOfBirth(purchaseList.getDateOfBirth());
				tokenDTOs.setTypeOfPurchase(purchaseList.getTypeOfPurchase());
				tokenDTOs.setTransactionType(purchaseList.getTransferType());
				tokenDTOs.setTokenAmount(purchaseList.getPurchaseTokens());
				tokenDTOs.setEmailId(purchaseList.getEmailId());
				tokenDTOs.setCryptoAmount(purchaseList.getCryptoAmount());
				tokenDTOs.setBchWalletAddress(bchWallet);
				tokenDTOs.setEtherWalletAddress(purchaseList.getEtherWalletAddress());
				tokenDTOs.setBtcWalletAddress(btcWallet);
				tokenDTOs.setPurchasedDate(purchaseList.getPurchasedDate());
				list.add(tokenDTOs);
			}
			return list;
		} else {

			Sort sort = new Sort(new Sort.Order(Direction.ASC, "purchasedDate"));
			Pageable pageable = new PageRequest(tokenDTO.getPageNum(), tokenDTO.getPageSize(), sort);
			/*
			 * Page<PurchaseTokenInfo> purchaseTokenInfo1 = (Page<PurchaseTokenInfo>)
			 * purchaseTokenInfoRepository .findAll(pageable);
			 */
			Page<PurchaseTokenInfo> purchaseTokenInfo1 = null;

			if (tokenDTO.getTransactionType() == 5 && tokenDTO.getKeyword() != null) {
				purchaseTokenInfo1 = purchaseTokenInfoRepository.getPurchaseListAdmin(startDate, endDate,
						tokenDTO.getKeyword(), pageable);
			} else if (tokenDTO.getTransactionType() != 5 && tokenDTO.getKeyword() != null) {
				purchaseTokenInfo1 = purchaseTokenInfoRepository.getPurchaseListAdmin(tokenDTO.getTransactionType(),
						startDate, endDate, tokenDTO.getKeyword(), pageable);
			} else if (tokenDTO.getTransactionType() == 5 && tokenDTO.getKeyword() == null) {
				purchaseTokenInfo1 = purchaseTokenInfoRepository.getPurchaseListAdmin(startDate, endDate, pageable);
			} else if (tokenDTO.getTransactionType() != 5 && tokenDTO.getKeyword() == null) {
				purchaseTokenInfo1 = purchaseTokenInfoRepository.getPurchaseListAdmin(tokenDTO.getTransactionType(),
						startDate, endDate, pageable);
			}

			tokenDTO.setTotalPages(purchaseTokenInfo1.getTotalPages());
			tokenDTO.setTotalElements(purchaseTokenInfo1.getTotalElements());
			for (PurchaseTokenInfo purchaseList : purchaseTokenInfo1) {

				String bchWallet = "";
				String btcWallet = "";

				if (purchaseList.getBchWalletAddress() != null && purchaseList.getBchWalletAddress() != "") {
					bchWallet = EncryptDecrypt.decrypt(purchaseList.getBchWalletAddress());

				}

				if (purchaseList.getBtcWalletAddress() != null && purchaseList.getBtcWalletAddress() != "") {
					btcWallet = EncryptDecrypt.decrypt(purchaseList.getBtcWalletAddress());

				}

				TokenDTO tokenDTOs = new TokenDTO();
				tokenDTOs.setIsVesting(purchaseList.getIsVesting());
				tokenDTOs.setSchoolName(purchaseList.getSchoolName());
				tokenDTOs.setDateOfBirth(purchaseList.getDateOfBirth());
				tokenDTOs.setTypeOfPurchase(purchaseList.getTypeOfPurchase());
				tokenDTOs.setTransactionType(purchaseList.getTransferType());
				tokenDTOs.setTokenAmount(purchaseList.getPurchaseTokens());
				tokenDTOs.setEmailId(purchaseList.getEmailId());
				tokenDTOs.setCryptoAmount(purchaseList.getCryptoAmount());
				tokenDTOs.setBchWalletAddress(bchWallet);
				tokenDTOs.setEtherWalletAddress(purchaseList.getEtherWalletAddress());
				tokenDTOs.setBtcWalletAddress(btcWallet);
				tokenDTOs.setPurchasedDate(purchaseList.getPurchasedDate());
				list.add(tokenDTOs);
			}
			return list;
		}
	}

	@SuppressWarnings("unused")
	public boolean synchronizedManualTokenTransfer(TokenTransferDTO tokenListDTO, TokenDTO tokenDTO) throws Exception {

		AirDropUserBonus airDropUserBonus = airDropUserBonusRepository.findOne(tokenListDTO.getAirDropId());
		if (airDropUserBonus.getTokenTransferStatus() == 0) {
			if (airDropUserBonus.getAllocationType().equalsIgnoreCase("AIRDROP")) {
				BigDecimal value = BigDecimal.ZERO;
				if (airDropUserBonus.getEarnedToken() == value) {
					return false;
				}
			}
			transactionReceipt = new TransactionReceipt();

			TransactionHistory transactionHistory = new TransactionHistory();
			Config config = configInfoRepository.findConfigByConfigKey("walletfile");
			DecimalFormat df = new DecimalFormat("0.00000000");
			RegisterInfo registerInfo = registerInfoRepository
					.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());
			Credentials credentials;
			try {
				LOG.info("calling blackchain for manual token transfer--->" + tokenDTO.getEmailId());
				transactionHistory.setFromAddress(registerInfo.getWalletFile());
				transactionHistory.setToAddress(tokenListDTO.getToAddress());
				transactionHistory.setAmount(new BigDecimal(0));
				transactionHistory.setToken(tokenListDTO.getRequestToken());
				transactionHistory.setPaymentMode(env.getProperty("admin.payment"));
				transactionHistory.setTransferDate(new Date());
				transactionHistory.setEmailId(tokenDTO.getEmailId());
				transactionHistory.setStatus(env.getProperty("status.pending"));
				transactionInfoRepo.save(transactionHistory);
				if (transactionHistory != null) {
					credentials = WalletUtils.loadCredentials(EncryptDecrypt.decrypt(registerInfo.getWalletPassword()),
							config.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getWalletAddress()));
					RemcoToken assetToken = RemcoToken.load(env.getProperty("token.address"), web3j, credentials,
							Contract.GAS_PRICE, Contract.GAS_LIMIT);
					BigDecimal amount = BigDecimal.valueOf(tokenListDTO.getRequestToken());

					amount = amount.multiply(new BigDecimal("100000000"));
					BigInteger transVal = amount.toBigInteger();

					// BigInteger transVal =
					// BigDecimal.valueOf(doublevalue).toBigInteger();
					transactionReceipt = assetToken.transfer(tokenListDTO.getToAddress(), transVal).sendAsync().get();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			finally {
				//
				LOG.info("after calling blackchain then transaction insert into db");
				if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
					if (transactionHistory != null) {
						transactionHistory.setStatus(env.getProperty("status.success"));
						transactionInfoRepo.save(transactionHistory);

						if (airDropUserBonus != null) {
							airDropUserBonus.setTokenTransferStatus(1);
							airDropUserBonus.setTransferredDate(new Date());
							airDropUserBonusRepository.save(airDropUserBonus);
						}
						return true;
					}
				} else if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
					if (transactionHistory != null) {
						transactionHistory.setStatus(env.getProperty("status.failed"));
						transactionInfoRepo.save(transactionHistory);

						if (airDropUserBonus != null) {
							airDropUserBonus.setTokenTransferStatus(-1);
							airDropUserBonusRepository.save(airDropUserBonus);
						}
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	public boolean promoTokenTransfer(UserRegisterDTO userRegisterDTO) {
		transactionReceipt = new TransactionReceipt();

		TransactionHistory transactionHistory = new TransactionHistory();
		Config config = configInfoRepository.findConfigByConfigKey("walletfile");
		Config promotoken = null;
		if (userRegisterDTO.getReferSite() != null && userRegisterDTO.getReferSite().equalsIgnoreCase("VTN")) {
			promotoken = configInfoRepository.findConfigByConfigKey("vtnpromotoken");

		} else if (userRegisterDTO.getReferSite() != null && userRegisterDTO.getReferSite().equalsIgnoreCase("QUIZ")) {
			promotoken = configInfoRepository.findConfigByConfigKey("promotoken");

		}
		userRegisterDTO.setNoOfTokens(Double.parseDouble(promotoken.getConfigValue()));
		DecimalFormat df = new DecimalFormat("0.00000000");
		RegisterInfo registerInfo = registerInfoRepository
				.findRegisterInfoByEmailIdIgnoreCase(env.getProperty("promo.email"));
		Credentials credentials;
		try {
			LOG.info("calling blackchain for manual token transfer--->" + registerInfo.getEmailId());
			transactionHistory.setFromAddress(registerInfo.getWalletFile());
			transactionHistory.setToAddress(userRegisterDTO.getWalletAddress());
			transactionHistory.setAmount(new BigDecimal(0));
			transactionHistory.setToken(Double.parseDouble(promotoken.getConfigValue()));
			transactionHistory.setPaymentMode(env.getProperty("admin.payment"));
			transactionHistory.setTransferDate(new Date());
			transactionHistory.setEmailId(registerInfo.getEmailId());
			transactionHistory.setStatus(env.getProperty("status.pending"));
			transactionInfoRepo.save(transactionHistory);
			if (transactionHistory != null) {
				credentials = WalletUtils.loadCredentials(EncryptDecrypt.decrypt(registerInfo.getWalletPassword()),
						config.getConfigValue() + EncryptDecrypt.decrypt(registerInfo.getWalletAddress()));
				RemcoToken assetToken = RemcoToken.load(env.getProperty("token.address"), web3j, credentials,
						Contract.GAS_PRICE, Contract.GAS_LIMIT);
				BigDecimal amount = new BigDecimal(promotoken.getConfigValue());

				amount = amount.multiply(new BigDecimal("100000000"));
				BigInteger transVal = amount.toBigInteger();

				// BigInteger transVal =
				// BigDecimal.valueOf(doublevalue).toBigInteger();
				transactionReceipt = assetToken.transfer(userRegisterDTO.getWalletAddress(), transVal).sendAsync()
						.get();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		finally {
			//
			LOG.info("after calling blackchain then transaction insert into db");
			// if
			// (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success")))
			// {

			if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.success"))) {
				if (transactionHistory != null) {
					transactionHistory.setStatus(env.getProperty("status.success"));
					transactionInfoRepo.save(transactionHistory);

					PromoUsersInfo promo = promoUsersInfoRepository.findOne(userRegisterDTO.getPromoId());
					promo.setTokenStatus(1);
					promoUsersInfoRepository.save(promo);
					return true;
					// return true;
				}
			} else if (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed"))) {
				// else if
				// (transactionReceipt.getStatus().equals(env.getProperty("transactionReceipt.failed")))
				// {
				if (transactionHistory != null) {
					transactionHistory.setStatus(env.getProperty("status.failed"));
					transactionInfoRepo.save(transactionHistory);
					return false;
					// return true;
				}
			}
		}
		return false;
	}

}
