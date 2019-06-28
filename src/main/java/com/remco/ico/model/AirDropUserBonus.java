package com.remco.ico.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "AirDropUserBonus")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AirDropUserBonus implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "full_name")
	private String fullName;

	@Column(name = "email_id")
	private String emailId;
	
	@Column(name = "wallet_address")
	private String walletAddress;
	
	@Column(name = "token_transfer_status")
	private Integer tokenTransferStatus;
	
	@Column(name = "earned_token")
	private BigDecimal earnedToken;
	
	@Column(name = "allocation_type")
	private String allocationType;
	
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "transferred_date")
	private Date transferredDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public Integer getTokenTransferStatus() {
		return tokenTransferStatus;
	}

	public void setTokenTransferStatus(Integer tokenTransferStatus) {
		this.tokenTransferStatus = tokenTransferStatus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public BigDecimal getEarnedToken() {
		return earnedToken;
	}

	public void setEarnedToken(BigDecimal earnedToken) {
		this.earnedToken = earnedToken;
	}

	public String getAllocationType() {
		return allocationType;
	}

	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getTransferredDate() {
		return transferredDate;
	}

	public void setTransferredDate(Date transferredDate) {
		this.transferredDate = transferredDate;
	}



}
