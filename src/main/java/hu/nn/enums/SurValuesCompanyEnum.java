package hu.nn.enums;

import lombok.Getter;

@Getter
public enum SurValuesCompanyEnum {
    COMPANY_CURRENCY_HUF('1', "HUF"), COMPANY_CURRENCY_EUR('2', "EUR");

    private final char company;
    private final String currency;

    SurValuesCompanyEnum(char company, String currency) {
        this.company = company;
        this.currency = currency;
    }
}
