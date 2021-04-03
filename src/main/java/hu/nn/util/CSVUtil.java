package hu.nn.util;

public class CSVUtil {

    public static String getElement(final String[] csvRow, int index) {
        if (csvRow.length > index + 1) {
            String csvColumn = csvRow[index];
            return Util.isNotEmpty(csvColumn) ? csvColumn.trim() : null;
        }
        return null;
    }

}
