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

/**
 * Write datasets in CSV.
 */
public class CsvWriter implements TransformerWriter {

    /** The CSV writer. */
    private final CSVWriter writer;

    /** the columns ids. */
    private String[] columnIds;

    /**
     * Constructor.
     * 
     * @param output where to write the dataset.
     * @param separator the separator to use.
     */
    public CsvWriter(final OutputStream output, final char separator) {
        writer = new CSVWriter(new OutputStreamWriter(output), separator);
    }

    /**
     * @see TransformerWriter#write(RowMetadata)
     */
    @Override
    public void write(final RowMetadata rowMetadata) throws IOException {
        // write the columns names
        String[] columnsName = rowMetadata.getColumns().stream().map(ColumnMetadata::getName).toArray(String[]::new);
        writer.writeNext(columnsName);

        // and store the columns ids for the rows.
        columnIds = rowMetadata.getColumns().stream().map(ColumnMetadata::getId).toArray(String[]::new);
    }

    /**
     * @see TransformerWriter#write(DataSetRow)
     * @throws UnsupportedOperationException if CsvWriter#write(RowMetadata) was not called before.
     */
    @Override
    public void write(final DataSetRow row) throws IOException {
        if (columnIds == null) {
            throw new UnsupportedOperationException("Write columns should be called before to init column list");
        }
        final String[] csvRow = Arrays.stream(columnIds).map(row::get).toArray(String[]::new);
        writer.writeNext(csvRow);
    }

    /**
     * @see TransformerWriter#flush()
     */
    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public boolean requireMetadataForHeader() {
        return true;
    }
}
