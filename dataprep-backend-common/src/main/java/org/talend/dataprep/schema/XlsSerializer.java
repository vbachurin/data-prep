package org.talend.dataprep.schema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;

@Component("serializer#xls")
public class XlsSerializer implements Serializer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public InputStream serialize(InputStream rawContent, DataSetMetadata metadata) {

        try {

            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(rawContent);
            StringWriter writer = new StringWriter();
            JsonGenerator generator = new JsonFactory().createJsonGenerator(writer);

            // FIXME: ATM we work with only one sheet
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);

            generator.writeStartArray();

            List<ColumnMetadata> columns = metadata.getRow().getColumns();

            for (int i = 0, size = sheet.getLastRowNum(); i < size; i++) {

                HSSFRow row = sheet.getRow(i);

                generator.writeStartObject();
                for (int j = 0; j < columns.size(); j++) {
                    ColumnMetadata columnMetadata = columns.get(j);
                    String cellValue = getCellValueAsString(row.getCell(j));
                    logger.debug("cellValue for {}/{}: {}", i, j, cellValue);
                    generator.writeStringField(columnMetadata.getId(), cellValue);
                }
                generator.writeEndObject();

            }

            generator.writeEndArray();
            generator.flush();
            return new ByteArrayInputStream(writer.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize to JSON.", e);
        }

    }

    protected String getCellValueAsString(Cell cell) {
        if (cell==null){
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
            return cell.getStringCellValue();
        default:
            return "Unknown Cell Type: " + cell.getCellType();
        }

    }
}
