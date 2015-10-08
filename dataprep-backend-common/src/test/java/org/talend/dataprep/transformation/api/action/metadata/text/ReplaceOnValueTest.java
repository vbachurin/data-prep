// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.transformation.api.action.metadata.text;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.talend.dataprep.api.type.Type.BOOLEAN;
import static org.talend.dataprep.api.type.Type.STRING;
import static org.talend.dataprep.transformation.api.action.metadata.common.ImplicitParameters.*;
import static org.talend.dataprep.transformation.api.action.metadata.text.ReplaceOnValue.CELL_VALUE_PARAMETER;
import static org.talend.dataprep.transformation.api.action.metadata.text.ReplaceOnValue.REPLACE_VALUE_PARAMETER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Test class for Replace value action
 */
public class ReplaceOnValueTest {

    private ReplaceOnValue action = new ReplaceOnValue();

    @Test
    public void should_return_common_and_specific_parameters() {
        //when
        final List<Parameter> actionParams = action.getParameters();

        //then
        assertThat(actionParams, hasSize(5));

        final List<String> paramNames = actionParams.stream().map(Parameter::getName).collect(toList());
        assertThat(paramNames, IsIterableContainingInAnyOrder.containsInAnyOrder(
                COLUMN_ID.getKey(),
                ROW_ID.getKey(),
                SCOPE.getKey(),
                CELL_VALUE_PARAMETER,
                REPLACE_VALUE_PARAMETER));
    }

    @Test
    public void should_accept_string_typed_column() {
        //given
        final ColumnMetadata column = new ColumnMetadata();
        column.setType(STRING.toString());

        //when
        final boolean accepted = action.acceptColumn(column);

        //then
        assertThat(accepted, is(true));
    }

    @Test
    public void should_reject_non_string_typed_column() {
        //given
        final ColumnMetadata column = new ColumnMetadata();
        column.setType(BOOLEAN.toString());

        //when
        final boolean accepted = action.acceptColumn(column);

        //then
        assertThat(accepted, is(false));
    }

    @Test
    public void should_replace_the_value_that_match_on_the_specified_column() {
        //given
        final String columnId = "firstname";

        final Map<String, String> values = new HashMap<>();
        values.put(columnId, "James");
        final DataSetRow row = new DataSetRow(values);

        final Map<String,String> parameters = new HashMap<>();
        parameters.put(CELL_VALUE_PARAMETER, "James");
        parameters.put(REPLACE_VALUE_PARAMETER, "Jimmy");

        //when
        action.applyOnColumn(row, null, parameters, columnId);

        //then
        assertThat(row.get(columnId), is("Jimmy"));
    }

    @Test
    public void should_NOT_replace_the_value_that_DOESNT_match_on_the_specified_column() {
        //given
        final String columnId = "firstname";

        final Map<String, String> values = new HashMap<>();
        values.put(columnId, "Toto");
        final DataSetRow row = new DataSetRow(values);

        final Map<String,String> parameters = new HashMap<>();
        parameters.put(CELL_VALUE_PARAMETER, "James");
        parameters.put(REPLACE_VALUE_PARAMETER, "Jimmy");

        //when
        action.applyOnColumn(row, null, parameters, columnId);

        //then
        assertThat(row.get(columnId), is("Toto"));
    }

    @Test
    public void should_NOT_replace_the_value_because_the_column_DOES_NOT_exist() {
        // given
        final String columnId = "firstname";

        final Map<String, String> values = new HashMap<>();
        values.put(columnId, "Toto");
        final DataSetRow row = new DataSetRow(values);

        final Map<String, String> parameters = new HashMap<>();
        parameters.put(CELL_VALUE_PARAMETER, "James");
        parameters.put(REPLACE_VALUE_PARAMETER, "Jimmy");

        // when
        action.applyOnColumn(row, null, parameters, "no column here");

        // then
        assertThat(row.get(columnId), is("Toto"));
    }

    @Test
    public void should_replace_the_value_that_match_on_the_specified_cell() {
        //given
        final String columnId = "firstname";

        final Map<String, String> values = new HashMap<>();
        values.put(columnId, "James");
        final DataSetRow row = new DataSetRow(values);

        final Map<String,String> parameters = new HashMap<>();
        parameters.put(CELL_VALUE_PARAMETER, "James");
        parameters.put(REPLACE_VALUE_PARAMETER, "Jimmy");

        //when
        action.applyOnCell(row, null, parameters, 85L, columnId);

        //then
        assertThat(row.get(columnId), is("Jimmy"));
    }

    @Test
    public void should_NOT_replace_the_value_that_DOESNT_match_on_the_specified_cell() {
        //given
        final String columnId = "firstname";

        final Map<String, String> values = new HashMap<>();
        values.put(columnId, "Toto");
        final DataSetRow row = new DataSetRow(values);

        final Map<String,String> parameters = new HashMap<>();
        parameters.put(CELL_VALUE_PARAMETER, "James");
        parameters.put(REPLACE_VALUE_PARAMETER, "Jimmy");

        //when
        action.applyOnCell(row, null, parameters, 85L, columnId);

        //then
        assertThat(row.get(columnId), is("Toto"));
    }

    @Test
    public void should_replace_the_value_because_regexp() {
        // given
        final String columnId = "firstname";

        final Map<String, String> values = new HashMap<>();
        values.put(columnId, "password swordfish with Halle Berry");
        final DataSetRow row = new DataSetRow(values);

        final Map<String, String> parameters = new HashMap<>();
        parameters.put(CELL_VALUE_PARAMETER, ".*Halle.*");
        parameters.put(REPLACE_VALUE_PARAMETER, "replaced");

        // when
        action.applyOnCell(row, null, parameters, 85L, columnId);

        // then
        assertThat(row.get(columnId), is("replaced"));
    }


    @Test
    public void test_TDP_663() {
        // given
        final String columnId = "firstname";

        final Map<String, String> values = new HashMap<>();
        values.put(columnId, "password swordfish with Halle Berry");
        final DataSetRow row = new DataSetRow(values);

        final Map<String, String> parameters = new HashMap<>();
        parameters.put(CELL_VALUE_PARAMETER, "*");
        parameters.put(REPLACE_VALUE_PARAMETER, "replaced");

        // when
        action.applyOnCell(row, null, parameters, 85L, columnId);

        // then
        assertThat(row.get(columnId), is("password swordfish with Halle Berry"));
    }

}
