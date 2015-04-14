package org.talend.dataprep.schema;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;

public class XlsUtils {

    protected static String getCellValueAsString(Cell cell) {
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
            // TODO configurable??
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                return sdf.format(cell.getDateCellValue());
            }
            return String.valueOf(cell.getNumericCellValue());
        case Cell.CELL_TYPE_STRING:
            return StringUtils.trim(cell.getStringCellValue());
        default:
            return "Unknown Cell Type: " + cell.getCellType();
        }

    }

}
