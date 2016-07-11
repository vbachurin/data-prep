// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.transformation.actions.type;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.ActionTestWorkbench;
import org.talend.dataprep.transformation.actions.AbstractMetadataBaseTest;
import org.talend.dataprep.transformation.actions.column.TypeChange;
import org.talend.dataprep.transformation.actions.common.ImplicitParameters;

public class ChangeTypeTest extends AbstractMetadataBaseTest {

    @Autowired
    private TypeChange typeChange;

    @Test
    public void should_change_type() throws Exception {
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0000", "David Bowie");
        values.put("0001", "");
        values.put("0002", "Something");

        final DataSetRow row = new DataSetRow(values);
        final RowMetadata rowMetadata = row.getRowMetadata();
        rowMetadata.getById("0001").setType(Type.INTEGER.getName());
        rowMetadata.getById("0001").setDomain("FR_BEER");
        rowMetadata.getById("0001").setDomainFrequency(1);
        rowMetadata.getById("0001").setDomainLabel("French Beer");

        Map<String, String> parameters = new HashMap<>();
        parameters.put(TypeChange.NEW_TYPE_PARAMETER_KEY, "STRING");
        parameters.put(ImplicitParameters.SCOPE.getKey().toLowerCase(), "column");
        parameters.put(ImplicitParameters.COLUMN_ID.getKey().toLowerCase(), "0001");

        // when
        ActionTestWorkbench.test(row, actionRegistry, factory.create(typeChange, parameters));

        // then
        final ColumnMetadata columnMetadata = row.getRowMetadata().getById("0001");
        Assertions.assertThat(columnMetadata.getDomain()).isEmpty();
        Assertions.assertThat(columnMetadata.getDomainLabel()).isEmpty();
        Assertions.assertThat(columnMetadata.getDomainFrequency()).isEqualTo(0);

        Assertions.assertThat(columnMetadata.isTypeForced()).isTrue();

        // Check for TDP-838:
        Assertions.assertThat(columnMetadata.isDomainForced()).isTrue();
    }

    @Test
    public void should_not_apply() {

        for (Type type : Type.values()) {

            ColumnMetadata columnMetadata = ColumnMetadata.Builder.column() //
                    .type(type) //
                    .computedId("0002") //
                    .domain("FR_BEER") //
                    .domainFrequency(1) //
                    .domainLabel("French Beer") //
                    .build();

            Assertions.assertThat(typeChange.acceptColumn(columnMetadata)).isTrue();
        }
    }
}
