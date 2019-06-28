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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "transactionHistory")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionHistory implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "from_address")
	@NotNull
	private String fromAddress;

	@Column(name = "to_address")
	@NotNull
	private String toAddress;

	@Column(name = "amount")
	@NotNull
	private BigDecimal amount;

	@Column(name = "transfer_Date")
	private Date transferDate;
	
	@Column(name = "token")
	@NotNull
	private Double token;
	
	@Column(name = "payment_mode")
	private String paymentMode;
	
	@Column(name = "status")
	//@NotNull
	private String status;
	
	@Column(name = "token_status")
	//@NotNull
	private String tokenStatus;
	
	@Column(name = "email_id")
	private String emailId;
	

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Date getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Double getToken() {
		return token;
	}

	public void setToken(Double token) {
		this.token = token;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTokenStatus() {
		return tokenStatus;
	}

	public void setTokenStatus(String tokenStatus) {
		this.tokenStatus = tokenStatus;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
