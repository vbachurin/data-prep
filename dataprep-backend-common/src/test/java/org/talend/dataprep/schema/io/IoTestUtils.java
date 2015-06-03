package org.talend.dataprep.schema.io;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.schema.CSVFormatGuess;

/**
 * Utility class for the io unit tests.
 */
public class IoTestUtils {

    /**
     * Default constructor.
     */
    private IoTestUtils() {
        // private constructor for utility class
    }

    /**
     * @return a ready to use dataset.
     */
    public static DataSetMetadata getDataSetMetadata() {
        DataSetMetadata datasetMetadata = DataSetMetadata.Builder.metadata().id("123456789").build();
        datasetMetadata.getContent().addParameter(CSVFormatGuess.SEPARATOR_PARAMETER, ";");
        return datasetMetadata;
    }

    /**
     * @param id the column id.
     * @param name the column name.
     * @return the wanted column metadata.
     */
    public static ColumnMetadata getColumnMetadata(int id, String name) {
        return ColumnMetadata.Builder.column().id(id).name(name).type(Type.STRING).build();
    }

    /**
     * @param wantedColumns the wanted columns for the dataset.
     * @return a simple dataset metadata with as many columns as wanted
     */
    public static DataSetMetadata getSimpleDataSetMetadata(String... wantedColumns) {

        List<ColumnMetadata> columns = new ArrayList<>(wantedColumns.length);
        for (int i = 0; i < wantedColumns.length; i++) {
            columns.add(IoTestUtils.getColumnMetadata(i, wantedColumns[i]));
        }

        DataSetMetadata datasetMetadata = getDataSetMetadata();
        datasetMetadata.setRowMetadata(new RowMetadata((columns)));

        return datasetMetadata;

    }
}
