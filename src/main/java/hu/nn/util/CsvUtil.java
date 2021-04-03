package hu.nn.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CsvUtil {

    public static String getElement(final String[] csvRow, int index) {
        if (csvRow.length > index + 1) {
            String csvColumn = csvRow[index];
            return Util.isNotEmpty(csvColumn) ? csvColumn.trim() : null;
        }
        return null;
    }

}
