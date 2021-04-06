package hu.nn.util;

import java.util.Arrays;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class CSVUtil {

    public static String getElement(final String[] csvRow, int index) {
        log.info("getElement called. csvRow: {}, index: {}", csvRow, index);
        if (csvRow.length > index) {
            String csvColumn = csvRow[index];
            return Util.isNotEmpty(csvColumn) ? csvColumn.trim() : null;
        }
        return null;
    }

    public static boolean isEmpty(final String[] csvRow) {
        log.info("isEmpty called. Arrays.asList(csvRow): {}", Arrays.asList(csvRow));
        boolean empty = true;
        if (csvRow != null) {
            for (String csvColumn : csvRow) {
                if (Util.isNotEmpty(csvColumn)) {
                    empty = false;
                    break;
                }
            }
        }
        return empty;
    }

    public static boolean isNotEmpty(final String[] csvRow) {
        log.info("isNotEmpty called.");
        return !CSVUtil.isEmpty(csvRow);
    }

}
