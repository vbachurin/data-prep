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
import static org.talend.dataprep.transformation.api.action.metadata.text.ReplaceOnValue.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.ActionTestWorkbench;
import org.talend.dataprep.transformation.api.action.context.ActionContext;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.common.ImplicitParameters;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Test class for Replace value action
 */
public class ReplaceOnValueTest {

    private ReplaceOnValue action = new ReplaceOnValue();

    private ActionContext buildPatternActionContext(String regex, String replacement, boolean replace) {
        ActionContext context = new ActionContext(new TransformationContext());
        Map<String, String> parameters = new HashMap<>();
        parameters.put(ReplaceOnValue.CELL_VALUE_PARAMETER, regex);
        parameters.put(ReplaceOnValue.REPLACE_VALUE_PARAMETER, replacement);
        parameters.put(ReplaceOnValue.REPLACE_ENTIRE_CELL_PARAMETER, String.valueOf(replace));
        context.setParameters(parameters);
        action.compile(context);
        return context;
    }

    @Test
    public void should_return_common_and_specific_parameters() {
        // when
        final List<Parameter> actionParams = action.getParameters();

        // then
        assertThat(actionParams, hasSize(7));

        final List<String> paramNames = actionParams.stream().map(Parameter::getName).collect(toList());
        assertThat(paramNames, IsIterableContainingInAnyOrder.containsInAnyOrder(COLUMN_ID.getKey(), //
                ROW_ID.getKey(), //
                SCOPE.getKey(), //
                FILTER.getKey(), //
                CELL_VALUE_PARAMETER, //
                REPLACE_VALUE_PARAMETER, //
                REPLACE_ENTIRE_CELL_PARAMETER));
    }

    @Test
    public void should_accept_string_typed_column() {
        // given
        final ColumnMetadata column = new ColumnMetadata();
        column.setType(STRING.toString());

        // when
        final boolean accepted = action.acceptColumn(column);

        // then
        assertThat(accepted, is(true));
    }

    @Test
    public void should_reject_non_string_typed_column() {
        // given
        final ColumnMetadata column = new ColumnMetadata();
        column.setType(BOOLEAN.toString());

        // when
        final boolean accepted = action.acceptColumn(column);

        // then
        assertThat(accepted, is(false));
    }

    @Test
    public void should_replace_the_value_that_match_on_the_specified_column_entire() {
        // given
        final String columnId = "firstname";

        final Map<String, String> values = new HashMap<>();
        values.put(columnId, "James Hetfield");
        final DataSetRow row = new DataSetRow(values);

        final Map<String, String> parameters = new HashMap<>();
        parameters.put(CELL_VALUE_PARAMETER, "James");
        parameters.put(REPLACE_VALUE_PARAMETER, "Jimmy");
        parameters.put(REPLACE_ENTIRE_CELL_PARAMETER, "true");
        parameters.put(ImplicitParameters.SCOPE.getKey().toLowerCase(), "column");
        parameters.put(ImplicitParameters.COLUMN_ID.getKey().toLowerCase(), columnId);

        // when
        ActionTestWorkbench.test(row, action.create(parameters).getRowAction());

        // then
        assertThat(row.get(columnId), is("Jimmy"));
    }

