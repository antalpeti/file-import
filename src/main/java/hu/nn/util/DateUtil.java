package hu.nn.util;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class DateUtil {
    private static final String DATE_PATTERN_8_HU = "yyyyMMdd";

    private static final String[] DATE_PATTERNS = { DATE_PATTERN_8_HU };

    public static Date parseDate(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }

        Date convertedValue = null;
        try {
            convertedValue = DateUtils.parseDate(value, DATE_PATTERNS);
        } catch (Exception e) {
            log.error("Error in DateUtil.parseDate: {}", e);
        }
        return convertedValue;
    }
}
