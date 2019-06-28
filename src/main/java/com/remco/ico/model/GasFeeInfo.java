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
@Table(name = "gas_fee")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GasFeeInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "ether")
	@NotNull
	private Double ether;
	
	@Column(name = "bitcoin")
	@NotNull
	private Double bitcoin;
	
	@Column(name = "bitcoin_cash")
	@NotNull
	private Double bitcoinCash;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getEther() {
		return ether;
	}

	public void setEther(Double ether) {
		this.ether = ether;
	}

	public Double getBitcoin() {
		return bitcoin;
	}

	public void setBitcoin(Double bitcoin) {
		this.bitcoin = bitcoin;
	}

	public Double getBitcoinCash() {
		return bitcoinCash;
	}

	public void setBitcoinCash(Double bitcoinCash) {
		this.bitcoinCash = bitcoinCash;
	}
}