    @Test
    public void testComputeNewValue() {
        // Case text when matches:
        Assert.assertEquals("Bob Dylan", action.computeNewValue(buildPatternActionContext("Robert", "Bob", false), "Robert Dylan"));
        Assert.assertEquals("Bob", action.computeNewValue(buildPatternActionContext("Robert", "Bob", true), "Robert Dylan"));
        Assert.assertEquals("I listen to Bob Dylan every day", action.computeNewValue(buildPatternActionContext("Robert", "Bob", false), "I listen to Robert Dylan every day"));

        // Case text when don't match:
        Assert.assertEquals("Robert Dylan", action.computeNewValue(buildPatternActionContext("Andy", "Bob", false), "Robert Dylan"));
        Assert.assertEquals("Robert Dylan", action.computeNewValue(buildPatternActionContext("Andy", "Bob", false), "Robert Dylan"));
        Assert.assertEquals("Robert Dylan", action.computeNewValue(buildPatternActionContext("Andy", "Bob", true), "Robert Dylan"));

        // Case regexp when matches:
        Assert.assertEquals("Bob", action.computeNewValue(buildPatternActionContext("Robert.*", "Bob", false), "Robert Dylan"));
        Assert.assertEquals("Bob", action.computeNewValue(buildPatternActionContext("Robert.*", "Bob", true), "Robert Dylan"));
        Assert.assertEquals("I want to break free", action.computeNewValue(buildPatternActionContext("\\d", "to", false), "I want 2 break free"));
        Assert.assertEquals("to", action.computeNewValue(buildPatternActionContext(".*\\d.*", "to", false), "I want 2 break free"));
        Assert.assertEquals("to", action.computeNewValue(buildPatternActionContext("\\d", "to", true), "I want 2 break free"));

        // Case regexp when don't match:
        Assert.assertEquals("Robert Dylan", action.computeNewValue(buildPatternActionContext(".*Andy.*", "Bob", false), "Robert Dylan"));
        Assert.assertEquals("Robert Dylan", action.computeNewValue(buildPatternActionContext(".*Andy.*", "Bob", true), "Robert Dylan"));

        Assert.assertEquals("XXX_EN_YYY", action.computeNewValue(buildPatternActionContext("FR", "EN", false), "XXX_FR_YYY"));
        Assert.assertEquals("XXX_FR_YYY", action.computeNewValue(buildPatternActionContext("FOO", "EN", false), "XXX_FR_YYY"));
        Assert.assertEquals("EN", action.computeNewValue(buildPatternActionContext("FR", "EN", true), "XXX_FR_YYY"));

        Assert.assertEquals("XXX_EN_YYY", action.computeNewValue(buildPatternActionContext("F.", "EN", false), "XXX_FR_YYY"));
        Assert.assertEquals("XXX_FR_YYY", action.computeNewValue(buildPatternActionContext("G.", "EN", false), "XXX_FR_YYY"));
        Assert.assertEquals("EN", action.computeNewValue(buildPatternActionContext("F.", "EN", true), "XXX_FR_YYY"));

        Assert.assertEquals("XXX_EN_YYY", action.computeNewValue(buildPatternActionContext("_.*_", "_EN_", false), "XXX_David Bowie_YYY"));
        Assert.assertEquals("XXX_David 2 Bowie_YYY", action.computeNewValue(buildPatternActionContext("_[a-zA-Z]*_", "EN", false), "XXX_David 2 Bowie_YYY"));
        Assert.assertEquals("XXX_EN_YYY", action.computeNewValue(buildPatternActionContext("_[a-zA-Z0-9 ]*_", "_EN_", false), "XXX_David 2 Bowie_YYY"));

        Assert.assertEquals("XXX_YYY", action.computeNewValue(buildPatternActionContext("FR_", "", false), "XXX_FR_YYY"));

    }

    @Test
    public void should_replace_the_value_that_match_on_the_specified_column() {
        Assert.assertEquals("Jimmy Hetfield", action.computeNewValue(buildPatternActionContext("James", "Jimmy", false), "James Hetfield"));
        Assert.assertEquals("Jimmy", action.computeNewValue(buildPatternActionContext("James", "Jimmy", true), "James"));
    }

    @Test
    public void should_NOT_replace_the_value_that_DOESNT_match_on_the_specified_column() {
        Assert.assertEquals("Toto", action.computeNewValue(buildPatternActionContext("James", "Jimmy", false), "Toto"));
        Assert.assertEquals("Toto", action.computeNewValue(buildPatternActionContext("James", "Jimmy", true), "Toto"));
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
        parameters.put(REPLACE_ENTIRE_CELL_PARAMETER, "false");
        parameters.put(ImplicitParameters.SCOPE.getKey().toLowerCase(), "column");
        parameters.put(ImplicitParameters.COLUMN_ID.getKey().toLowerCase(), "no column here");

        // when
        ActionTestWorkbench.test(row, action.create(parameters).getRowAction());

        // then
        assertThat(row.get(columnId), is("Toto"));
    }

    @Test
    public void should_replace_the_value_that_match_on_the_specified_cell() {
        // given
        final String columnId = "firstname";

        final Map<String, String> values = new HashMap<>();
        values.put(columnId, "James");
        final DataSetRow row = new DataSetRow(values);
        row.setTdpId(85L);

        final Map<String, String> parameters = new HashMap<>();
        parameters.put(CELL_VALUE_PARAMETER, "James");
        parameters.put(REPLACE_VALUE_PARAMETER, "Jimmy");
        parameters.put(REPLACE_ENTIRE_CELL_PARAMETER, "false");
        parameters.put(ImplicitParameters.SCOPE.getKey().toLowerCase(), "cell");
        parameters.put(ImplicitParameters.COLUMN_ID.getKey().toLowerCase(), columnId);
        parameters.put(ImplicitParameters.ROW_ID.getKey().toLowerCase(), "85");

        // when
        ActionTestWorkbench.test(row, action.create(parameters).getRowAction());

        // then
        assertThat(row.get(columnId), is("Jimmy"));
    }

