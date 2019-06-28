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
@Table(name = "kyc_info")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KycInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "first_name")
	@NotNull
	private String firstName;
	
	@Column(name = "mid_name")
	private String middleName;
	
	@Column(name = "last_name")
	@NotNull
	private String lastName;
	
	@Column(name="email_id")
	@NotNull
	private String emailId;
	
	@Column(name = "identity_type")
	@NotNull
	private String identityType;
	
	@Column(name = "identity_no")
	@NotNull
	private String identityNo;
	
	@Column(name="country_of_issue")
	@NotNull
	private String countryOfIssue;
	
	@Column(name = "date_of_issue")
	@NotNull
	private Date dateOfIssue;
	
	@Column(name = "date_of_expiry")
	@NotNull
	private Date dateOfExpiry;
	
	@Column(name = "res_state")
	@NotNull
	private String resState;
	
	@Column(name = "res_country")
	@NotNull
	private String resCountry;
	
	@Column(name = "sale_type")
	@NotNull
	private String saleType;
	
	@Column(name = "inv_entity_type")
	private String invEntityType;
	
	@Column(name = "entity_name")
	private String entityName;
	
	@Column(name = "how_accredited")
	private String howAccredited;
	
	@Column(name = "inv_amount")
	private String invAmount;
	
	@Column(name="is_vesting")
	private String isVesting;
	
	@Column(name="school_name")
	private String schoolName;
	
	@Column(name="id_path")
	@NotNull
	private String idPath;
	
	@Column(name="kyc_status")
	@NotNull
	private String kycStatus;
	
	@Column(name="reason_for_reject")
	private String reasonForRejection;

	public String getReasonForRejection() {
		return reasonForRejection;
	}

	public void setReasonForRejection(String reasonForRejection) {
		this.reasonForRejection = reasonForRejection;
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

	public String getIdentityType() {
		return identityType;
	}

	public void setIdentityType(String identityType) {
		this.identityType = identityType;
	}

	public String getIdentityNo() {
		return identityNo;
	}

	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}

	public String getCountryOfIssue() {
		return countryOfIssue;
	}

	public void setCountryOfIssue(String countryOfIssue) {
		this.countryOfIssue = countryOfIssue;
	}

	public Date getDateOfIssue() {
		return dateOfIssue;
	}

	public void setDateOfIssue(Date dateOfIssue) {
		this.dateOfIssue = dateOfIssue;
	}

	public Date getDateOfExpiry() {
		return dateOfExpiry;
	}

	public void setDateOfExpiry(Date dateOfExpiry) {
		this.dateOfExpiry = dateOfExpiry;
	}

	public String getResState() {
		return resState;
	}

	public void setResState(String resState) {
		this.resState = resState;
	}

	public String getResCountry() {
		return resCountry;
	}

	public void setResCountry(String resCountry) {
		this.resCountry = resCountry;
	}

	public String getSaleType() {
		return saleType;
	}

	public void setSaleType(String saleType) {
		this.saleType = saleType;
	}

	public String getInvEntityType() {
		return invEntityType;
	}

	public void setInvEntityType(String invEntityType) {
		this.invEntityType = invEntityType;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getHowAccredited() {
		return howAccredited;
	}

	public void setHowAccredited(String howAccredited) {
		this.howAccredited = howAccredited;
	}

	public String getInvAmount() {
		return invAmount;
	}

	public void setInvAmount(String invAmount) {
		this.invAmount = invAmount;
	}

	public String getIsVesting() {
		return isVesting;
	}

	public void setIsVesting(String isVesting) {
		this.isVesting = isVesting;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getKycStatus() {
		return kycStatus;
	}

	public void setKycStatus(String kycStatus) {
		this.kycStatus = kycStatus;
	}

	public String getIdPath() {
		return idPath;
	}

	public void setIdPath(String idPath) {
		this.idPath = idPath;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	

}
