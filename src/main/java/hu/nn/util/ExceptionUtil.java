package hu.nn.util;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtil {

    public static final String NEWLINE = "\n";

    public static String getRootCauseMessageInOneLine(final Throwable th) {
        return ExceptionUtils.getRootCauseMessage(th).replace(NEWLINE, " ");
    }

}