    @Test
    public void should_replace_value_based_on_regex() {
        //given
        final String from = "bridge.html?region=FR";
        final String regexp = "bridge.html[?]region=FR";
        final String to = "pont.html?region=FR";

        //when
        final String result = action.computeNewValue(buildPatternActionContext(regexp, to, false), from);

        //then
        assertThat(result, is(to));
    }

    @Test
    public void should_replace_many_values_that_match_on_the_specified_cell() {
        // given
        final String columnId = "firstname";

        final Map<String, String> values = new HashMap<>();
        values.put(columnId, "James Cleveland James");
        final DataSetRow row = new DataSetRow(values);
        row.setTdpId(85L);

        final Map<String, String> parameters = new HashMap<>();
        parameters.put(CELL_VALUE_PARAMETER, "James");
        parameters.put(REPLACE_VALUE_PARAMETER, "Jimmy");
        parameters.put(REPLACE_ENTIRE_CELL_PARAMETER, "false");
        parameters.put(ImplicitParameters.SCOPE.getKey().toLowerCase(), "cell");
        parameters.put(ImplicitParameters.COLUMN_ID.getKey().toLowerCase(), columnId);
        parameters.put(ImplicitParameters.ROW_ID.getKey().toLowerCase(), "85");

        // when
        ActionTestWorkbench.test(row, action.create(parameters).getRowAction());

        // then
        assertThat(row.get(columnId), is("Jimmy Cleveland Jimmy"));
    }

    @Test
    public void should_NOT_replace_the_value_that_DOESNT_match_on_the_specified_cell() {
        // given
        final String columnId = "firstname";

        final Map<String, String> values = new HashMap<>();
        values.put(columnId, "Toto");
        values.put(DataSetRow.TDP_ID, "85");
        final DataSetRow row = new DataSetRow(values);

        final Map<String, String> parameters = new HashMap<>();
        parameters.put(CELL_VALUE_PARAMETER, "James");
        parameters.put(REPLACE_VALUE_PARAMETER, "Jimmy");
        parameters.put(REPLACE_ENTIRE_CELL_PARAMETER, "false");
        parameters.put(ImplicitParameters.SCOPE.getKey().toLowerCase(), "cell");
        parameters.put(ImplicitParameters.COLUMN_ID.getKey().toLowerCase(), columnId);
        parameters.put(ImplicitParameters.ROW_ID.getKey().toLowerCase(), "85");

        // when
        ActionTestWorkbench.test(row, action.create(parameters).getRowAction());

        // then
        assertThat(row.get(columnId), is("Toto"));
    }

    @Test
    public void should_replace_the_value_because_regexp() {
        Assert.assertEquals("replaced", action.computeNewValue(buildPatternActionContext(".*Halle.*", "replaced", true), "password swordfish with Halle Berry"));
        Assert.assertEquals("replaced", action.computeNewValue(buildPatternActionContext(".*Halle.*", "replaced", false), "password swordfish with Halle Berry"));
    }

    @Test
    public void test_TDP_663() {
        Assert.assertEquals("password swordfish with Halle Berry", action.computeNewValue(buildPatternActionContext("*", "replaced", false), "password swordfish with Halle Berry"));
        Assert.assertEquals("password swordfish with Halle Berry", action.computeNewValue(buildPatternActionContext("*", "replaced", true), "password swordfish with Halle Berry"));
    }

    @Test
    public void test_TDP_958_emptyPattern() {
        Assert.assertEquals("password swordfish with Halle Berry", action.computeNewValue(buildPatternActionContext("", "replaced", false), "password swordfish with Halle Berry"));
        Assert.assertEquals("password swordfish with Halle Berry", action.computeNewValue(buildPatternActionContext("", "replaced", true), "password swordfish with Halle Berry"));
    }

    @Test
    public void test_TDP_958_invalidPattern() {
        Assert.assertEquals("password swordfish with Halle Berry", action.computeNewValue(buildPatternActionContext("^(", "replaced", false), "password swordfish with Halle Berry"));
        Assert.assertEquals("password swordfish with Halle Berry", action.computeNewValue(buildPatternActionContext("^(", "replaced", true), "password swordfish with Halle Berry"));
    }

}
