package org.talend.dataprep.transformation.api.action;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.metadata.TextClustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.talend.dataprep.transformation.api.action.metadata.SingleColumnAction.COLUMN_NAME_PARAMETER_NAME;

public class TextClusteringTest {
    private TextClustering textClustering = new TextClustering();

    @Test
    public void create_should_build_textclustering_consumer() {
        //given
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(COLUMN_NAME_PARAMETER_NAME, "uglystate");
        parameters.put("T@T@", "Tata");
        parameters.put("TaaTa", "Tata");
        parameters.put("Toto", "Tata");

        final Consumer<DataSetRow> consumer = textClustering.create(parameters);

        final List<DataSetRow> rows = new ArrayList<>();
        rows.add(createRow("uglystate", "T@T@"));
        rows.add(createRow("uglystate", "TaaTa"));
        rows.add(createRow("uglystate", "Toto"));
        rows.add(createRow("uglystate", "Tata"));

        //when
        rows.stream().forEach((row) -> consumer.accept(row));

        //then
        rows.stream()
                .map((row) -> row.get("uglystate"))
                .forEach((uglystate) -> Assertions.assertThat(uglystate).isEqualTo("Tata"));
    }

    @Test
    public void create_result_should_not_change_unmatched_value() {
        //given
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(COLUMN_NAME_PARAMETER_NAME, "uglystate");
        parameters.put("T@T@", "Tata");
        parameters.put("TaaTa", "Tata");
        parameters.put("Toto", "Tata");

        final Consumer<DataSetRow> consumer = textClustering.create(parameters);

        final List<DataSetRow> rows = new ArrayList<>();
        rows.add(createRow("uglystate", "T@T@1"));
        rows.add(createRow("uglystate", "TaaTa1"));
        rows.add(createRow("uglystate", "Toto1"));
        rows.add(createRow("uglystate", "Tata1"));

        //when
        rows.stream().forEach((row) -> consumer.accept(row));

        //then
        rows.stream()
                .map((row) -> row.get("uglystate"))
                .forEach((uglystate) -> Assertions.assertThat(uglystate).isNotEqualTo("Tata"));
    }

    private DataSetRow createRow(final String key, final String value) {
        final DataSetRow row = new DataSetRow();
        row.set(key, value);

        return row;
    }
}
