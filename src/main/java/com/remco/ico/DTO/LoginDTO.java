package com.remco.ico.DTO;

import java.math.BigDecimal;
import java.util.Date;

public class LoginDTO {
	
	private String mobileNo;
	private String isdCode;
	private String emailId;
	private String password;
	private String userId;
	private String walletAddress;
	private Double walletBalance;
	private String id;
	private Integer userType;
    private String sessionId;
    private String tokenAddress;
    private String securedKey;
    private String status;
    private BigDecimal coinRate;
    private String message;
    private Date createdDate;
	private String firstName;
	private String lastName;
	private String authToken;
	private String centralAdmin;
	private String kycStatus;
	private String bitcoinCashWalletAddress;
	private String bitcoinWalletAddress;
	private Double bitcoinWalletBalance;
	private Double bitcoinCashWalletBalance;
	private String referralLink;
	private String promoFlag;
	
    
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
	public String getCentralAdmin() {
		return centralAdmin;
	}
	public void setCentralAdmin(String centralAdmin) {
		this.centralAdmin = centralAdmin;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
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
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getIsdCode() {
		return isdCode;
	}
	public void setIsdCode(String isdCode) {
		this.isdCode = isdCode;
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getWalletAddress() {
		return walletAddress;
	}
	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}
	public Double getWalletBalance() {
		return walletBalance;
	}
	public void setWalletBalance(Double walletBalance) {
		this.walletBalance = walletBalance;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getTokenAddress() {
		return tokenAddress;
	}
	public void setTokenAddress(String tokenAddress) {
		this.tokenAddress = tokenAddress;
	}
	public String getSecuredKey() {
		return securedKey;
	}
	public void setSecuredKey(String securedKey) {
		this.securedKey = securedKey;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getCoinRate() {
		return coinRate;
	}
	public void setCoinRate(BigDecimal coinRate) {
		this.coinRate = coinRate;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getKycStatus() {
		return kycStatus;
	}
	public void setKycStatus(String kycStatus) {
		this.kycStatus = kycStatus;
	}
	public String getReferralLink() {
		return referralLink;
	}
	public void setReferralLink(String referralLink) {
		this.referralLink = referralLink;
	}
	public String getPromoFlag() {
		return promoFlag;
	}
	public void setPromoFlag(String promoFlag) {
		this.promoFlag = promoFlag;
	}

}
