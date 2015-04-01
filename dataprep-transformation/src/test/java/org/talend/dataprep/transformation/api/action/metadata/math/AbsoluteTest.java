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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.Application;
import org.talend.dataprep.transformation.TransformationServiceTests;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest
@WebAppConfiguration
public class AbsoluteTest {

    private static final String FLOAT_COLUMN = "float_column"; //$NON-NLS-1$

    private static final String INT_COLUMN = "int_column"; //$NON-NLS-1$

    @Autowired
    private AbsoluteFloat absFloatAction;

    @Autowired
    private AbsoluteInt absIntAction;

    private Consumer<DataSetRow> absFloatConsumer;

    private Consumer<DataSetRow> absIntConsumer;

    @Before
    public void setUp() throws IOException {
        String floatAction = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("absoluteFloatAction.json")); //$NON-NLS-1$

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = floatAction.trim();
        JsonNode node = mapper.readTree(content);

        absFloatConsumer = absFloatAction.create(node.get("actions").get(0).get("parameters").getFields()); //$NON-NLS-1$//$NON-NLS-2$

        String intAction = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("absoluteIntAction.json")); //$NON-NLS-1$

        content = intAction.trim();
        node = mapper.readTree(content);

        absIntConsumer = absIntAction.create(node.get("actions").get(0).get("parameters").getFields()); //$NON-NLS-1$ //$NON-NLS-2$

    }

    @Test
    public void testAbsoluteFloatWithPositiveFloat() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "5.42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absFloatConsumer.accept(dsr);
        assertEquals("5.42", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithPositiveFloat() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "5.42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absIntConsumer.accept(dsr);
        assertEquals("5.42", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithNegativeFloat() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "-5.42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absFloatConsumer.accept(dsr);
        assertEquals("5.42", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithNegativeFloat() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "-5.42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absIntConsumer.accept(dsr);
        assertEquals("5.42", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithPositiveInt() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absFloatConsumer.accept(dsr);
        assertEquals("42", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithPositiveInt() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "42"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absIntConsumer.accept(dsr);
        assertEquals("42", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithNegativeInt() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "-542"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absFloatConsumer.accept(dsr);
        assertEquals("542", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithNegativeInt() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "-542"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absIntConsumer.accept(dsr);
        assertEquals("542", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithNegativeZero() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "-0"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absFloatConsumer.accept(dsr);
        assertEquals("0", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithNegativeZero() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "-0"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absIntConsumer.accept(dsr);
        assertEquals("0", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithEmty() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, ""); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absFloatConsumer.accept(dsr);
        assertEquals("", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithEMpty() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, ""); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absIntConsumer.accept(dsr);
        assertEquals("", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithNonNumeric() {
        Map<String, String> values = new HashMap<>();
        values.put(FLOAT_COLUMN, "foobar"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absFloatConsumer.accept(dsr);
        assertEquals("foobar", dsr.get(FLOAT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteIntWithNonNumeric() {
        Map<String, String> values = new HashMap<>();
        values.put(INT_COLUMN, "foobar"); //$NON-NLS-1$
        DataSetRow dsr = new DataSetRow(values);

        absIntConsumer.accept(dsr);
        assertEquals("foobar", dsr.get(INT_COLUMN)); //$NON-NLS-1$
    }

    @Test
    public void testAbsoluteFloatWithMissingColumn() {
        Map<String, String> values = new HashMap<>();
        values.put("wrong_column", "-12"); //$NON-NLS-1$ //$NON-NLS-2$
        DataSetRow dsr = new DataSetRow(values);

        absFloatConsumer.accept(dsr);
        assertEquals("-12", dsr.get("wrong_column")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testAbsoluteIntWithMissingColumn() {
        Map<String, String> values = new HashMap<>();
        values.put("wrong_column", "-13"); //$NON-NLS-1$ //$NON-NLS-2$
        DataSetRow dsr = new DataSetRow(values);

        absIntConsumer.accept(dsr);
        assertEquals("-13", dsr.get("wrong_column")); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
