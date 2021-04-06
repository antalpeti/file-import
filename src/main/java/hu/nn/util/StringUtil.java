package hu.nn.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class StringUtil {

    public static String getSubstring(String value, int startIndex, int length) {
        log.info("getSubstring called. value: {}, startIndex: {}, length: {}", value, startIndex, length);
        return value.length() >= startIndex + length ? value.substring(startIndex, startIndex + length) : null;
    }

}
