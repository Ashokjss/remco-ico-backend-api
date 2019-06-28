package com.remco.ico.utils;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CallbackUtil {
	
	private String message;
	private String ipn_refno;
	/*private String merchant_email;
	private String item;
	private String amount;
	private String status;*/
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getIpn_refno() {
		return ipn_refno;
	}
	public void setIpn_refno(String ipn_refno) {
		this.ipn_refno = ipn_refno;
	}
	/*public String getMerchant_email() {
		return merchant_email;
	}
	public void setMerchant_email(String merchant_email) {
		this.merchant_email = merchant_email;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}*/
	
	

}
