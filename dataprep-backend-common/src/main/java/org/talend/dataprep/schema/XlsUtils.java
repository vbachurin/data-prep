package org.talend.dataprep.schema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XlsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XlsUtils.class);

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

    protected static Workbook getWorkbook(InputStream stream) throws IOException {

        // TODO that's a pain as we have to keep this :-(
        // but for some reasons new HSSFWorkbook consume part of the stream
        byte[] bytes = IOUtils.toByteArray(stream);

        try {
            return new HSSFWorkbook(new ByteArrayInputStream(bytes));
        } catch (OfficeXmlFileException e) {
            LOGGER.debug("OfficeXmlFileException so try XSSFWorkbook");
            return new XSSFWorkbook(new ByteArrayInputStream(bytes));
        }
    }

}
