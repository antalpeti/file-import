package hu.nn.dto;

import java.io.Serializable;

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