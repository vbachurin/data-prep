package org.talend.dataprep.transformation.api.transformer.exporter.csv;

import au.com.bytecode.opencsv.CSVWriter;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

public class CsvWriter implements TransformerWriter {
    private final CSVWriter writer;
    private String[] columnIds;

    public CsvWriter(final OutputStream output, final char separator) {
        writer = new CSVWriter(new OutputStreamWriter(output), separator);

    }

    @Override
    public void write(final List<ColumnMetadata> columns) throws IOException {
        columnIds = columns.stream().map(ColumnMetadata::getId).toArray(String[]::new);
        writer.writeNext(columnIds);
    }

    @Override
    public void write(final DataSetRow row) throws IOException {
        final String[] csvRow = Arrays.stream(columnIds).map(row::get).toArray(String[]::new);
        writer.writeNext(csvRow);
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }
}
