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
package org.talend.dataprep.transformation.api.action.metadata;

import static org.junit.Assert.*;

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

/**
 * Test class for DeleteEmpty action. Creates one consumer, and test it.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest
@WebAppConfiguration
public class ProperCaseTest {

    private Consumer<DataSetRow> consumer;

    @Autowired
    private ProperCase           action;

    @Before
    public void setUp() throws IOException {
        String actions = IOUtils.toString(TransformationServiceTests.class.getResourceAsStream("properCaseAction.json"));
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = actions.trim();
        JsonNode node = mapper.readTree(content);
        consumer = action.create(node.get("actions").get(0).get("parameters").getFields());
    }

    @Test
    public void test1() {
        Map<String, String> values = new HashMap<>();
        values.put("band", "the beatles");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);

        assertEquals("The Beatles", dsr.get("band"));
    }

    @Test
    public void test2() {
        Map<String, String> values = new HashMap<>();
        values.put("band", "THE BEATLES");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);

        assertEquals("The Beatles", dsr.get("band"));
    }

    @Test
    public void test3() {
        Map<String, String> values = new HashMap<>();
        values.put("bando", "the beatles");
        DataSetRow dsr = new DataSetRow(values);

        consumer.accept(dsr);

        assertEquals("the beatles", dsr.get("bando"));
    }
}
