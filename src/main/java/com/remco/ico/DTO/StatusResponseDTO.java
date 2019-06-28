package com.remco.ico.DTO;

import java.math.BigDecimal;
import java.util.List;

public class StatusResponseDTO {
	
	private String status;
	private String message;
	private LoginDTO loginInfo;
	private List<KycDTO> kycList;
	private List<TokenDTO> transactionHistoryInfo;
	private TokenDTO tokenBalance;
	private TokenDTO etherBalanceInfo;
	private TokenDTO getTokenValue;
    private BigDecimal coinRate;
    private String discountPercent;
    private String bonusPercent;
    private String vestingDiscount;
    private TokenDTO getSaleDates;
	private List<UserRegisterDTO> userList;
	private Double bchBalance;
	private Double btcBalance;
	private List<TokenDTO> purchaseListInfo;
	private String securedKey;
	private Long totalElements;
	private Integer totalPages;
	private Integer totalUserCount;
	private TokenDTO pg_formFields;
	private String pg_ActionURL;
	private String typeOfPurchase;
	private Double numberOfTokens;
	private String purchaseTokenId;
	private UserRegisterDTO noOfTokens;
	private UserRegisterDTO amount;
	private String tokenBalanceString;
	private List<SaleTypeDTO> TokenRateInfo;
	private String[] ipAddresses;
	private String promoTokenValue;
	private Integer id;
	private String referralLink;
	private String walletAddress;
	private Integer userStatus;
	private Integer noOfUsers;
	
	public Integer getNoOfUsers() {
		return noOfUsers;
	}
	public void setNoOfUsers(Integer noOfUsers) {
		this.noOfUsers = noOfUsers;
	}
	public UserRegisterDTO getAmount() {
		return amount;
	}
	public void setAmount(UserRegisterDTO amount) {
		this.amount = amount;
	}
	public UserRegisterDTO getNoOfTokens() {
		return noOfTokens;
	}
	public void setNoOfTokens(UserRegisterDTO noOfTokens) {
		this.noOfTokens = noOfTokens;
	}

	public String getPurchaseTokenId() {
		return purchaseTokenId;
	}
	public void setPurchaseTokenId(String purchaseTokenId) {
		this.purchaseTokenId = purchaseTokenId;
	}
	public Double getNumberOfTokens() {
		return numberOfTokens;
	}
	public void setNumberOfTokens(Double numberOfTokens) {
		this.numberOfTokens = numberOfTokens;
	}
	public TokenDTO getPg_formFields() {
		return pg_formFields;
	}
	public void setPg_formFields(TokenDTO pg_formFields) {
		this.pg_formFields = pg_formFields;
	}
	public String getTypeOfPurchase() {
		return typeOfPurchase;
	}
	public void setTypeOfPurchase(String typeOfPurchase) {
		this.typeOfPurchase = typeOfPurchase;
	}
	public String getPg_ActionURL() {
		return pg_ActionURL;
	}
	public void setPg_ActionURL(String pg_ActionURL) {
		this.pg_ActionURL = pg_ActionURL;
	}

	public Integer getTotalUserCount() {
		return totalUserCount;
	}
	public void setTotalUserCount(Integer totalUserCount) {
		this.totalUserCount = totalUserCount;
	}
	public List<TokenDTO> getPurchaseListInfo() {
		return purchaseListInfo;
	}
	public void setPurchaseListInfo(List<TokenDTO> purchaseListInfo) {
		this.purchaseListInfo = purchaseListInfo;
	}
	public Double getBchBalance() {
		return bchBalance;
	}
	public void setBchBalance(Double bchBalance) {
		this.bchBalance = bchBalance;
	}
	public Double getBtcBalance() {
		return btcBalance;
	}
	public void setBtcBalance(Double btcBalance) {
		this.btcBalance = btcBalance;
	}
	public List<TokenDTO> getTransactionHistoryInfo() {
		return transactionHistoryInfo;
	}
	public void setTransactionHistoryInfo(List<TokenDTO> transactionHistoryInfo) {
		this.transactionHistoryInfo = transactionHistoryInfo;
	}
	public TokenDTO getTokenBalance() {
		return tokenBalance;
	}
	public void setTokenBalance(TokenDTO tokenBalance) {
		this.tokenBalance = tokenBalance;
	}
	public TokenDTO getEtherBalanceInfo() {
		return etherBalanceInfo;
	}
	public void setEtherBalanceInfo(TokenDTO etherBalanceInfo) {
		this.etherBalanceInfo = etherBalanceInfo;
	}
	public TokenDTO getGetTokenValue() {
		return getTokenValue;
	}
	public void setGetTokenValue(TokenDTO getTokenValue) {
		this.getTokenValue = getTokenValue;
	}
	public List<KycDTO> getKycList() {
		return kycList;
	}
	public void setKycList(List<KycDTO> kycList) {
		this.kycList = kycList;
	}
	public KycDTO getKycUserInfo() {
		return kycUserInfo;
	}
	public void setKycUserInfo(KycDTO kycUserInfo) {
		this.kycUserInfo = kycUserInfo;
	}
	private KycDTO kycUserInfo;
	
	public LoginDTO getLoginInfo() {
		return loginInfo;
	}
	public void setLoginInfo(LoginDTO loginInfo) {
		this.loginInfo = loginInfo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public BigDecimal getCoinRate() {
		return coinRate;
	}
	public void setCoinRate(BigDecimal coinRate) {
		this.coinRate = coinRate;
	}
	public String getDiscountPercent() {
		return discountPercent;
	}
	public void setDiscountPercent(String discountPercent) {
		this.discountPercent = discountPercent;
	}
	public String getBonusPercent() {
		return bonusPercent;
	}
	public void setBonusPercent(String bonusPercent) {
		this.bonusPercent = bonusPercent;
	}
	public String getVestingDiscount() {
		return vestingDiscount;
	}
	public void setVestingDiscount(String vestingDiscount) {
		this.vestingDiscount = vestingDiscount;
	}
	public TokenDTO getGetSaleDates() {
		return getSaleDates;
	}
	public void setGetSaleDates(TokenDTO getSaleDates) {
		this.getSaleDates = getSaleDates;
	}
	public List<UserRegisterDTO> getUserList() {
		return userList;
	}
	public void setUserList(List<UserRegisterDTO> userList) {
		this.userList = userList;
	}
	public String getSecuredKey() {
		return securedKey;
	}
	public void setSecuredKey(String securedKey) {
		this.securedKey = securedKey;
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
	public String getTokenBalanceString() {
		return tokenBalanceString;
	}
	public void setTokenBalanceString(String tokenBalanceString) {
		this.tokenBalanceString = tokenBalanceString;
	}
	public List<SaleTypeDTO> getTokenRateInfo() {
		return TokenRateInfo;
	}
	public void setTokenRateInfo(List<SaleTypeDTO> tokenRateInfo) {
		TokenRateInfo = tokenRateInfo;
	}
	public String[] getIpAddresses() {
		return ipAddresses;
	}
	public void setIpAddresses(String[] ipAddresses) {
		this.ipAddresses = ipAddresses;
	}
	public String getPromoTokenValue() {
		return promoTokenValue;
	}
	public void setPromoTokenValue(String promoTokenValue) {
		this.promoTokenValue = promoTokenValue;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getReferralLink() {
		return referralLink;
	}
	public void setReferralLink(String referralLink) {
		this.referralLink = referralLink;
	}
	public String getWalletAddress() {
		return walletAddress;
	}
	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}
	public Integer getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}

}
