package com.remco.ico.serviceImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.utils.Convert;

import com.remco.ico.DTO.SaleTypeDTO;
import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.TokenTransferDTO;
import com.remco.ico.DTO.UserRegisterDTO;
import com.remco.ico.model.Config;
import com.remco.ico.model.ICOTokenInfo;
import com.remco.ico.model.PurchaseTokenInfo;
import com.remco.ico.model.RegisterInfo;
import com.remco.ico.model.ReservedTokenInfo;
import com.remco.ico.model.SaleDatesInfo;
import com.remco.ico.model.TokenDiscountInfo;
import com.remco.ico.model.TokenRateInfo;
import com.remco.ico.model.TransactionHistory;
import com.remco.ico.repository.AirDropUserBonusRepository;
import com.remco.ico.repository.ConfigInfoRepository;
import com.remco.ico.repository.ICOTokenInfoRepository;
import com.remco.ico.repository.PurchaseTokenInfoRepository;
import com.remco.ico.repository.RegisterInfoRepository;
import com.remco.ico.repository.ReservedTokenInfoRepository;
import com.remco.ico.repository.SaleDatesInfoRepository;
import com.remco.ico.repository.TokenDiscountInfoRepository;
import com.remco.ico.repository.TokenRateInfoRepository;
import com.remco.ico.repository.TransactionInfoRepository;
import com.remco.ico.service.EmailNotificationService;
import com.remco.ico.service.TokenUserService;
import com.remco.ico.solidityHandler.SolidityHandler;
import com.remco.ico.solidityToJava.RemcoToken;
import com.remco.ico.utils.EncryptDecrypt;
import com.remco.ico.utils.RemcoUtils;

@Service
public class TokenUserServiceImpl implements TokenUserService {

	@Autowired
	private RegisterInfoRepository registerInfoRepository;

	@Autowired
	private SolidityHandler solidityHandler;
	@Autowired
	private AirDropUserBonusRepository airDropUserBonusRepository;
	@Autowired
	private RemcoUtils remcoUtils;
	@Autowired
	private SaleDatesInfoRepository saleDatesInfoRepo;
	@Autowired
	private Environment env;
	@Autowired
	private TransactionInfoRepository transactionInfoRepo;
	@Autowired
	private EmailNotificationService emailNotificationService;
	@Autowired
	private TokenRateInfoRepository tokenRateInfoRepository;
	@Autowired
	private ConfigInfoRepository configInfoRepository;
	@Autowired
	private ICOTokenInfoRepository icoTokenInfoRepository;
	@Autowired
	private PurchaseTokenInfoRepository purchaseTokenInfoRepository;
	@Autowired
	private SaleDatesInfoRepository saleDatesInfoRepository;
	@Autowired
	private ReservedTokenInfoRepository reservedTokenInfoRepository;
	@Autowired
	private TokenDiscountInfoRepository tokenDiscountInfoRepository;

	// private final Web3j web3j = Web3j.build(new
	// HttpService("https://rinkeby.infura.io/"));

	private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io"));

	private static final Logger LOG = LoggerFactory.getLogger(TokenUserServiceImpl.class);

	BigInteger GAS = BigInteger.valueOf(4300000L);
	BigInteger GAS_PRICE = BigInteger.valueOf(2000000L);
	private BigInteger GAS_LIMIT = BigInteger.valueOf(3000000L);

	private static BigDecimal truncateDecimal(double x, int numberofDecimals) {
		if (x > 0) {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
		} else {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
		}
	}

