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
@Table(name = "ReservedTokenInfo")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ReservedTokenInfo {
	
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "reserve_key")
	@NotNull
	private String reserveKey;

	@Column(name = "reserved_value")
	@NotNull
	private Double reservedValue;
	
	@Column(name = "reserved_sold")
	@NotNull
	private Double reservedSold;
	
	@Column(name = "reserved_avail")
	@NotNull
	private Double reservedAvail;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getReserveKey() {
		return reserveKey;
	}

	public void setReserveKey(String reserveKey) {
		this.reserveKey = reserveKey;
	}

	public Double getReservedValue() {
		return reservedValue;
	}

	public void setReservedValue(Double reservedValue) {
		this.reservedValue = reservedValue;
	}

	public Double getReservedSold() {
		return reservedSold;
	}

	public void setReservedSold(Double reservedSold) {
		this.reservedSold = reservedSold;
	}

	public Double getReservedAvail() {
		return reservedAvail;
	}

	public void setReservedAvail(Double reservedAvail) {
		this.reservedAvail = reservedAvail;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
