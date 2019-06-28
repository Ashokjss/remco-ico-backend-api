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
@Table(name = "BitcoinInfo")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class BitcoinInfo {
	
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
	
	@Column(name = "btc_wallet_address")
	private String btcWalletAddress;
	
	@Column(name = "btc_wallet_password")
	private String btcWalletPassword;
	
	@Column(name = "token_type")
	@NotNull
	private String tokenType;
	
	@Column(name = "label")
	private String label;
	

	public String getBtcWalletAddress() {
		return btcWalletAddress;
	}

	public void setBtcWalletAddress(String btcWalletAddress) {
		this.btcWalletAddress = btcWalletAddress;
	}

	public String getBtcWalletPassword() {
		return btcWalletPassword;
	}

	public void setBtcWalletPassword(String btcWalletPassword) {
		this.btcWalletPassword = btcWalletPassword;
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

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}
