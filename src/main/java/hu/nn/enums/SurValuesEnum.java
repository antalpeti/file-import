package hu.nn.enums;

import lombok.Getter;

@Getter
public enum SurValuesEnum {
    COMPANY(0, 0, 1), CHDRNUM(1, 1, 8), SURRENDERVALUE(2, 9, 15), JOB_USER(3, 24, 10), JOB_NAME(4, 34, 10), JOB_TIMESTAMP(5, 44, 26);

    private final int rowArrayIndex;
    private final int lineStartIndex;
    private final int lineLength;

    SurValuesEnum(int rowArrayIndex, int lineStartIndex, int lineLength) {
        this.rowArrayIndex = rowArrayIndex;
        this.lineStartIndex = lineStartIndex;
        this.lineLength = lineLength;
    }
}
