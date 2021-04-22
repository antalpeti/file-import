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

import lombok.Data;

@Entity
@Table(name = "SurValues")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "surValuesRegion")
@DynamicInsert
@DynamicUpdate
@Data
public class SurValues implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ID", columnDefinition = "int", unique = true, nullable = false)
    private Integer id;
    @Column(name = "Company", columnDefinition = "char(1)", nullable = false)
    private String company;
    @Column(name = "Chdrnum", columnDefinition = "char(8)", nullable = false)
    private String chdrnum;
    @Column(name = "Survalue", columnDefinition = "decimal(15, 2)", nullable = false)
    private BigDecimal survalue;
    @Column(name = "Currency", columnDefinition = "char(3)", nullable = true)
    private String currency;
    @Column(name = "ValidDate", columnDefinition = "char(10)", nullable = true)
    private String validDate;

}