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
@Table(name = "ICOTokenInfo")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ICOTokenInfo {
	
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "ico_key")
	@NotNull
	private String icoKey;

	@Column(name = "ico_value")
	@NotNull
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Double icoValue;
	
	@Column(name = "ico_sold")
	@NotNull
	private Double icoSold;
	
	@Column(name = "ico_avail")
	@NotNull
	private Double icoAvail;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIcoKey() {
		return icoKey;
	}

	public void setIcoKey(String icoKey) {
		this.icoKey = icoKey;
	}

	public Double getIcoValue() {
		return icoValue;
	}

	public void setIcoValue(Double icoValue) {
		this.icoValue = icoValue;
	}

	public Double getIcoSold() {
		return icoSold;
	}

	public void setIcoSold(Double icoSold) {
		this.icoSold = icoSold;
	}

	public Double getIcoAvail() {
		return icoAvail;
	}

	public void setIcoAvail(Double icoAvail) {
		this.icoAvail = icoAvail;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
