package com.remco.ico.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "register_info")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class RegisterInfo{
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "email_id")
	private String emailId;
	
	@Column(name = "password")
	@NotNull
	private String password;

	@Column(name = "createdDate")
	private Date createdDate;
	
	@Column(name = "wallet_address")
//	@NotNull
	private String walletAddress;
	
	@Column(name = "wallet_password")
	//@NotNull
	private String walletPassword;
	
	@Column(name = "mint_password")
	//@NotNull
	private String mintPassword;
	
	@Column(name = "wallet_file")
//	@NotNull
	private String walletFile;
	
	@Column(name = "mobile_no")
	private String mobileNo;
	
	@Column(name = "user_type")
	@NotNull
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int userType;

	@Column(name = "active")
	private Integer active;
	
	@Column(name="secured_key")
	private Integer securedKey;

	@Column(name="key_date")
	private Date keyDate;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "zipcode")
	private String zipcode;
	
	@Column(name = "country")
	private String country;
	
	@Column(name="kyc_status")
	@NotNull
	private String kycStatus;
	
	@Column(name = "referral_id")
	private Integer referralId;
	
	@Column(name="twofa_status")
	private Integer twofaStatus;
	
	@Column(name="ip_address")
	private String ipAddress;
	
	@Column(name = "promo_flag")
	private String promoFlag;
	
	@Column(name = "refer_site")
	private String referSite;
	
	@Column(name = "email_status", columnDefinition = "bigint(20) default 0")
	private int emailStatus;

	public int getEmailStatus() {
		return emailStatus;
	}

	public void setEmailStatus(int emailStatus) {
		this.emailStatus = emailStatus;
	}

	public Integer getReferralId() {
		return referralId;
	}

	public void setReferralId(Integer referralId) {
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
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

	public String getMintPassword() {
		return mintPassword;
	}

	public void setMintPassword(String mintPassword) {
		this.mintPassword = mintPassword;
	}

	public String getWalletFile() {
		return walletFile;
	}

	public void setWalletFile(String walletFile) {
		this.walletFile = walletFile;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public Integer getSecuredKey() {
		return securedKey;
	}

	public void setSecuredKey(Integer securedKey) {
		this.securedKey = securedKey;
	}

	public Date getKeyDate() {
		return keyDate;
	}

	public void setKeyDate(Date keyDate) {
		this.keyDate = keyDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getKycStatus() {
		return kycStatus;
	}

	public void setKycStatus(String kycStatus) {
		this.kycStatus = kycStatus;
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

	public String getPromoFlag() {
		return promoFlag;
	}

	public void setPromoFlag(String promoFlag) {
		this.promoFlag = promoFlag;
	}

	public String getReferSite() {
		return referSite;
	}

	public void setReferSite(String referSite) {
		this.referSite = referSite;
	}

}
