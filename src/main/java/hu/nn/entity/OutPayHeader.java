package hu.nn.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "OUTPAY_HEADER")
@Cacheable
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="txt")
@DynamicInsert
@DynamicUpdate
@Getter @Setter
@ToString
@EqualsAndHashCode
public class OutPayHeader implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue
    @Column(name = "Outpay_Header_ID", columnDefinition="int", nullable = false)
	private Integer outpayHeaderId;
	@Column(name = "Clntnum", columnDefinition="char(8)", nullable = false)
	private String clntnum;
	@Column(name = "Chdrnum", columnDefinition="char(8)", nullable = false)
	private String chdrnum;
	@Column(name = "LetterType", columnDefinition="char(12)", nullable = false)
	private String letterType;
	@Column(name = "PrintDate", columnDefinition="datetime", nullable = false)
	private Date printDate;
	@Column(name = "DataID", columnDefinition="char(6)", nullable = true)
	private String dataId;
	@Column(name = "ClntName", columnDefinition="nvarchar(80)", nullable = true)
	private String clntName;
	@Column(name = "ClntAddress", columnDefinition="nvarchar(80)", nullable = true)
	private String clntAddress;
	@Column(name = "RegDate", columnDefinition="datetime", nullable = true)
	private Date regDate;
	@Column(name = "BenPercent", columnDefinition="decimal(6, 2)", nullable = true)
	private BigDecimal benPercent;
	@Column(name = "Role1", columnDefinition="char(2)", nullable = true)
	private String role1;
	@Column(name = "Role2", columnDefinition="char(2)", nullable = true)
	private String role2;
	@Column(name = "CownNum", columnDefinition="char(8)", nullable = true)
	private String cownNum;
	@Column(name = "CownName", columnDefinition="nvarchar(80)", nullable = true)
	private String cownName;
	@Column(name = "notice01", columnDefinition="nvarchar(80)", nullable = true)
	private String notice01;
	@Column(name = "Notice02", columnDefinition="nvarchar(80)", nullable = true)
	private String notice02;
	@Column(name = "Notice03", columnDefinition="nvarchar(80)", nullable = true)
	private String notice03;
	@Column(name = "Notice04", columnDefinition="nvarchar(80)", nullable = true)
	private String notice04;
	@Column(name = "Notice05", columnDefinition="nvarchar(80)", nullable = true)
	private String notice05;
	@Column(name = "Notice06", columnDefinition="nvarchar(80)", nullable = true)
	private String notice06;
	@Column(name = "Claim_ID", columnDefinition="char(9)", nullable = true)
	private String claimId;
	@Column(name = "TP2ProcessDate", columnDefinition="datetime", nullable = true)
	private Date tp2processDate;

}