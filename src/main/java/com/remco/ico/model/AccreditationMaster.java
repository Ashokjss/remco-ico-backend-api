package com.remco.ico.model;

import java.io.Serializable;

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
@Table(name = "accreditation_master")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AccreditationMaster implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "accredit_id")
	@NotNull
	private String accreditId;

	@Column(name = "accredit_desc")
	@NotNull
	private String accreditDesc;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccreditId() {
		return accreditId;
	}

	public void setAccreditId(String accreditId) {
		this.accreditId = accreditId;
	}

	public String getAccreditDesc() {
		return accreditDesc;
	}

	public void setAccreditDesc(String accreditDesc) {
		this.accreditDesc = accreditDesc;
	}

}
