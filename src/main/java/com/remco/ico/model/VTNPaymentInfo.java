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
@Table(name = "vtn_payment_info")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VTNPaymentInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "ipn_refno")
	@NotNull
	private String ipnRefno;
	
	@Column(name = "item")
	private String item;
	
	@Column(name = "amount")
	private Double amount;
	
	@Column(name = "transaction_number")
	private String transactionNumber;
	
	@Column(name = "customer_email")
	private String customerEmail;
	
	@Column(name = "converted_amount")
	private Double convertedAmount;
	
	@Column(name = "custom_remarks")
	private String customRemarks;
	
	@Column(name = "commission")
	private String commission;
	
	@Column(name = "created_date")
	private String createdDate;
	
	@Column(name = "message")
	private String message;
	
	@Column(name = "status")
	private String status;

	public String getIpnRefno() {
		return ipnRefno;
	}

	public void setIpnRefno(String ipnRefno) {
		this.ipnRefno = ipnRefno;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public Double getConvertedAmount() {
		return convertedAmount;
	}

	public void setConvertedAmount(Double convertedAmount) {
		this.convertedAmount = convertedAmount;
	}

	public String getCustomRemarks() {
		return customRemarks;
	}

	public void setCustomRemarks(String customRemarks) {
		this.customRemarks = customRemarks;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}
