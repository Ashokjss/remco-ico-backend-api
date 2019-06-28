package com.remco.ico.DTO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class TokenDTO {
	
	private Integer id;
	private BigInteger initialValue;
	private String coinName;
	private String coinSymbol;
	private BigInteger decimalUnits;
	private String centralAdmin;
	private Double mainBalance;
	private Double tokens;
	private String toAddress;
	private String fromAddress;
	private Double amount;
	private Double transferAmount;
	private Double icoRequestToken;
	private String icoRequestTokenString;
	private Double requestAmount;
	private BigInteger gasValue;
	private String status;
	private Double mintToken;
	private Double icoSoldToken;
	private String icoSoldTokenString;
	private String walletAddress;
	private String walletPassword;
	private Double tokenBalance;
	private String emailId;
	private String tokenName;
	private BigDecimal tokenValue;
	private String tokenValueString;
	private Integer transactionType;
	private Date transferDate;
	private String transferStatus;
	private Double tokenAmount;
	private BigDecimal etherBalance;
	private String discount;
	private String referralBonus;
	private String vestingDisc;
	private Date privateStartDate;
	private Date privateEndDate;
	private Date prePublicStartDate;
	private Date prePublicEndDate;
	private Date publicStartDate;
	private Date publicEndDate;
	private String minContribute;
	private String tokenTransId;
	private String typeOfPurchase;
	private Double ethAmount;
	private Double btcAmount;
	private Double bchAmount;
	private String schoolName;
	private Date dateOfBirth;
	private Integer isVesting;
	private Double freeTokens;
	private String etherWalletAddress;
	private String btcWalletAddress;
	private String bchWalletAddress;
	private Date purchasedDate;
	private Double cryptoAmount;
	private Integer securedKey;
	private Integer userType;
	private Integer isPrivateAccredit;
	private Integer isPresaleAccredit;
	private Integer isPublicAccredit;
	private Integer pageNum;
	private Integer pageSize;
	private Long totalElements;
	private Integer totalPages;
	private String searchTxt;
	private Double totalIcoToken;
	private String totalIcoTokenString;
	private Double totalSoldToken;
	private String totalSoldTokenString;
	private Double totalToken;
	private String totalTokenString;
	private String amountString;
	private String burntCountString;
	private Double burntCount;
	private String tokenBalanceString;
	private Double totaltokenBalance;
	private String totalTokenBalanceString;
	private String tokenFlag;
	private Double icoAvailableCount;
	private String icoAvailableCountString;
	private Double totalReservedTokenCount;
	private String totalReservedTokenCountString;
	private Double spentReservedTokenCount;
	private String spentReservedTokenCountString;
	private Double blcReservedTokenCount;
	private String blcReservedTokenCountString;
	private Double nonIcoTokenCount;
	private String nonIcoTokenCountString;
	private Double spentNonIcoTokenCount;
	private String spentNonIcoTokenCountString;
	private Double blcNonIcoTokenCount;
	private String blcNonIcoTokenCountString;
	private Double requestToken;
	private String requestTokenString;
	private Double totalIcoTokenCount;
	private String totalIcoTokenCountString;
	private String totalTokenTransferredCount;
	private String burnFlag;
	private String message;
	private String merchant_email_id;
	private String item_name;
	private String custom_remarks;
	private String callback_id;
	private String ipnRefNumber;
	private String return_url;
	private String cancel_url;
	private String currency_id;
	private String purchaseTokenId;
	private Date startDate;
	private Date endDate;
	private String keyword;
	private String ngnValue;
	private String tokenAmountString;
	private String saleType;
	private Double referralCommission;
	private Double referralpurchasedTokens;
	private String firstName;
	private String lastName;
	private Date referredDate;
	private Date registeredDate;
	private Integer referralStatus;
	private List<TokenTransferDTO> tokenList;
	private Integer userCount;
	
	public String getTotalTokenTransferredCount() {
		return totalTokenTransferredCount;
	}
	public void setTotalTokenTransferredCount(String totalTokenTransferredCount) {
		this.totalTokenTransferredCount = totalTokenTransferredCount;
	}
	public Integer getUserCount() {
		return userCount;
	}
	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}
	public Date getRegisteredDate() {
		return registeredDate;
	}
	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}
	public Date getReferredDate() {
		return referredDate;
	}
	public void setReferredDate(Date referredDate) {
		this.referredDate = referredDate;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Double getReferralpurchasedTokens() {
		return referralpurchasedTokens;
	}
	public void setReferralpurchasedTokens(Double referralpurchasedTokens) {
		this.referralpurchasedTokens = referralpurchasedTokens;
	}
	public Double getReferralCommission() {
		return referralCommission;
	}
	public void setReferralCommission(Double referralCommission) {
		this.referralCommission = referralCommission;
	}
	public String getMerchant_email_id() {
		return merchant_email_id;
	}
	public void setMerchant_email_id(String merchant_email_id) {
		this.merchant_email_id = merchant_email_id;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getCustom_remarks() {
		return custom_remarks;
	}
	public void setCustom_remarks(String custom_remarks) {
		this.custom_remarks = custom_remarks;
	}
	public String getCallback_id() {
		return callback_id;
	}
	public void setCallback_id(String callback_id) {
		this.callback_id = callback_id;
	}
	public String getPurchaseTokenId() {
		return purchaseTokenId;
	}
	public void setPurchaseTokenId(String purchaseTokenId) {
		this.purchaseTokenId = purchaseTokenId;
	}
	public String getReturn_url() {
		return return_url;
	}
	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}
	public String getCancel_url() {
		return cancel_url;
	}
	public void setCancel_url(String cancel_url) {
		this.cancel_url = cancel_url;
	}
	public String getCurrency_id() {
		return currency_id;
	}
	public void setCurrency_id(String currency_id) {
		this.currency_id = currency_id;
	}
	public String getIpnRefNumber() {
		return ipnRefNumber;
	}
	public void setIpnRefNumber(String ipnRefNumber) {
		this.ipnRefNumber = ipnRefNumber;
	}
	public Double getRequestToken() {
		return requestToken;
	}
	public void setRequestToken(Double requestToken) {
		this.requestToken = requestToken;
	}
	public String getRequestTokenString() {
		return requestTokenString;
	}
	public void setRequestTokenString(String requestTokenString) {
		this.requestTokenString = requestTokenString;
	}
	public String getTokenFlag() {
		return tokenFlag;
	}
	public void setTokenFlag(String tokenFlag) {
		this.tokenFlag = tokenFlag;
	}
	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getIsPrivateAccredit() {
		return isPrivateAccredit;
	}
	public void setIsPrivateAccredit(Integer isPrivateAccredit) {
		this.isPrivateAccredit = isPrivateAccredit;
	}
	public Integer getIsPresaleAccredit() {
		return isPresaleAccredit;
	}
	public void setIsPresaleAccredit(Integer isPresaleAccredit) {
		this.isPresaleAccredit = isPresaleAccredit;
	}
	public Integer getIsPublicAccredit() {
		return isPublicAccredit;
	}
	public void setIsPublicAccredit(Integer isPublicAccredit) {
		this.isPublicAccredit = isPublicAccredit;
	}
	public Double getEthAmount() {
		return ethAmount;
	}
	public void setEthAmount(Double ethAmount) {
		this.ethAmount = ethAmount;
	}
	public String getTypeOfPurchase() {
		return typeOfPurchase;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public Integer getIsVesting() {
		return isVesting;
	}
	public void setIsVesting(Integer isVesting) {
		this.isVesting = isVesting;
	}
	public Double getFreeTokens() {
		return freeTokens;
	}
	public void setFreeTokens(Double freeTokens) {
		this.freeTokens = freeTokens;
	}
	public String getEtherWalletAddress() {
		return etherWalletAddress;
	}
	public void setEtherWalletAddress(String etherWalletAddress) {
		this.etherWalletAddress = etherWalletAddress;
	}
	public String getBtcWalletAddress() {
		return btcWalletAddress;
	}
	public void setBtcWalletAddress(String btcWalletAddress) {
		this.btcWalletAddress = btcWalletAddress;
	}
	public String getBchWalletAddress() {
		return bchWalletAddress;
	}
	public void setBchWalletAddress(String bchWalletAddress) {
		this.bchWalletAddress = bchWalletAddress;
	}
	public Date getPurchasedDate() {
		return purchasedDate;
	}
	public void setPurchasedDate(Date purchasedDate) {
		this.purchasedDate = purchasedDate;
	}
	public Double getCryptoAmount() {
		return cryptoAmount;
	}
	public void setCryptoAmount(Double cryptoAmount) {
		this.cryptoAmount = cryptoAmount;
	}
	public void setTypeOfPurchase(String typeOfPurchase) {
		this.typeOfPurchase = typeOfPurchase;
	}
	public Double getBtcAmount() {
		return btcAmount;
	}
	public void setBtcAmount(Double btcAmount) {
		this.btcAmount = btcAmount;
	}
	public Double getBchAmount() {
		return bchAmount;
	}
	public void setBchAmount(Double bchAmount) {
		this.bchAmount = bchAmount;
	}
	public Integer getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(Integer transactionType) {
		this.transactionType = transactionType;
	}
	public Date getTransferDate() {
		return transferDate;
	}
	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}
	public String getTransferStatus() {
		return transferStatus;
	}
	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}
	public Double getTokenAmount() {
		return tokenAmount;
	}
	public void setTokenAmount(Double tokenAmount) {
		this.tokenAmount = tokenAmount;
	}
	public BigDecimal getEtherBalance() {
		return etherBalance;
	}
	public void setEtherBalance(BigDecimal etherBalance) {
		this.etherBalance = etherBalance;
	}
	public String getTokenName() {
		return tokenName;
	}
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BigInteger getInitialValue() {
		return initialValue;
	}
	public void setInitialValue(BigInteger initialValue) {
		this.initialValue = initialValue;
	}
	public String getCoinName() {
		return coinName;
	}
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}
	public String getCoinSymbol() {
		return coinSymbol;
	}
	public void setCoinSymbol(String coinSymbol) {
		this.coinSymbol = coinSymbol;
	}
	public BigInteger getDecimalUnits() {
		return decimalUnits;
	}
	public void setDecimalUnits(BigInteger decimalUnits) {
		this.decimalUnits = decimalUnits;
	}
	public String getCentralAdmin() {
		return centralAdmin;
	}
	public void setCentralAdmin(String centralAdmin) {
		this.centralAdmin = centralAdmin;
	}
	public Double getMainBalance() {
		return mainBalance;
	}
	public void setMainBalance(Double mainBalance) {
		this.mainBalance = mainBalance;
	}
	public Double getTokens() {
		return tokens;
	}
	public void setTokens(Double tokens) {
		this.tokens = tokens;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getTransferAmount() {
		return transferAmount;
	}
	public void setTransferAmount(Double transferAmount) {
		this.transferAmount = transferAmount;
	}
	public Double getRequestAmount() {
		return requestAmount;
	}
	public void setRequestAmount(Double requestAmount) {
		this.requestAmount = requestAmount;
	}
	public BigInteger getGasValue() {
		return gasValue;
	}
	public void setGasValue(BigInteger gasValue) {
		this.gasValue = gasValue;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Double getMintToken() {
		return mintToken;
	}
	public void setMintToken(Double mintToken) {
		this.mintToken = mintToken;
	}
	public String getWalletAddress() {
		return walletAddress;
	}
	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}
	public String getWalletPassword() {
		return walletPassword;
	}
	public void setWalletPassword(String walletPassword) {
		this.walletPassword = walletPassword;
	}
	public Double getIcoRequestToken() {
		return icoRequestToken;
	}
	public void setIcoRequestToken(Double icoRequestToken) {
		this.icoRequestToken = icoRequestToken;
	}
	public String getIcoRequestTokenString() {
		return icoRequestTokenString;
	}
	public void setIcoRequestTokenString(String icoRequestTokenString) {
		this.icoRequestTokenString = icoRequestTokenString;
	}
	public Double getIcoSoldToken() {
		return icoSoldToken;
	}
	public void setIcoSoldToken(Double icoSoldToken) {
		this.icoSoldToken = icoSoldToken;
	}
	public String getIcoSoldTokenString() {
		return icoSoldTokenString;
	}
	public void setIcoSoldTokenString(String icoSoldTokenString) {
		this.icoSoldTokenString = icoSoldTokenString;
	}
	public Double getIcoAvailableCount() {
		return icoAvailableCount;
	}
	public void setIcoAvailableCount(Double icoAvailableCount) {
		this.icoAvailableCount = icoAvailableCount;
	}
	public Double getTotalReservedTokenCount() {
		return totalReservedTokenCount;
	}
	public void setTotalReservedTokenCount(Double totalReservedTokenCount) {
		this.totalReservedTokenCount = totalReservedTokenCount;
	}
	public String getTotalReservedTokenCountString() {
		return totalReservedTokenCountString;
	}
	public void setTotalReservedTokenCountString(String totalReservedTokenCountString) {
		this.totalReservedTokenCountString = totalReservedTokenCountString;
	}
	public Double getSpentReservedTokenCount() {
		return spentReservedTokenCount;
	}
	public void setSpentReservedTokenCount(Double spentReservedTokenCount) {
		this.spentReservedTokenCount = spentReservedTokenCount;
	}
	public String getSpentReservedTokenCountString() {
		return spentReservedTokenCountString;
	}
	public void setSpentReservedTokenCountString(String spentReservedTokenCountString) {
		this.spentReservedTokenCountString = spentReservedTokenCountString;
	}
	public Double getBlcReservedTokenCount() {
		return blcReservedTokenCount;
	}
	public void setBlcReservedTokenCount(Double blcReservedTokenCount) {
		this.blcReservedTokenCount = blcReservedTokenCount;
	}
	public String getBlcReservedTokenCountString() {
		return blcReservedTokenCountString;
	}
	public void setBlcReservedTokenCountString(String blcReservedTokenCountString) {
		this.blcReservedTokenCountString = blcReservedTokenCountString;
	}
	public Double getNonIcoTokenCount() {
		return nonIcoTokenCount;
	}
	public void setNonIcoTokenCount(Double nonIcoTokenCount) {
		this.nonIcoTokenCount = nonIcoTokenCount;
	}
	public String getNonIcoTokenCountString() {
		return nonIcoTokenCountString;
	}
	public void setNonIcoTokenCountString(String nonIcoTokenCountString) {
		this.nonIcoTokenCountString = nonIcoTokenCountString;
	}
	public Double getSpentNonIcoTokenCount() {
		return spentNonIcoTokenCount;
	}
	public void setSpentNonIcoTokenCount(Double spentNonIcoTokenCount) {
		this.spentNonIcoTokenCount = spentNonIcoTokenCount;
	}
	public String getSpentNonIcoTokenCountString() {
		return spentNonIcoTokenCountString;
	}
	public void setSpentNonIcoTokenCountString(String spentNonIcoTokenCountString) {
		this.spentNonIcoTokenCountString = spentNonIcoTokenCountString;
	}
	public Double getBlcNonIcoTokenCount() {
		return blcNonIcoTokenCount;
	}
	public void setBlcNonIcoTokenCount(Double blcNonIcoTokenCount) {
		this.blcNonIcoTokenCount = blcNonIcoTokenCount;
	}
	public String getBlcNonIcoTokenCountString() {
		return blcNonIcoTokenCountString;
	}
	public void setBlcNonIcoTokenCountString(String blcNonIcoTokenCountString) {
		this.blcNonIcoTokenCountString = blcNonIcoTokenCountString;
	}
	public Double getTokenBalance() {
		return tokenBalance;
	}
	public void setTokenBalance(Double tokenBalance) {
		this.tokenBalance = tokenBalance;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public BigDecimal getTokenValue() {
		return tokenValue;
	}
	public void setTokenValue(BigDecimal tokenValue) {
		this.tokenValue = tokenValue;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public Date getPrivateStartDate() {
		return privateStartDate;
	}
	public void setPrivateStartDate(Date privateStartDate) {
		this.privateStartDate = privateStartDate;
	}
	public Date getPrivateEndDate() {
		return privateEndDate;
	}
	public void setPrivateEndDate(Date privateEndDate) {
		this.privateEndDate = privateEndDate;
	}
	public Date getPrePublicStartDate() {
		return prePublicStartDate;
	}
	public void setPrePublicStartDate(Date prePublicStartDate) {
		this.prePublicStartDate = prePublicStartDate;
	}
	public Date getPrePublicEndDate() {
		return prePublicEndDate;
	}
	public void setPrePublicEndDate(Date prePublicEndDate) {
		this.prePublicEndDate = prePublicEndDate;
	}
	public Date getPublicStartDate() {
		return publicStartDate;
	}
	public void setPublicStartDate(Date publicStartDate) {
		this.publicStartDate = publicStartDate;
	}
	public Date getPublicEndDate() {
		return publicEndDate;
	}
	public void setPublicEndDate(Date publicEndDate) {
		this.publicEndDate = publicEndDate;
	}
	public String getMinContribute() {
		return minContribute;
	}
	public void setMinContribute(String minContribute) {
		this.minContribute = minContribute;
	}
	public String getReferralBonus() {
		return referralBonus;
	}
	public void setReferralBonus(String referralBonus) {
		this.referralBonus = referralBonus;
	}
	public String getVestingDisc() {
		return vestingDisc;
	}
	public void setVestingDisc(String vestingDisc) {
		this.vestingDisc = vestingDisc;
	}
	public String getTokenTransId() {
		return tokenTransId;
	}
	public void setTokenTransId(String tokenTransId) {
		this.tokenTransId = tokenTransId;
	}
	public Integer getSecuredKey() {
		return securedKey;
	}
	public void setSecuredKey(Integer securedKey) {
		this.securedKey = securedKey;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public Long getTotalElements() {
		return totalElements;
	}
	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}
	public Integer getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	public String getSearchTxt() {
		return searchTxt;
	}
	public void setSearchTxt(String searchTxt) {
		this.searchTxt = searchTxt;
	}
	public Double getTotalSoldToken() {
		return totalSoldToken;
	}
	public void setTotalSoldToken(Double totalSoldToken) {
		this.totalSoldToken = totalSoldToken;
	}
	public String getTotalSoldTokenString() {
		return totalSoldTokenString;
	}
	public void setTotalSoldTokenString(String totalSoldTokenString) {
		this.totalSoldTokenString = totalSoldTokenString;
	}
	public Double getTotalToken() {
		return totalToken;
	}
	public void setTotalToken(Double totalToken) {
		this.totalToken = totalToken;
	}
	public String getTotalTokenString() {
		return totalTokenString;
	}
	public void setTotalTokenString(String totalTokenString) {
		this.totalTokenString = totalTokenString;
	}
	public String getAmountString() {
		return amountString;
	}
	public void setAmountString(String amountString) {
		this.amountString = amountString;
	}
	public String getBurntCountString() {
		return burntCountString;
	}
	public void setBurntCountString(String burntCountString) {
		this.burntCountString = burntCountString;
	}
	public String getTokenValueString() {
		return tokenValueString;
	}
	public void setTokenValueString(String tokenValueString) {
		this.tokenValueString = tokenValueString;
	}
	public Double getBurntCount() {
		return burntCount;
	}
	public void setBurntCount(Double burntCount) {
		this.burntCount = burntCount;
	}
	public String getTokenBalanceString() {
		return tokenBalanceString;
	}
	public void setTokenBalanceString(String tokenBalanceString) {
		this.tokenBalanceString = tokenBalanceString;
	}
	public Double getTotaltokenBalance() {
		return totaltokenBalance;
	}
	public void setTotaltokenBalance(Double totaltokenBalance) {
		this.totaltokenBalance = totaltokenBalance;
	}
	public String getTotalTokenBalanceString() {
		return totalTokenBalanceString;
	}
	public void setTotalTokenBalanceString(String totalTokenBalanceString) {
		this.totalTokenBalanceString = totalTokenBalanceString;
	}
	public Double getTotalIcoTokenCount() {
		return totalIcoTokenCount;
	}
	public void setTotalIcoTokenCount(Double totalIcoTokenCount) {
		this.totalIcoTokenCount = totalIcoTokenCount;
	}
	public String getTotalIcoTokenCountString() {
		return totalIcoTokenCountString;
	}
	public void setTotalIcoTokenCountString(String totalIcoTokenCountString) {
		this.totalIcoTokenCountString = totalIcoTokenCountString;
	}
	public String getIcoAvailableCountString() {
		return icoAvailableCountString;
	}
	public void setIcoAvailableCountString(String icoAvailableCountString) {
		this.icoAvailableCountString = icoAvailableCountString;
	}
	public Double getTotalIcoToken() {
		return totalIcoToken;
	}
	public void setTotalIcoToken(Double totalIcoToken) {
		this.totalIcoToken = totalIcoToken;
	}
	public String getTotalIcoTokenString() {
		return totalIcoTokenString;
	}
	public void setTotalIcoTokenString(String totalIcoTokenString) {
		this.totalIcoTokenString = totalIcoTokenString;
	}
	public String getBurnFlag() {
		return burnFlag;
	}
	public void setBurnFlag(String burnFlag) {
		this.burnFlag = burnFlag;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getNgnValue() {
		return ngnValue;
	}
	public void setNgnValue(String ngnValue) {
		this.ngnValue = ngnValue;
	}
	public String getTokenAmountString() {
		return tokenAmountString;
	}
	public void setTokenAmountString(String tokenAmountString) {
		this.tokenAmountString = tokenAmountString;
	}
	public String getSaleType() {
		return saleType;
	}
	public void setSaleType(String saleType) {
		this.saleType = saleType;
	}
	public Integer getReferralStatus() {
		return referralStatus;
	}
	public void setReferralStatus(Integer referralStatus) {
		this.referralStatus = referralStatus;
	}
	public List<TokenTransferDTO> getTokenList() {
		return tokenList;
	}
	public void setTokenList(List<TokenTransferDTO> tokenList) {
		this.tokenList = tokenList;
	}


}
