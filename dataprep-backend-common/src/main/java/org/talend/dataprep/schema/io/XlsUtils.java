package org.talend.dataprep.schema.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XlsUtils {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(XlsUtils.class);

    /**
     * Private constructor.
     */
    private XlsUtils() {
        // Utility class should not have a public constructor.
    }

    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return StringUtils.EMPTY;
        }
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_BLANK:
            return "";
        case Cell.CELL_TYPE_BOOLEAN:
            return cell.getBooleanCellValue() ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
        case Cell.CELL_TYPE_ERROR:
            return "Cell Error type";
        case Cell.CELL_TYPE_FORMULA:
            return cell.getCellFormula();
        case Cell.CELL_TYPE_NUMERIC:
            return getNumericValue(cell);
        case Cell.CELL_TYPE_STRING:
            return StringUtils.trim(cell.getStringCellValue());
        default:
            return "Unknown Cell Type: " + cell.getCellType();
        }
    }

    /**
     * Return the numeric value.
     *
     * @param cell the cell to extract the value from.
     * @return the numeric value from the cell.
     */
    private static String getNumericValue(Cell cell) {
        // Date is typed as numeric
        if (HSSFDateUtil.isCellDateFormatted(cell)) { // TODO configurable??
            DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            return sdf.format(cell.getDateCellValue());
        }
        // Numeric type (use data formatter to get number format right)
        DataFormatter formatter = new HSSFDataFormatter(Locale.ENGLISH);
        return formatter.formatCellValue(cell);
    }


    public static Workbook getWorkbook(InputStream stream) throws IOException {


        // Depending on the excel file used the poi object to use is different
        // so we try one (catch exception then try the other one)

        // TODO that's a pain as we have to keep this :-(
        // TODO use ByteBuffer or mark/reset the input if supported ?
        // but for some reasons new HSSFWorkbook consume part of the stream

        if (stream == null) {
            throw new IOException("cannot read null stream");
        }

        byte[] bytes = IOUtils.toByteArray(stream);

        try {
            return new XSSFWorkbook(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            LOGGER.debug("{} so try XSSFWorkbook", e);
            return new HSSFWorkbook(new ByteArrayInputStream(bytes));
        }

    }

}
