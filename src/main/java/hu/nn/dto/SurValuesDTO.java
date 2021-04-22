package hu.nn.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class SurValuesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Exclude
    private Integer id;
    private String company;
    private String chdrnum;
    private BigDecimal survalue;
    private String currency;
    private String validDate;

    private String causeOfSaveFailure;

}