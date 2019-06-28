package com.remco.ico.model;

import java.io.Serializable;
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
@Table(name = "purchase_token_info")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class PurchaseTokenInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "email_id")
	@NotNull
	private String emailId;
	
	@Column(name = "purchase_date")
	@NotNull
	private Date purchasedDate;
	
	@Column(name = "bch_wallet_address")
	private String bchWalletAddress;
	
	@Column(name = "btc_wallet_address")
	private String btcWalletAddress;
	
	@Column(name = "ether_wallet_address")
	private String etherWalletAddress;
	
	@Column(name = "purchase_tokens")
	@NotNull
	private Double purchaseTokens;
	
	@Column(name = "free_tokens")
	private Double freeTokens;
	
	@Column(name = "crypto_amount")
	private Double cryptoAmount;
	
	@Column(name = "type_purchase")
	@NotNull
	private String typeOfPurchase;
	
	@Column(name = "transfer_type")
	@NotNull
	private Integer transferType;
	
	@Column(name = "school_name")
	private String schoolName;
	
	@Column(name = "d.o.b")
	private Date dateOfBirth;
	
	@Column(name = "is_vesting")
	private Integer isVesting;
	
	@Column(name = "asynch_status")
	private String asynchStatus;
	
	public String getAsynchStatus() {
		return asynchStatus;
	}

	public void setAsynchStatus(String asynchStatus) {
		this.asynchStatus = asynchStatus;
	}

	public Integer getIsVesting() {
		return isVesting;
	}

	public void setIsVesting(Integer isVesting) {
		this.isVesting = isVesting;
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

	public Double getCryptoAmount() {
		return cryptoAmount;
	}

	public void setCryptoAmount(Double cryptoAmount) {
		this.cryptoAmount = cryptoAmount;
	}

	public Integer getTransferType() {
		return transferType;
	}

	public void setTransferType(Integer transferType) {
		this.transferType = transferType;
	}

	public Double getFreeTokens() {
		return freeTokens;
	}

	public void setFreeTokens(Double freeTokens) {
		this.freeTokens = freeTokens;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Date getPurchasedDate() {
		return purchasedDate;
	}

	public void setPurchasedDate(Date purchasedDate) {
		this.purchasedDate = purchasedDate;
	}

	public String getBchWalletAddress() {
		return bchWalletAddress;
	}

	public void setBchWalletAddress(String bchWalletAddress) {
		this.bchWalletAddress = bchWalletAddress;
	}

	public String getBtcWalletAddress() {
		return btcWalletAddress;
	}

	public void setBtcWalletAddress(String btcWalletAddress) {
		this.btcWalletAddress = btcWalletAddress;
	}

	public String getEtherWalletAddress() {
		return etherWalletAddress;
	}

	public void setEtherWalletAddress(String etherWalletAddress) {
		this.etherWalletAddress = etherWalletAddress;
	}

	public Double getPurchaseTokens() {
		return purchaseTokens;
	}

	public void setPurchaseTokens(Double purchaseTokens) {
		this.purchaseTokens = purchaseTokens;
	}

	public String getTypeOfPurchase() {
		return typeOfPurchase;
	}

	public void setTypeOfPurchase(String typeOfPurchase) {
		this.typeOfPurchase = typeOfPurchase;
	}
	
	
}
