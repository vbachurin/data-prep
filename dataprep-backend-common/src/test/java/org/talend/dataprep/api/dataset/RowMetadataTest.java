package org.talend.dataprep.api.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.talend.dataprep.api.dataset.diff.Flag;
import org.talend.dataprep.api.type.Type;

/**
 * Unit test for the RowMetadata class.
 * 
 * @see RowMetadata
 */
public class RowMetadataTest {

    @Test
    public void no_diff() {
        // given
        List<ColumnMetadata> columns = new ArrayList<>();
        columns.add(getColumnMetadata("toto"));
        columns.add(getColumnMetadata("tata"));
        columns.add(getColumnMetadata("tutu"));
        RowMetadata rowMetadata = new RowMetadata(columns);

        // when
        RowMetadata reference = new RowMetadata(columns);
        rowMetadata.diff(reference);

        // then (make sure the diff flag is always null)
        rowMetadata.getColumns().forEach(column -> {
            assertNull(column.getDiffFlagValue());
        });
    }

    @Test
    public void should_diff_new_columns() {

        // given
        List<ColumnMetadata> columns = new ArrayList<>();
        columns.add(getColumnMetadata("toto"));
        RowMetadata reference = new RowMetadata(columns);

        // when
        List<ColumnMetadata> expected = new ArrayList<>();
        expected.add(getColumnMetadata("tata")); // new column
        expected.add(getColumnMetadata("titi")); // new column
        List<ColumnMetadata> temp = new ArrayList<>();
        temp.addAll(columns);
        temp.addAll(expected);
        RowMetadata row = new RowMetadata(temp);
        row.diff(reference);

        // then (collect the columns with the new flag only and compare them with
        // the expected result)
        List<ColumnMetadata> actual = row.getColumns().stream().filter(column -> {
            return Flag.NEW.getValue().equals(column.getDiffFlagValue());
        }).collect(Collectors.toList());

        assertEquals(expected, actual);

    }

    /**
     * @param name the column name.
     * @return a new column.
     */
    private ColumnMetadata getColumnMetadata(String name) {
        return ColumnMetadata.Builder.column().name(name).type(Type.STRING).build();
    }
}
