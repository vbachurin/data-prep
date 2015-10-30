package org.talend.dataprep.transformation.api.action.metadata.datablending;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.talend.dataprep.transformation.api.action.metadata.common.ImplicitParameters.COLUMN_ID;
import static org.talend.dataprep.transformation.api.action.metadata.datablending.Lookup.PARAMETERS.LOOKUP_DS_URL;
import static org.talend.dataprep.transformation.api.action.metadata.datablending.Lookup.PARAMETERS.LOOKUP_JOIN_ON;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Unit test for the Lookup action.
 * 
 * @see Lookup
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { LookupTestApplication.class, LookupTestConfiguration.class })
@WebAppConfiguration
@IntegrationTest({ "server.port=0" })
public class LookupTest {

    @Value("${local.server.port}")
    public int port;

    /** The action to test. */
    @Autowired
    private Lookup action;

    private Map<String, String> parameters;

    @Test
    public void testAccept() throws Exception {
        assertFalse(action.acceptColumn(new ColumnMetadata()));
    }

    @Test
    public void shouldAdapt() {
        // given
        final DataSetMetadata ds = DataSetMetadata.Builder.metadata().name("great dataset").id("ds#123").build();
        String dsUrl = "http://estcequecestbientotleweekend.fr";

        // when
        action.adapt(ds, dsUrl);

        // when
        final List<Parameter> parameters = action.getParameters();
        final Optional<String> adaptedName = parameters.stream().filter(p -> StringUtils.equals(p.getName(), "LOOKUP_DS_NAME"))
                .map(p -> p.getDefault()).findFirst();
        assertEquals("great dataset", adaptedName.get());
        final Optional<String> adaptedId = parameters.stream().filter(p -> StringUtils.equals(p.getName(), "LOOKUP_DS_ID"))
                .map(p -> p.getDefault()).findFirst();
        assertEquals("ds#123", adaptedId.get());
        final Optional<String> adaptedUrl = parameters.stream().filter(p -> StringUtils.equals(p.getName(), "LOOKUP_DS_URL"))
                .map(p -> p.getDefault()).findFirst();
        assertEquals(dsUrl, adaptedUrl.get());
    }

    @Test
    public void shouldMerge() throws IOException {

        // given
        parameters = ActionMetadataTestUtils.parseParameters(this.getClass().getResourceAsStream("lookupAction.json"));
        parameters.put(LOOKUP_DS_URL.getKey(), "http://localhost:" + port + "/test/lookup/us_states");
        parameters.put(LOOKUP_JOIN_ON.getKey(), "0000");
        parameters.put(COLUMN_ID.getKey(), "0001");

        Map<String, String> values = new HashMap<>();
        values.put("0000", "Atlanta");
        values.put("0001", "GA");
        values.put("0002", "Philips Arena");
        DataSetRow row = new DataSetRow(values);

        // when
        action.applyOnDataSet(row, new TransformationContext(), parameters);

        // then
        Map<String, String> expectedValues = new HashMap<>();
        expectedValues.put("0000", "Atlanta");
        expectedValues.put("0001", "GA");
        expectedValues.put("0003", "Georgia");
        expectedValues.put("0002", "Philips Arena");
        DataSetRow expected = new DataSetRow(expectedValues);

        Assert.assertEquals(expected, row);
    }
}