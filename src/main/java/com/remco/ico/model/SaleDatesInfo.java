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
@Table(name = "SaleDatesInfo")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SaleDatesInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "ico_key")
	@NotNull
	private String icoKey;
	
	@Column(name = "private_start_date")
	private Date privateStartDate;
	
	@Column(name = "private_end_date")
	private Date privateEndDate;
	
	@Column(name = "pre_public_start_date")
	private Date prePublicStartDate;
	
	@Column(name = "pre_public_end_date")
	private Date prePublicEndDate;
	
	@Column(name = "public_start_date")
	private Date publicStartDate;
	
	@Column(name = "public_end_date")
	private Date publicEndDate;
	
	@Column(name = "private_accreditation")
	private Integer privateAccredit;
	
	@Column(name = "presale_accreditation")
	private Integer presaleAccredit;
	
	@Column(name = "public_accreditation")
	private Integer publicAccredit;


	public String getIcoKey() {
		return icoKey;
	}

	public void setIcoKey(String icoKey) {
		this.icoKey = icoKey;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getPrivateStartDate() {
		return privateStartDate;
	}

	public void setPrivateStartDate(Date privateStartDate) {
		this.privateStartDate = privateStartDate;
	}

	public Date getPrivateEndDate() {
		return privateEndDate;
	}

	public void setPrivateEndDate(Date privateEndDate) {
		this.privateEndDate = privateEndDate;
	}

	public Date getPrePublicStartDate() {
		return prePublicStartDate;
	}

	public void setPrePublicStartDate(Date prePublicStartDate) {
		this.prePublicStartDate = prePublicStartDate;
	}

	public Date getPrePublicEndDate() {
		return prePublicEndDate;
	}

	public void setPrePublicEndDate(Date prePublicEndDate) {
		this.prePublicEndDate = prePublicEndDate;
	}

	public Date getPublicStartDate() {
		return publicStartDate;
	}

	public void setPublicStartDate(Date publicStartDate) {
		this.publicStartDate = publicStartDate;
	}

	public Date getPublicEndDate() {
		return publicEndDate;
	}

	public void setPublicEndDate(Date publicEndDate) {
		this.publicEndDate = publicEndDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getPrivateAccredit() {
		return privateAccredit;
	}

	public void setPrivateAccredit(Integer privateAccredit) {
		this.privateAccredit = privateAccredit;
	}

	public Integer getPresaleAccredit() {
		return presaleAccredit;
	}

	public void setPresaleAccredit(Integer presaleAccredit) {
		this.presaleAccredit = presaleAccredit;
	}

	public Integer getPublicAccredit() {
		return publicAccredit;
	}

	public void setPublicAccredit(Integer publicAccredit) {
		this.publicAccredit = publicAccredit;
	}

}
