package hu.nn.dto;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class SurValuesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String company;
    private String chdrnum;
    private BigDecimal survalue;
    private String currency;
    private String validDate;

}