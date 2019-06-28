package com.remco.ico.DTO;

import java.math.BigDecimal;
import java.util.Date;

public class UserRegisterDTO {
	
	private Integer id;
	private String firstName;
	private String lastName;
	private String middleName;
	private String emailId;
	private String password;
	private String newPassword;
	private String confirmPassword;
	private String walletPassword;
	private String walletAddress;
	private Integer userType;
	private String mobileno;
	private String sessionId;
    private Integer securedKey;
	private String message;
    private String isPopup;
    private Integer loginType;
    private String oldPassword;
    private Date createdDate;
    private Date keyDate;
    private String userName;
    private Integer status;
    private String oldWalletPassword;
    private String newWalletPassword;
    private String confirmWalletPassword;
    private String refEmailId;
    private String userAddress;
    private String userCity;
    private String userZipcode;
    private String userCountry;
    private String referralId;
    private String kycStatus;
    private String bitcoinCashWalletAddress;
    private String bitcoinWalletAddress;
	private Double bitcoinWalletBalance;
	private Double bitcoinCashWalletBalance;
	private String referralEmailId;
	private Integer twofaStatus;
	private String ipAddress;
	private Integer pageNum;
	private Integer pageSize;
	private Long totalElements;
	private Integer totalPages;
	private String searchTxt;
	private Integer userStatus;
	private String refUserName;
	private Integer userCount;
	private Double tokenAmount;
	private String typeOfPurchase;
	private Double amount;
	private Double noOfTokens;
	private String saleType;
	private String ipFlag;
	private String[] ipAddresses; 
	private String userEmailId;
	private String mailFlag;
	private Integer tokenStatus;
	private Double promoToken;
	private String accessToken;
	private String hashSecret;
	private String promoFlag;
	private Integer promoId;
	private String referralLink;
	private String allocationType;
	private BigDecimal earnedToken;
	private Date transferredDate;
	private String referSite;
	private String earnedTokenString;
	private String subject;
    
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Double getNoOfTokens() {
		return noOfTokens;
	}
	public void setNoOfTokens(Double noOfTokens) {
		this.noOfTokens = noOfTokens;
	}
	public Double getTokenAmount() {
		return tokenAmount;
	}
	public void setTokenAmount(Double tokenAmount) {
		this.tokenAmount = tokenAmount;
	}
	public String getTypeOfPurchase() {
		return typeOfPurchase;
	}
	public void setTypeOfPurchase(String typeOfPurchase) {
		this.typeOfPurchase = typeOfPurchase;
	}

