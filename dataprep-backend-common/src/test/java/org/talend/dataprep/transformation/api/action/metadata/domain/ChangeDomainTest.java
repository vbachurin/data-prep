package org.talend.dataprep.transformation.api.action.metadata.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

public class ChangeDomainTest {

    @Test
    public void should_change_domain() throws Exception {
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0001", "David Bowie");
        values.put("0002", "");
        values.put("0003", "Something");

        final RowMetadata rowMetadata = new RowMetadata();
        rowMetadata.setColumns(Arrays.asList(ColumnMetadata.Builder.column() //
                .type(Type.INTEGER) //
                .computedId("0002") //
                .domain("FR_BEER") //
                .domainFrequency(1) //
                .domainLabel("French Beer") //
                .build()));

        final DataSetRow row = new DataSetRow(values);
        row.setRowMetadata(rowMetadata);

        DomainChange domainChange = new DomainChange();

        TransformationContext transformationContext = new TransformationContext();

        Map<String, String> parameters = new HashMap<>();
        parameters.put(DomainChange.NEW_DOMAIN_FREQUENCY_PARAMETER_KEY, "98");
        parameters.put(DomainChange.NEW_DOMAIN_ID_PARAMETER_KEY, "AUS_BEER");
        parameters.put(DomainChange.NEW_DOMAIN_LABEL_PARAMETER_KEY, "Aussie Beer");

        // when
        domainChange.applyOnColumn(row, transformationContext, parameters, "0002");

        // then
        Assert.assertEquals("AUS_BEER", row.getRowMetadata().getColumns().get(0).getDomain());
        Assert.assertEquals("Aussie Beer", row.getRowMetadata().getColumns().get(0).getDomainLabel());
        Assertions.assertThat(row.getRowMetadata().getColumns().get(0).getDomainFrequency()).isEqualTo(98);

        Set<String> forcedColumns = domainChange.getForcedColumns(transformationContext);

        Assertions.assertThat(forcedColumns).isNotNull().isNotEmpty().hasSize(1).contains("0002");
    }

    @Test
    public void should_not_apply() {
        DomainChange domainChange = new DomainChange();
        for (Type type : Type.values()) {

            ColumnMetadata columnMetadata = ColumnMetadata.Builder.column() //
                    .type(type) //
                    .computedId("0002") //
                    .domain("FR_BEER") //
                    .domainFrequency(1) //
                    .domainLabel("French Beer") //
                    .build();

            Assertions.assertThat(domainChange.acceptColumn(columnMetadata)).isFalse();
        }
    }
}
