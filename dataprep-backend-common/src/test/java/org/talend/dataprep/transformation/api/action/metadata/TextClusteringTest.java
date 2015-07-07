package org.talend.dataprep.transformation.api.action.metadata;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;
import static org.talend.dataprep.transformation.api.action.metadata.SingleColumnAction.COLUMN_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

public class TextClusteringTest {

    private TextClustering textClustering = new TextClustering();

    @Test
    public void create_should_build_textclustering_consumer() {
        // given
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(COLUMN_ID, "uglystate");
        parameters.put("T@T@", "Tata");
        parameters.put("TaaTa", "Tata");
        parameters.put("Toto", "Tata");

        final BiConsumer<DataSetRow, TransformationContext> consumer = textClustering.create(parameters);

        final List<DataSetRow> rows = new ArrayList<>();
        rows.add(createRow("uglystate", "T@T@"));
        rows.add(createRow("uglystate", "TaaTa"));
        rows.add(createRow("uglystate", "Toto"));
        rows.add(createRow("uglystate", "Tata"));

        // when
        rows.stream().forEach((row) -> consumer.accept(row, new TransformationContext()));

        // then
        rows.stream().map((row) -> row.get("uglystate"))
                .forEach((uglystate) -> Assertions.assertThat(uglystate).isEqualTo("Tata"));
    }

    @Test
    public void testAdapt() throws Exception {
        assertThat(textClustering.adapt(null), is(textClustering));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(textClustering.adapt(column), is(textClustering));
    }

    @Test
    public void create_result_should_not_change_unmatched_value() {
        // given
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(COLUMN_ID, "uglystate");
        parameters.put("T@T@", "Tata");
        parameters.put("TaaTa", "Tata");
        parameters.put("Toto", "Tata");

        final BiConsumer<DataSetRow, TransformationContext> consumer = textClustering.create(parameters);

        final List<DataSetRow> rows = new ArrayList<>();
        rows.add(createRow("uglystate", "T@T@1"));
        rows.add(createRow("uglystate", "TaaTa1"));
        rows.add(createRow("uglystate", "Toto1"));
        rows.add(createRow("uglystate", "Tata1"));

        // when
        rows.stream().forEach((row) -> consumer.accept(row, new TransformationContext()));

        // then
        rows.stream().map((row) -> row.get("uglystate"))
                .forEach((uglystate) -> Assertions.assertThat(uglystate).isNotEqualTo("Tata"));
    }

    private DataSetRow createRow(final String key, final String value) {
        final DataSetRow row = new DataSetRow();
        row.set(key, value);

        return row;
    }

    @Test
    public void should_accept_column() {
        assertTrue(textClustering.accept(getColumn(Type.STRING)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(textClustering.accept(getColumn(Type.NUMERIC)));
        assertFalse(textClustering.accept(getColumn(Type.DOUBLE)));
        assertFalse(textClustering.accept(getColumn(Type.FLOAT)));
        assertFalse(textClustering.accept(getColumn(Type.INTEGER)));
        assertFalse(textClustering.accept(getColumn(Type.DATE)));
        assertFalse(textClustering.accept(getColumn(Type.BOOLEAN)));
    }
}
