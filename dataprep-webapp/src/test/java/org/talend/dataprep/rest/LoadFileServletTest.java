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
package org.talend.dataprep.rest;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.common.EasyFiles;

/**
 * created by stef on Dec 17, 2014 Detailled comment
 *
 */
public class LoadFileServletTest {

    @Test
    public void testBuildFullJson() throws IOException {
        String fullJson = LoadFileServlet.buildFullJson(EasyFiles.getFile("customers_2.json"));

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode oNode = (ObjectNode) mapper.readTree(fullJson);

        Assert.assertEquals(2, oNode.size());

        Assert.assertTrue(oNode.has("records"));
        Assert.assertTrue(oNode.get("records").isArray());

        Assert.assertTrue(oNode.has("columns"));
        Assert.assertTrue(oNode.get("columns").isArray());

        JsonNode firstRecord = oNode.get("records").iterator().next();

        Assert.assertEquals("88522", firstRecord.get("id").asText());
        Assert.assertEquals("Dwight", firstRecord.get("firstname").asText());
        Assert.assertEquals("Ford", firstRecord.get("lastname").asText());

        JsonNode columns = oNode.get("columns");
        Assert.assertEquals(3, columns.size());
    }

}
