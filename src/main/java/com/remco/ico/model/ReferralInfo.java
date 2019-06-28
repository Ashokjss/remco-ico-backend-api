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
@Table(name = "ReferralInfo")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class ReferralInfo {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "emailId")
	@NotNull
	private String emailId;
	
	@Column(name = "referral_tokens")
	@NotNull
	private Double referralTokens;
	
	@Column(name = "referral_id")
	@NotNull
	private Integer referralId;
	
	@Column(name = "referral_percentage")
	@NotNull
	private Double referralPercentage;
	
	@Column(name = "referred_purchase_date")
	private Date referralPurchaseDate;

	@Column(name = "status")
	private Integer status;
	
	@Column(name = "referral_emailId")
	private String referralEmailId;
	
	@Column(name = "referral_purchased_tokens")
	private Double referralPurchasedTokens;
	
	
	public Date getReferralPurchaseDate() {
		return referralPurchaseDate;
	}

	public void setReferralPurchaseDate(Date referralPurchaseDate) {
		this.referralPurchaseDate = referralPurchaseDate;
	}

	public String getReferralEmailId() {
		return referralEmailId;
	}

	public void setReferralEmailId(String referralEmailId) {
		this.referralEmailId = referralEmailId;
	}

	public Double getReferralPurchasedTokens() {
		return referralPurchasedTokens;
	}

	public void setReferralPurchasedTokens(Double referralPurchasedTokens) {
		this.referralPurchasedTokens = referralPurchasedTokens;
	}

	public Double getReferralPercentage() {
		return referralPercentage;
	}

	public void setReferralPercentage(Double referralPercentage) {
		this.referralPercentage = referralPercentage;
	}

	public Integer getReferralId() {
		return referralId;
	}

	public void setReferralId(Integer referralId) {
		this.referralId = referralId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public Double getReferralTokens() {
		return referralTokens;
	}

	public void setReferralTokens(Double referralTokens) {
		this.referralTokens = referralTokens;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
