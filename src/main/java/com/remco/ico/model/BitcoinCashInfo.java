package com.remco.ico.model;

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
@Table(name = "BitcoinCashInfo")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class BitcoinCashInfo {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "emailId")
	@NotNull
	private String emailId;
	
	@Column(name = "access_tokens")
	@NotNull
	private String accessTokens;
	
	@Column(name = "wallet_id")
	private String walletId;
	
	@Column(name = "bch_wallet_address")
	private String bchWalletAddress;
	
	@Column(name = "bch_wallet_password")
	private String bchWalletPassword;
	
	@Column(name = "token_type")
	@NotNull
	private String tokenType;
	
	@Column(name = "label")
	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public String getAccessTokens() {
		return accessTokens;
	}

	public void setAccessTokens(String accessTokens) {
		this.accessTokens = accessTokens;
	}

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public String getBchWalletAddress() {
		return bchWalletAddress;
	}

	public void setBchWalletAddress(String bchWalletAddress) {
		this.bchWalletAddress = bchWalletAddress;
	}

	public String getBchWalletPassword() {
		return bchWalletPassword;
	}

	public void setBchWalletPassword(String bchWalletPassword) {
		this.bchWalletPassword = bchWalletPassword;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
