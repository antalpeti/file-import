package hu.nn.util;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtil {

    public static final String LINE_FEED = "\n";

    public static String getRootCauseMessageWithoutLineFeed(final Throwable th) {
        return ExceptionUtils.getRootCauseMessage(th).replace(LINE_FEED, " ");
    }

}
