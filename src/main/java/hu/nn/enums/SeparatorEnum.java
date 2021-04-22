package hu.nn.enums;

import lombok.Getter;

@Getter
public enum SeparatorEnum {
    SEMICOLON(';'), PIPE('|'), EMPTY('\0');

    private final char valueAsChar;
    private final String valueAsString;

    SeparatorEnum(char value) {
        this.valueAsChar = value;
        this.valueAsString = Character.toString(value);
    }

}
