package hu.nn.util;

import org.springframework.util.NumberUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class NumberUtil {

    public <T extends Number> T parseNumber(String value, Class<T> targetClass) {
        log.info("parseNumber called. value: {}, targetClass: {}", value, targetClass);
        T number = null;
        try {
            number = NumberUtils.parseNumber(value, targetClass);
        } catch (Exception e) {
            log.error("Error in parseNumber: {}", e);
        }
        return number;
    }

}
