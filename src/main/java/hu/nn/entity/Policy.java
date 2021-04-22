package hu.nn.entity;

import java.io.Serializable;

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
@Table(name = "POLICY")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "policyRegion")
@DynamicInsert
@DynamicUpdate
@Data
public class Policy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ID", columnDefinition = "int", unique = true, nullable = false)
    private Integer id;
    @Column(name = "Chdrnum", columnDefinition = "char(8)", nullable = false)
    private String chdrnum;
    @Column(name = "Cownnum", columnDefinition = "char(8)", nullable = false)
    private String cownnum;
    @Column(name = "OwnerName", columnDefinition = "nvarchar(50)", nullable = true)
    private String ownerName;
    @Column(name = "LifcNum", columnDefinition = "char(8)", nullable = true)
    private String lifcNum;
    @Column(name = "LifcName", columnDefinition = "nvarchar(50)", nullable = true)
    private String lifcName;
    @Column(name = "Aracde", columnDefinition = "char(3)", nullable = true)
    private String aracde;
    @Column(name = "Agntnum", columnDefinition = "char(5)", nullable = true)
    private String agntnum;
    @Column(name = "MailAddress", columnDefinition = "nvarchar(50)", nullable = true)
    private String mailAddress;

}