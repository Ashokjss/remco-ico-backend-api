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
@Table(name = "UserReferralInfo")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserReferralInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "user_email")
	@NotNull
	private String userEmail;

	@Column(name = "newuser_email")
	@NotNull
	private String newuserEmail;
	
	@Column(name = "referred_date")
	@NotNull
	private Date referredDate;
	
	@Column(name = "referral_count")
	@NotNull
	private Integer referralCount;
	
	@Column(name = "registered_date")
	private Date registeredDate;
	
	@Column(name = "status")
	@NotNull
	private Integer status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getNewuserEmail() {
		return newuserEmail;
	}

	public void setNewuserEmail(String newuserEmail) {
		this.newuserEmail = newuserEmail;
	}

	public Date getReferredDate() {
		return referredDate;
	}

	public void setReferredDate(Date referredDate) {
		this.referredDate = referredDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getReferralCount() {
		return referralCount;
	}

	public void setReferralCount(Integer referralCount) {
		this.referralCount = referralCount;
	}

	public Date getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}
	
	

}
