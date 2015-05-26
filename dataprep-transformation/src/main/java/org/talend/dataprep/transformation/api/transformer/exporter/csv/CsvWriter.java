package org.talend.dataprep.transformation.api.transformer.exporter.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;

import au.com.bytecode.opencsv.CSVWriter;

public class CsvWriter implements TransformerWriter {

    private final CSVWriter writer;

    private String[] columnIds;

    public CsvWriter(final OutputStream output, final char separator) {
        writer = new CSVWriter(new OutputStreamWriter(output), separator);
    }

    @Override
    public void write(final RowMetadata rowMetadata) throws IOException {
        columnIds = rowMetadata.getColumns().stream().map(ColumnMetadata::getId).toArray(String[]::new);
        writer.writeNext(columnIds);
    }

    @Override
    public void write(final DataSetRow row) throws IOException {
        if (columnIds == null) {
            throw new UnsupportedOperationException("Write columns should be called before to init column list");
        }
        final String[] csvRow = Arrays.stream(columnIds).map(row::get).toArray(String[]::new);
        writer.writeNext(csvRow);
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }
}
