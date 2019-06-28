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
@Table(name = "token_rate_info")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TokenRateInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "created_date")
	private Date createdDate;
		
	@Column(name = "sale_type")
	private String saletype;
	
	@Column(name = "token_value")
	private BigDecimal tokenValue;
	
	@Column(name = "discount")
	private BigDecimal discount;
	
	@Column(name = "min_contribute")
	private BigDecimal minContribute;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getSaletype() {
		return saletype;
	}

	public void setSaletype(String saletype) {
		this.saletype = saletype;
	}

	public BigDecimal getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(BigDecimal tokenValue) {
		this.tokenValue = tokenValue;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getMinContribute() {
		return minContribute;
	}

	public void setMinContribute(BigDecimal minContribute) {
		this.minContribute = minContribute;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
