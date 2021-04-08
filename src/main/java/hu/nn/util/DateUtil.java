package hu.nn.util;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class DateUtil {

    public static final String DATE_PATTERN_8_HU = "yyyyMMdd";
    public static final String DATE_PATTERN_10_HU_HYPHEN = "yyyy-MM-dd";

    private static final String[] DATE_PATTERNS = { DATE_PATTERN_8_HU };

    public static Date parseDate(String value) {
        log.info("parseDate called. value: {}", value);
        if (value == null || value.length() == 0) {
            return null;
        }

        Date convertedValue = null;
        try {
            convertedValue = DateUtils.parseDateStrictly(value, DATE_PATTERNS);
        } catch (Exception e) {
            log.error("Error in parseDate: {}", e);
        }
        return convertedValue;
    }

}
