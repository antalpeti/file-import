package hu.nn.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class PolicyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Exclude
    private Integer id;
    private String chdrnum;
    private String cownnum;
    private String ownerName;
    private String lifcNum;
    private String lifcName;
    private String aracde;
    private String agntnum;
    private String mailAddress;

    private String causeOfSaveFailure;

}