	public Integer getUserCount() {
		return userCount;
	}
	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}
	public String getRefUserName() {
		return refUserName;
	}
	public void setRefUserName(String refUserName) {
		this.refUserName = refUserName;
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
	public String getBitcoinCashWalletAddress() {
		return bitcoinCashWalletAddress;
	}
	public void setBitcoinCashWalletAddress(String bitcoinCashWalletAddress) {
		this.bitcoinCashWalletAddress = bitcoinCashWalletAddress;
	}
	public String getBitcoinWalletAddress() {
		return bitcoinWalletAddress;
	}
	public void setBitcoinWalletAddress(String bitcoinWalletAddress) {
		this.bitcoinWalletAddress = bitcoinWalletAddress;
	}
	public Double getBitcoinWalletBalance() {
		return bitcoinWalletBalance;
	}
	public void setBitcoinWalletBalance(Double bitcoinWalletBalance) {
		this.bitcoinWalletBalance = bitcoinWalletBalance;
	}
	public Double getBitcoinCashWalletBalance() {
		return bitcoinCashWalletBalance;
	}
	public void setBitcoinCashWalletBalance(Double bitcoinCashWalletBalance) {
		this.bitcoinCashWalletBalance = bitcoinCashWalletBalance;
	}
	public String getReferralId() {
		return referralId;
	}
	public void setReferralId(String referralId) {
		this.referralId = referralId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getWalletPassword() {
		return walletPassword;
	}
	public void setWalletPassword(String walletPassword) {
		this.walletPassword = walletPassword;
	}
	public String getWalletAddress() {
		return walletAddress;
	}
	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Integer getSecuredKey() {
		return securedKey;
	}
	public void setSecuredKey(Integer securedKey) {
		this.securedKey = securedKey;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getIsPopup() {
		return isPopup;
	}
	public void setIsPopup(String isPopup) {
		this.isPopup = isPopup;
	}
	public Integer getLoginType() {
		return loginType;
	}
	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getKeyDate() {
		return keyDate;
	}
	public void setKeyDate(Date keyDate) {
		this.keyDate = keyDate;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getOldWalletPassword() {
		return oldWalletPassword;
	}
	public void setOldWalletPassword(String oldWalletPassword) {
		this.oldWalletPassword = oldWalletPassword;
	}
	public String getNewWalletPassword() {
		return newWalletPassword;
	}
	public void setNewWalletPassword(String newWalletPassword) {
		this.newWalletPassword = newWalletPassword;
	}
	public String getConfirmWalletPassword() {
		return confirmWalletPassword;
	}
	public void setConfirmWalletPassword(String confirmWalletPassword) {
		this.confirmWalletPassword = confirmWalletPassword;
	}
	public String getRefEmailId() {
		return refEmailId;
	}
	public void setRefEmailId(String refEmailId) {
		this.refEmailId = refEmailId;
	}
	public String getUserAddress() {
		return userAddress;
	}
	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}
	public String getUserCity() {
		return userCity;
	}
	public void setUserCity(String userCity) {
		this.userCity = userCity;
	}
	public String getUserZipcode() {
		return userZipcode;
	}
	public void setUserZipcode(String userZipcode) {
		this.userZipcode = userZipcode;
	}
	public String getUserCountry() {
		return userCountry;
	}
	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}
	public String getKycStatus() {
		return kycStatus;
	}
	public void setKycStatus(String kycStatus) {
		this.kycStatus = kycStatus;
	}
	public String getReferralEmailId() {
		return referralEmailId;
	}
	public void setReferralEmailId(String referralEmailId) {
		this.referralEmailId = referralEmailId;
	}
	public Integer getTwofaStatus() {
		return twofaStatus;
	}
	public void setTwofaStatus(Integer twofaStatus) {
		this.twofaStatus = twofaStatus;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
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
	public Integer getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getSaleType() {
		return saleType;
	}
	public void setSaleType(String saleType) {
		this.saleType = saleType;
	}
	public String getIpFlag() {
		return ipFlag;
	}
	public void setIpFlag(String ipFlag) {
		this.ipFlag = ipFlag;
	}
	public String[] getIpAddresses() {
		return ipAddresses;
	}
	public void setIpAddresses(String[] ipAddresses) {
		this.ipAddresses = ipAddresses;
	}
	public String getUserEmailId() {
		return userEmailId;
	}
	public void setUserEmailId(String userEmailId) {
		this.userEmailId = userEmailId;
	}
	public String getMailFlag() {
		return mailFlag;
	}
	public void setMailFlag(String mailFlag) {
		this.mailFlag = mailFlag;
	}
	public Integer getTokenStatus() {
		return tokenStatus;
	}
	public void setTokenStatus(Integer tokenStatus) {
		this.tokenStatus = tokenStatus;
	}
	public Double getPromoToken() {
		return promoToken;
	}
	public void setPromoToken(Double promoToken) {
		this.promoToken = promoToken;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getHashSecret() {
		return hashSecret;
	}
	public void setHashSecret(String hashSecret) {
		this.hashSecret = hashSecret;
	}
	public String getPromoFlag() {
		return promoFlag;
	}
	public void setPromoFlag(String promoFlag) {
		this.promoFlag = promoFlag;
	}
	public Integer getPromoId() {
		return promoId;
	}
	public void setPromoId(Integer promoId) {
		this.promoId = promoId;
	}
	public String getReferralLink() {
		return referralLink;
	}
	public void setReferralLink(String referralLink) {
		this.referralLink = referralLink;
	}
	public String getAllocationType() {
		return allocationType;
	}
	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}
	public BigDecimal getEarnedToken() {
		return earnedToken;
	}
	public void setEarnedToken(BigDecimal earnedToken) {
		this.earnedToken = earnedToken;
	}
	public Date getTransferredDate() {
		return transferredDate;
	}
	public void setTransferredDate(Date transferredDate) {
		this.transferredDate = transferredDate;
	}
	public String getReferSite() {
		return referSite;
	}
	public void setReferSite(String referSite) {
		this.referSite = referSite;
	}
	public String getEarnedTokenString() {
		return earnedTokenString;
	}
	public void setEarnedTokenString(String earnedTokenString) {
		this.earnedTokenString = earnedTokenString;
	}


}
