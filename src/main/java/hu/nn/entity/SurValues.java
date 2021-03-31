package hu.nn.entity;

import java.io.Serializable;
import java.math.BigDecimal;

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
@Table(name = "SurValues")
@Cacheable
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="export-import")
@DynamicInsert
@DynamicUpdate
@Getter @Setter
@ToString
@EqualsAndHashCode
public class SurValues implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
	private Integer id;
	@Column(name = "Chdrnum", nullable = false)
	private String chdrnum;
	@Column(name = "Survalue", nullable = false)
	private BigDecimal survalue;
	@Column(name = "Company", nullable = false)
	private String company;
	@Column(name = "currency", nullable = true)
	private String Currency;
	@Column(name = "ValidDate", nullable = true)
	private String validDate;

}