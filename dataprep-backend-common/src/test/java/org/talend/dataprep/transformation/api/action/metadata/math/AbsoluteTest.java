// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.transformation.api.action.metadata.math;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.DataSetRowAction;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.category.ActionCategory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test for the absolute actions.
 *
 * @see AbsoluteFloat
 * @see AbsoluteFloat
 */
public class AbsoluteTest {

    private static final String FLOAT_COLUMN = "float_column"; //$NON-NLS-1$

    private static final String INT_COLUMN = "int_column"; //$NON-NLS-1$

    private AbsoluteFloat absFloatAction;

    private AbsoluteInt absIntAction;

    private DataSetRowAction absFloatConsumer;

    private DataSetRowAction absIntConsumer;

    /**
     * Default empty constructor.
     */
    public AbsoluteTest() throws IOException {

        absFloatAction = new AbsoluteFloat();
        String floatAction = IOUtils.toString(AbsoluteTest.class.getResourceAsStream("absoluteFloatAction.json")); //$NON-NLS-1$

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = floatAction.trim();
        JsonNode node = mapper.readTree(content);

        Map<String, String> parameters = absFloatAction.parseParameters(node.get("actions").get(0).get("parameters").fields());//$NON-NLS-1$//$NON-NLS-2$
        final Action action = absFloatAction.create(parameters);
        absFloatConsumer = action.getRowAction();

        absIntAction = new AbsoluteInt();
        String intAction = IOUtils.toString(AbsoluteTest.class.getResourceAsStream("absoluteIntAction.json")); //$NON-NLS-1$

        content = intAction.trim();
        node = mapper.readTree(content);

        Map<String, String> parameters2 = absIntAction.parseParameters(node.get("actions").get(0).get("parameters").fields());//$NON-NLS-1$//$NON-NLS-2$
        absIntConsumer = absIntAction.create(parameters2).getRowAction();
    }

    @Test
    public void testAdaptFloat() throws Exception {
        assertThat(absFloatAction.adapt(null), is(absFloatAction));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(absFloatAction.adapt(column), is(absFloatAction));
    }

    @Test
    public void testAdaptInt() throws Exception {
        assertThat(absIntAction.adapt(null), is(absIntAction));
        ColumnMetadata column = column().name("myColumn").id(0).type(Type.STRING).build();
        assertThat(absIntAction.adapt(column), is(absIntAction));
    }

    @Test
    public void testCategory() throws Exception {
        assertThat(absIntAction.getCategory(), is(ActionCategory.MATH.getDisplayName()));
        assertThat(absFloatAction.getCategory(), is(ActionCategory.MATH.getDisplayName()));
    }

    @Test
    public void testAbsoluteFloatWithPositiveFloat() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "5.42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absFloatConsumer.apply(dsr, new TransformationContext());
        assertEquals("5.42", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithPositiveFloat() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "5.42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absIntConsumer.apply(dsr, new TransformationContext());
        assertEquals("5.42", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithNegativeFloat() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "-5.42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absFloatConsumer.apply(dsr, new TransformationContext());
        assertEquals("5.42", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithNegativeFloat() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "-5.42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absIntConsumer.apply(dsr, new TransformationContext());
        assertEquals("5.42", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithPositiveInt() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absFloatConsumer.apply(dsr, new TransformationContext());
        assertEquals("42", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithPositiveInt() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absIntConsumer.apply(dsr, new TransformationContext());
        assertEquals("42", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithNegativeInt() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "-542"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absFloatConsumer.apply(dsr, new TransformationContext());
        assertEquals("542", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithNegativeInt() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "-542"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absIntConsumer.apply(dsr, new TransformationContext());
        assertEquals("542", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithNegativeZero() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "-0"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absFloatConsumer.apply(dsr, new TransformationContext());
        assertEquals("0", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithNegativeZero() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "-0"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absIntConsumer.apply(dsr, new TransformationContext());
        assertEquals("0", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithEmpty() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, ""); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absFloatConsumer.apply(dsr, new TransformationContext());
        assertEquals("", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithEmpty() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, ""); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absIntConsumer.apply(dsr, new TransformationContext());
        assertEquals("", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithNonNumeric() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "foobar"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absFloatConsumer.apply(dsr, new TransformationContext());
        assertEquals("foobar", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithNonNumeric() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "foobar"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absIntConsumer.apply(dsr, new TransformationContext());
        assertEquals("foobar", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithMissingColumn() {
        Map<String, String> values = new HashMap<>();
        values.put("wrong_column", "-12"); //$NON-NLS-1$ //$NON-NLS-2$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absFloatConsumer.apply(dsr, new TransformationContext());
        assertEquals("-12", dsr.get("wrong_column")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testAbsoluteIntWithMissingColumn() {
        Map<String, String> values = new HashMap<>();
        values.put("wrong_column", "-13"); //$NON-NLS-1$ //$NON-NLS-2$
        DataSetRow dsr = new DataSetRow(values);

        dsr = absIntConsumer.apply(dsr, new TransformationContext());
        assertEquals("-13", dsr.get("wrong_column")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void should_accept_column() {
        assertTrue(absIntAction.acceptColumn(getColumn(Type.INTEGER)));
        assertTrue(absFloatAction.acceptColumn(getColumn(Type.FLOAT)));
        assertTrue(absFloatAction.acceptColumn(getColumn(Type.DOUBLE)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(absIntAction.acceptColumn(getColumn(Type.NUMERIC)));
        assertFalse(absIntAction.acceptColumn(getColumn(Type.DOUBLE)));
        assertFalse(absIntAction.acceptColumn(getColumn(Type.FLOAT)));
        assertFalse(absIntAction.acceptColumn(getColumn(Type.STRING)));
        assertFalse(absIntAction.acceptColumn(getColumn(Type.DATE)));
        assertFalse(absIntAction.acceptColumn(getColumn(Type.BOOLEAN)));

        assertFalse(absFloatAction.acceptColumn(getColumn(Type.NUMERIC)));
        assertFalse(absFloatAction.acceptColumn(getColumn(Type.INTEGER)));
        assertFalse(absFloatAction.acceptColumn(getColumn(Type.STRING)));
        assertFalse(absFloatAction.acceptColumn(getColumn(Type.DATE)));
        assertFalse(absFloatAction.acceptColumn(getColumn(Type.BOOLEAN)));

    }
}
