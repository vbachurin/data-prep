package org.talend.dataprep.transformation.api.transformer.exporter.xls;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;

public class XlsWriter implements TransformerWriter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OutputStream outputStream;

    private final Workbook workbook;

    private final Sheet sheet;

    private int rowIdx = 0;

    private List<ColumnMetadata> columnMetadatas;

    public XlsWriter(final OutputStream output) {
        this.outputStream = output;

        this.workbook = new HSSFWorkbook();
        // TODO sheet name as an option?
        this.sheet = this.workbook.createSheet("sheet1");
    }

    @Override
    public void write(RowMetadata columns) throws IOException {
        logger.debug("write RowMetadata: {}", columns);
        if (columns.getColumns().isEmpty()) {
            return;
        }

        this.columnMetadatas = columns.getColumns();

        CreationHelper createHelper = this.workbook.getCreationHelper();

        // writing headers so first row
        Row headerRow = this.sheet.createRow(rowIdx);
        rowIdx++;

        int cellIdx = 0;

        for (ColumnMetadata columnMetadata : columns.getColumns()) {

            // TODO apply some formatting as it's an header cell?
            headerRow.createCell(cellIdx).setCellValue(createHelper.createRichTextString(columnMetadata.getId()));

            cellIdx++;
        }

    }

    @Override
    public void write(DataSetRow dataSetRow) throws IOException {
        logger.debug("write DataSetRow: {}", dataSetRow);
        // writing datas

        Row row = this.sheet.createRow(rowIdx);
        rowIdx++;

        int cellIdx = 0;

        for (ColumnMetadata columnMetadata : this.columnMetadatas) {

            // FIXME use constants see Type
            Cell cell = row.createCell(cellIdx);
            switch (Type.get(columnMetadata.getType()).getName()) {
            case "numeric":
            case "integer":
            case "double":
            case "float":
                cell.setCellValue(Double.valueOf(dataSetRow.get(columnMetadata.getId())));
                break;
            case "boolean":
                cell.setCellValue(Boolean.valueOf(dataSetRow.get(columnMetadata.getId())));
                break;
            // FIXME ATM we don't have any idea about the date format so this can generate exceptions
            // case "date":
            // cell.setCellValue( );
            default:
                cell.setCellValue(dataSetRow.get(columnMetadata.getId()));
            }

            cellIdx++;
        }

    }

    @Override
    public void flush() throws IOException {
        this.workbook.write(outputStream);
    }

}
