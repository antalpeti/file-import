package hu.nn.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class OutPayHeaderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Exclude
    private Integer outpayHeaderId;
    private String clntnum;
    private String chdrnum;
    private String letterType;
    private Date printDate;
    private String dataId;
    private String clntName;
    private String clntAddress;
    private Date regDate;
    private BigDecimal benPercent;
    private String role1;
    private String role2;
    private String cownNum;
    private String cownName;
    private String notice01;
    private String notice02;
    private String notice03;
    private String notice04;
    private String notice05;
    private String notice06;
    private String claimId;
    private Date tp2processDate;

    private String causeOfSaveFailure;

}
