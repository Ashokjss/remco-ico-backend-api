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
	@Table(name = "token_discount_info")
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public class TokenDiscountInfo implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Integer id;
		
		@Column(name = "created_date")
		private Date createdDate;
			
		@Column(name = "token_name")
		private String tokenName;
		
		@Column(name = "vesting_disc")
		private BigDecimal vestingDisc;
		
		@Column(name = "referral_disc")
		private BigDecimal referralDisc;
		
		@Column(name = "ngn_value")
		private BigDecimal ngnValue;


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

		public String getTokenName() {
			return tokenName;
		}

		public void setTokenName(String tokenName) {
			this.tokenName = tokenName;
		}

		public BigDecimal getVestingDisc() {
			return vestingDisc;
		}

		public void setVestingDisc(BigDecimal vestingDisc) {
			this.vestingDisc = vestingDisc;
		}

		public BigDecimal getReferralDisc() {
			return referralDisc;
		}

		public void setReferralDisc(BigDecimal referralDisc) {
			this.referralDisc = referralDisc;
		}

		public static long getSerialversionuid() {
			return serialVersionUID;
		}

		public BigDecimal getNgnValue() {
			return ngnValue;
		}

		public void setNgnValue(BigDecimal ngnValue) {
			this.ngnValue = ngnValue;
		}

		
		
}