	@Override
	public boolean checkWalletAddressEquals(TokenDTO tokenDTO) throws Exception {

		RegisterInfo regInfo = registerInfoRepository.findRegisterInfoByEmailIdIgnoreCase(tokenDTO.getEmailId());

		if (regInfo != null) {
			if (!regInfo.getWalletFile().equalsIgnoreCase(tokenDTO.getToAddress())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean TransferCoin(TokenDTO tokenDTO) throws Exception {
		boolean status = solidityHandler.manualTokenTransfer(tokenDTO);

		if (status) {

			return true;
		}

		return false;
	}

	@Override
	public List<TokenDTO> transactionHistory(TokenDTO tokenDTO, String etherWalletAddress) throws Exception {
		List<TokenDTO> transactionLists = solidityHandler.transactionHistory(tokenDTO, etherWalletAddress);

		return transactionLists;
	}

	@Override
	public Double balanceTokens(TokenDTO tokenDTO) throws Exception {
		Double balance = solidityHandler.balanceTokens(tokenDTO);
		if (balance != null) {
			return balance;
		}
		return null;
	}

	@Override
	public BigDecimal etherBalance(TokenDTO tokenDTO) throws Exception {
		BigDecimal balance = solidityHandler.etherBalance(tokenDTO);
		if (balance != null) {
			return balance;
		}
		return null;
	}

	@Override
	public String burnTokens(TokenDTO tokenDTO) {
		String burn = solidityHandler.burnTokens(tokenDTO);
		if (burn != null) {
			return burn;
		}
		return null;
	}

	@Override
	public boolean checkMainbalance(TokenDTO tokenDTO) throws Exception {
		// LOG.info("centraladmin" + tokenDTO.getCentralAdmin());
		EthGetBalance ethGetBalance;
		try {
			DecimalFormat df = new DecimalFormat("#.########");
			ethGetBalance = web3j.ethGetBalance(tokenDTO.getCentralAdmin(), DefaultBlockParameterName.LATEST).send();

			BigDecimal wei = new BigDecimal(ethGetBalance.getBalance());
			// LOG.info("wei " + wei);
			// BigInteger wei = ethGetBalance.getBalance();
			BigDecimal amountCheck = Convert.fromWei(wei, Convert.Unit.ETHER);

			tokenDTO.setMainBalance(Double.parseDouble(df.format(amountCheck.doubleValue())));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public TokenDTO getSaleDates() {

		TokenDTO tokenDTO = new TokenDTO();
		SaleDatesInfo saleDates = saleDatesInfoRepo.findOne(1);
		if (saleDates != null) {

			System.out.println("Sales Dates is not null");
			tokenDTO.setPrivateStartDate(saleDates.getPrivateStartDate());
			tokenDTO.setPrivateEndDate(saleDates.getPrivateEndDate());
			tokenDTO.setPrePublicStartDate(saleDates.getPrePublicStartDate());
			tokenDTO.setPrePublicEndDate(saleDates.getPrePublicEndDate());
			tokenDTO.setPublicStartDate(saleDates.getPublicStartDate());
			tokenDTO.setPublicEndDate(saleDates.getPublicEndDate());
			tokenDTO.setIsPrivateAccredit(saleDates.getPrivateAccredit());
			tokenDTO.setIsPresaleAccredit(saleDates.getPresaleAccredit());
			tokenDTO.setIsPublicAccredit(saleDates.getPublicAccredit());

			return tokenDTO;
		}
		return null;
	}

	@Override
	public boolean updateSaleDates(TokenDTO tokenDTO) {

		boolean status = false;
		SaleDatesInfo tokenRate = saleDatesInfoRepo.findOne(1);
		if (tokenRate != null) {
			tokenRate.setPrivateStartDate(tokenDTO.getPrivateStartDate());
			tokenRate.setPrivateEndDate(tokenDTO.getPrivateEndDate());
			tokenRate.setPrePublicStartDate(tokenDTO.getPrePublicStartDate());
			tokenRate.setPrePublicEndDate(tokenDTO.getPrePublicEndDate());
			tokenRate.setPublicStartDate(tokenDTO.getPublicStartDate());
			tokenRate.setPublicEndDate(tokenDTO.getPublicEndDate());
			tokenRate.setPrivateAccredit(tokenDTO.getIsPrivateAccredit());
			tokenRate.setPresaleAccredit(tokenDTO.getIsPresaleAccredit());
			tokenRate.setPublicAccredit(tokenDTO.getIsPublicAccredit());
			saleDatesInfoRepo.save(tokenRate);
			status = true;
			return status;
		}
		return status;
	}

	@Override
	public List<SaleTypeDTO> getTokenValues(TokenDTO tokenDTO) {

		TokenDiscountInfo tokenRateInfo = tokenDiscountInfoRepository.findOne(1);
		// TokenRateInfo tokenRateInfos = new TokenRateInfo();
		if (tokenRateInfo == null) {
			tokenRateInfo = new TokenDiscountInfo();
			tokenRateInfo.setTokenName(env.getProperty("tokenName"));
			tokenRateInfo.setReferralDisc(new BigDecimal("0"));
			tokenRateInfo.setVestingDisc(new BigDecimal("0"));
			tokenRateInfo.setNgnValue(new BigDecimal("0"));
			tokenRateInfo = tokenDiscountInfoRepository.save(tokenRateInfo);
		}
		List<SaleTypeDTO> tokenDTo = remcoUtils.tokenValues(tokenRateInfo, tokenDTO);
		return tokenDTo;
	}

	@Override
	public boolean updateOffers(TokenDTO tokenDTO) {

		boolean status = false;

		if (tokenDTO.getSaleType().equals("REMCO")) {
			TokenDiscountInfo tokenRate = tokenDiscountInfoRepository.findOne(1);
			if (tokenRate != null) {
				tokenRate.setReferralDisc(new BigDecimal(tokenDTO.getReferralBonus()));
				tokenRate.setVestingDisc(new BigDecimal(tokenDTO.getVestingDisc()));
				tokenRate.setNgnValue(new BigDecimal(tokenDTO.getNgnValue()));
				tokenRate.setCreatedDate(new Date());
				tokenRate = tokenDiscountInfoRepository.save(tokenRate);
				status = true;
				return status;
			}
		} else {
			TokenRateInfo token = tokenRateInfoRepository.findBySaletype(tokenDTO.getSaleType());

			if (token != null) {
				token.setTokenValue(tokenDTO.getTokenValue());
				token.setDiscount(new BigDecimal(tokenDTO.getDiscount()));
				token.setMinContribute(new BigDecimal(tokenDTO.getMinContribute()));
				tokenRateInfoRepository.save(token);

				status = true;
				return status;
			}
		}
		return status;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean saveTokenDetails(TokenDTO tokenDTO) throws Exception {

		String adminMail = null;
		TransactionHistory transactionHistory = new TransactionHistory();

		transactionHistory.setFromAddress(tokenDTO.getFromAddress());
		transactionHistory.setToAddress(tokenDTO.getToAddress());
		transactionHistory.setAmount(new BigDecimal(0));
		transactionHistory.setToken(tokenDTO.getRequestToken());
		transactionHistory.setPaymentMode(env.getProperty("admin.payment"));
		transactionHistory.setTransferDate(new Date());
		transactionHistory.setStatus(env.getProperty("status.pending"));
		transactionHistory.setTokenStatus(env.getProperty("status.pending"));
		transactionInfoRepo.save(transactionHistory);

		if (transactionHistory != null) {
			transactionInfoRepo.save(transactionHistory);

			tokenDTO.setId(transactionHistory.getId());
			System.out.println("Token transfer Id ----------->" + transactionHistory.getId());

			String encryptedId = EncryptDecrypt.encrypt(transactionHistory.getId().toString());

			URI uri = new URI("http://remittancetoken.io/ApproveTransfer.aspx?TransactionId=" + encryptedId);
			System.out.println("Converted URI----->" + uri);
			URI uri2 = new URI(encryptedId);
			System.out.println("Converetd ID----->" + uri2);

			String content = "<b>Dear Admin,<br><br>"
					+ "<b>You have received a request for Token Transfer Approval. Please review and Approve !!!<br><br></b>"
					+ "toAddress=" + StringUtils.trim(tokenDTO.getToAddress()) + "<br>" + "fromAddress= "
					+ StringUtils.trim(transactionHistory.getFromAddress()) + "<br>" + "requestToken= "
					+ StringUtils.trim(tokenDTO.getRequestToken().toString()) + "<br><br>"
					+ "<a href=https://remittancetoken.io/TokenAdmin/ApproveTransfer.aspx?TransactionId=" + encryptedId
					+ "&tokenFlag=" + tokenDTO.getTokenFlag() + "> Click here to Approve Token Transfer. </a>" + "<br>"
					+ "<br>"
					+ "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>"
					+ "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>" + "<br><br>"
					+ "<b>Thank you,</b><br>" + "<b>Remco team.</b>";

			if (tokenDTO.getEmailId().equalsIgnoreCase(env.getProperty("admin1.email"))) {

				System.out.println("To email isAdmin2");
				adminMail = env.getProperty("admin2.email");
			} else if (tokenDTO.getEmailId().equalsIgnoreCase(env.getProperty("admin2.email"))) {
				System.out.println("To email isAdmin1");
				adminMail = env.getProperty("admin1.email");
			}
			System.out.println("emailidforsendmail" + "  " + adminMail + content);

			boolean isEmailSent = emailNotificationService.sendEmailTokenTransfer(adminMail,
					"Token Transfer Info from REMCO", content);
			if (!isEmailSent) {
				boolean isEmail = emailNotificationService.sendEmailforMailError(env.getProperty("admin1.email"),
						"Email Alert Info from REMCO", "");
			}
			return true;
		}
		return false;
	}

	public boolean isTokenTransApproved(TokenDTO tokenDTO) throws Exception {

		String id = tokenDTO.getTokenTransId().toString().replaceAll("\\s", "+");
		id = EncryptDecrypt.decrypt(id);

		LOG.info("Value--2--->" + id);
		TransactionHistory txHistory = transactionInfoRepo.findOne(Integer.parseInt(id));

		if (txHistory != null) {
			tokenDTO.setRequestToken(txHistory.getToken());
			tokenDTO.setToAddress(txHistory.getToAddress());
			if (txHistory.getTokenStatus().equals("0")) {
				tokenDTO.setTokenTransId(id);
				return true;
			}
		}
		return false;

	}

	@SuppressWarnings("unused")
	@Override
	public boolean adminTokenBalance(TokenDTO tokenDTO) throws Exception {

		Credentials credentials = WalletUtils.loadCredentials(this.env.getProperty("credentials.password"),
				this.env.getProperty("credentials.address"));

		RemcoToken assetToken = RemcoToken.load(env.getProperty("token.address"), web3j, credentials, GAS_PRICE,
				GAS_LIMIT);

		DecimalFormat df = new DecimalFormat("0.00000000");
		BigInteger balanceOf = assetToken.balanceOf(env.getProperty("main.address")).send();
		Double balanceOfTokenUser = balanceOf.doubleValue() / 100000000.0;

		// total supply
		BigInteger initial = assetToken._totalSupply().send();
		Double initialSupply = initial.doubleValue() / 100000000.0;

		// burn
		BigInteger burn = assetToken.deleteToken().send();
		Double burnTokens = burn.doubleValue() / 100000000.0;
		// tokenDTO.setTotalToken(initialSupply);
		tokenDTO.setTotalTokenString(truncateDecimal(initialSupply, 8).toPlainString());
		ICOTokenInfo icoToken = icoTokenInfoRepository.findIcoTokenInfoByIcoKey("ICOToken");
		ReservedTokenInfo reservedToken = reservedTokenInfoRepository.findOne(1);

		if (icoToken != null) {
			System.out.println("ICO Token Value----->" + icoToken.getIcoValue());
			Double icoTokenValue = icoToken.getIcoValue();
			BigDecimal bdecimal = truncateDecimal(icoTokenValue, 8);
			icoTokenValue = bdecimal.doubleValue();
			// tokenDTO.setTotalIcoToken(icoTokenValue);
			tokenDTO.setTotalIcoTokenString(truncateDecimal(icoTokenValue, 8).toPlainString());
			System.out.println("ICO Token value----->" + tokenDTO.getTotalIcoTokenString());
		}
		List<PurchaseTokenInfo> txSoldHistory = purchaseTokenInfoRepository
				.findByTransferTypeOrTransferTypeOrTransferTypeOrTransferType(0, 0, 0, 0);

		Double soldCount = 0.0;

		if (txSoldHistory != null) {
			for (int i = 0; i < txSoldHistory.size(); i++) {
				soldCount = soldCount + txSoldHistory.get(i).getPurchaseTokens();
			}
		}
		BigDecimal bd = truncateDecimal(soldCount, 8);
		soldCount = bd.doubleValue();

		// tokenDTO.setIcoSoldToken(soldCount);
		tokenDTO.setIcoSoldTokenString(truncateDecimal(soldCount, 8).toPlainString());
		Double blcToken = icoToken.getIcoAvail();
		BigDecimal bd4 = truncateDecimal(blcToken, 8);
		blcToken = bd4.doubleValue();
		// tokenDTO.setIcoAvailableCount(blcToken);
		tokenDTO.setIcoAvailableCountString(truncateDecimal(blcToken, 8).toPlainString());

		List<PurchaseTokenInfo> txHistory = purchaseTokenInfoRepository
				.findByTransferTypeOrTransferTypeOrTransferTypeOrTransferType(1, 2, 3, 4);

		Double reqCount = 0.0;

		if (txHistory != null) {
			for (int i = 0; i < txHistory.size(); i++) {
				reqCount = reqCount + txHistory.get(i).getPurchaseTokens();

			}
		}
		// tokenDTO.setIcoRequestToken(reqCount);
		tokenDTO.setIcoRequestTokenString(truncateDecimal(reqCount, 8).toPlainString());
		// tokenDTO.setBurntCount(burnTokens);
		tokenDTO.setBurntCountString(truncateDecimal(burnTokens, 8).toPlainString());

		Double totalTokBlc = balanceOfTokenUser - (icoToken.getIcoAvail() + reservedToken.getReservedAvail());
		Double totalNonIcoToken = initialSupply - (icoToken.getIcoValue() + reservedToken.getReservedValue());

		System.out.println("Total Blc Token 1------>" + totalTokBlc);
		System.out.println("Total Blc Token 2------>" + balanceOfTokenUser);
		System.out.println("Total Blc Token 3------>" + icoToken.getIcoAvail());

		BigDecimal bd3 = truncateDecimal(totalTokBlc, 8);
		totalTokBlc = bd3.doubleValue();
		// tokenDTO.setBlcNonIcoTokenCount(totalTokBlc);
		tokenDTO.setBlcNonIcoTokenCountString(truncateDecimal(totalTokBlc, 8).toPlainString());

		BigDecimal bd7 = truncateDecimal(totalNonIcoToken, 8);
		totalTokBlc = bd7.doubleValue();
		// tokenDTO.setNonIcoTokenCount(totalNonIcoToken);
		tokenDTO.setNonIcoTokenCountString(truncateDecimal(totalNonIcoToken, 8).toPlainString());

		Double totalReserved = reservedToken.getReservedValue();
		Double spentReserved = reservedToken.getReservedSold();
		Double blcReserved = reservedToken.getReservedAvail();

		BigDecimal bd5 = truncateDecimal(totalReserved, 8);
		totalReserved = bd5.doubleValue();
		// tokenDTO.setTotalReservedTokenCount(totalReserved);
		tokenDTO.setTotalReservedTokenCountString(truncateDecimal(totalReserved, 8).toPlainString());

		BigDecimal bd6 = truncateDecimal(spentReserved, 8);
		spentReserved = bd6.doubleValue();
		// tokenDTO.setSpentReservedTokenCount(spentReserved);
		tokenDTO.setSpentReservedTokenCountString(truncateDecimal(spentReserved, 8).toPlainString());

		BigDecimal bd8 = truncateDecimal(blcReserved, 8);
		spentReserved = bd8.doubleValue();
		// tokenDTO.setBlcReservedTokenCount(spentReserved);
		tokenDTO.setBlcReservedTokenCountString(truncateDecimal(spentReserved, 8).toPlainString());

		Double totalCount = airDropUserBonusRepository.getTheTotalTransferredTokenCount();
		System.out.println("totalCount : " + totalCount);
		Double purchaseTransferCount = purchaseTokenInfoRepository.getpurchasedTokenTransferCount();
		Double totalTokenTransferredCount = purchaseTransferCount + totalCount;
		tokenDTO.setTotalTokenTransferredCount(truncateDecimal(totalTokenTransferredCount, 8).toPlainString());

		return true;

	}

	@SuppressWarnings("unused")
	@Override
	public String validateVestingTokens(TokenDTO tokenDTO) throws Exception {

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

		List<PurchaseTokenInfo> purchaseList = purchaseTokenInfoRepository
				.findVestingListByEtherWalletAddressAndIsVestingAndTransferType(registerInfo.getWalletFile(), 1, 0);

		System.out.println("Wallet address " + tokenDTO.getWalletAddress());
		Double vestingCount = 0.0;

		if (purchaseList != null) {
			SaleDatesInfo saleDates = saleDatesInfoRepository.findById(1);
			Date currDate = new Date();
			Date startDate = saleDates.getPublicEndDate();
			long diff = currDate.getTime() - startDate.getTime();
			int diffInDays = (int) ((startDate.getTime() - currDate.getTime()) / (1000 * 60 * 60 * 24));

			if (diffInDays >= 364) {
				for (int i = 0; i < purchaseList.size(); i++) {
					vestingCount = vestingCount + purchaseList.get(i).getPurchaseTokens();

					System.out.println("Inside loop value--->" + purchaseList.get(i).getPurchaseTokens().doubleValue());
					System.out.println("Inside value--->" + vestingCount);
				}
			}
		}

		Double blc = tokenBig.doubleValue() - vestingCount;
		System.out.println("UserBalance------>" + blc);
		System.out.println("Request Token------->" + tokenDTO.getRequestToken());
		if (tokenDTO.getRequestToken() > blc) {
			return "You have only " + blc
					+ " left in your wallet excluding vesting tokens. Please transfer within that range.";
		}

		return "success";
	}

	@Override
	public boolean ValidateTokenBalanceForBurn(TokenDTO tokenDTO) throws Exception {
		try {
			tokenDTO.setMessage("Invalid Token Balance");
			DecimalFormat df = new DecimalFormat("0.00000000");

			Credentials credentials = WalletUtils.loadCredentials(env.getProperty("credentials.password"),
					env.getProperty("credentials.address"));

			// Credentials credentials =
			// WalletUtils.loadCredentials("Jansi@123","E://Ethereum//private-network//keystore//UTC--2018-07-16T14-07-09.940000000Z--9f8172b11c881d5ed73f19f97909a4421293b6f1.json");

			RemcoToken assetToken = RemcoToken.load(env.getProperty("token.address"), web3j, credentials,
					Contract.GAS_PRICE, Contract.GAS_LIMIT);
			/*
			 * AssetToken assetToken =
			 * AssetToken.load("0x261c6155748989b50e5D8bE19337cd43edDA9359", web3j,
			 * credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
			 */

			LOG.info("Total Supply" + assetToken.totalSupply().send());

			BigInteger balance = assetToken.balanceOf(env.getProperty("main.address")).send();

			Double balanceOfTokenUser = balance.doubleValue() / 100000000.0;

			ICOTokenInfo icoToken = icoTokenInfoRepository.findIcoTokenInfoByIcoKey("ICOToken");
			ReservedTokenInfo reservedToken = reservedTokenInfoRepository.findOne(1);

			Double totalTokBlc = balanceOfTokenUser
					- (icoToken.getIcoAvail().doubleValue() + reservedToken.getReservedAvail());
			BigDecimal bd = new BigDecimal(df.format(totalTokBlc));
			totalTokBlc = bd.doubleValue();

			if (tokenDTO.getBurnFlag().equals("ICO")) {

				if (icoToken.getIcoAvail().doubleValue() < tokenDTO.getTokens()) {
					BigDecimal bd1 = new BigDecimal(df.format(icoToken.getIcoAvail()));
					icoToken.setIcoAvail(bd1.doubleValue());
					tokenDTO.setMessage("Only " + BigDecimal.valueOf(icoToken.getIcoAvail()).toPlainString()
							+ " is available to Burn from ICO. Try within that Range!");
					return false;
				}
			} else if (tokenDTO.getBurnFlag().equals("NONICO")) {
				if (totalTokBlc.doubleValue() < tokenDTO.getTokens()) {
					BigDecimal bd2 = new BigDecimal(df.format(totalTokBlc));
					totalTokBlc = bd2.doubleValue();
					tokenDTO.setMessage("Only " + BigDecimal.valueOf(totalTokBlc).toPlainString()
							+ " is available to Burn. Try within that Range!");
					return false;
				}
			}

			// BigInteger balance =
			// assetToken.balanceOf("0x9F8172b11C881D5ed73F19f97909A4421293b6f1").send();
			Double tokenbalance = tokenDTO.getTokens();

			if (tokenbalance == 0) {
				return false;
			}
			// int count = balance.compareTo(tokenDTO.getBurnToken());
			if (totalTokBlc.doubleValue() > tokenDTO.getTokens()) {

				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean tokenPurchaseCancellationInVTN(TokenDTO tokenDTO) {

		if (tokenDTO.getPurchaseTokenId() != null) {
			boolean isCanceled = solidityHandler.purchaseTokenCanceledInVTN(tokenDTO);
			if (isCanceled) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean adminSoldBalance(TokenDTO tokenDTO) throws Exception {

		List<PurchaseTokenInfo> txSoldHistory = purchaseTokenInfoRepository
				.findByTransferTypeOrTransferTypeOrTransferTypeOrTransferType(0, 0, 0, 0);
		Double soldCount = 0.0;

		if (txSoldHistory != null) {
			for (int i = 0; i < txSoldHistory.size(); i++) {
				soldCount = soldCount + txSoldHistory.get(i).getPurchaseTokens();
			}
		}
		BigDecimal bd = truncateDecimal(soldCount, 8);
		soldCount = bd.doubleValue();
		tokenDTO.setIcoSoldTokenString(truncateDecimal(soldCount, 8).toPlainString());
		List<PurchaseTokenInfo> txHistory = purchaseTokenInfoRepository
				.findByTransferTypeOrTransferTypeOrTransferTypeOrTransferType(1, 2, 3, 4);
		Double reqCount = 0.0;
		if (txHistory != null) {
			for (int i = 0; i < txHistory.size(); i++) {
				reqCount = reqCount + txHistory.get(i).getPurchaseTokens();

			}
		}
		tokenDTO.setIcoRequestTokenString(truncateDecimal(reqCount, 8).toPlainString());
		return true;
	}

	@Override
	public boolean multipleTokenTransfer(TokenDTO tokenListDTO) {
		try {
			for (TokenTransferDTO tokenDTO : tokenListDTO.getTokenList()) {
				boolean transferred = solidityHandler.synchronizedManualTokenTransfer(tokenDTO, tokenListDTO);
				if (!transferred) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			LOG.info("Exception : " + e.toString());
			e.printStackTrace();
			return false;

		}
	}

	@Override
	public boolean promoTokenTransfer(UserRegisterDTO userRegisterDTO) {

		try {
			boolean transfer = solidityHandler.promoTokenTransfer(userRegisterDTO);
			if (transfer) {
				emailNotificationService.sendEmailforPromoTokenTransfer(userRegisterDTO.getEmailId(),
						"REMCO - EARNED TOKEN INFO ", userRegisterDTO.getFirstName(), userRegisterDTO.getNoOfTokens(),
						userRegisterDTO.getWalletAddress(), userRegisterDTO.getReferralLink());
			} else {
				return false;
			}

			return true;

		} catch (Exception e) {
			LOG.info("Exception : " + e.toString());
			e.printStackTrace();
			return false;

		}
	}

}
