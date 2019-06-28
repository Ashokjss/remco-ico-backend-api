package com.remco.ico.DTO;

public class TokenTransferDTO {

	private Double requestToken;	
	private String toAddress;
	private Integer airDropId;
	private Integer tokenTransferStatus;
	
	public Integer getTokenTransferStatus() {
		return tokenTransferStatus;
	}
	public void setTokenTransferStatus(Integer tokenTransferStatus) {
		this.tokenTransferStatus = tokenTransferStatus;
	}
	public Integer getAirDropId() {
		return airDropId;
	}
	public void setAirDropId(Integer airDropId) {
		this.airDropId = airDropId;
	}
	public Double getRequestToken() {
		return requestToken;
	}
	public void setRequestToken(Double requestToken) {
		this.requestToken = requestToken;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}



}
