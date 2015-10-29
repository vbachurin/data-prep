package org.talend.dataprep.transformation.api.action.metadata.datablending;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;
import org.talend.dataprep.transformation.api.action.metadata.common.DataSetAction;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Unit test for the Lookup action.
 * 
 * @see Lookup
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Lookup.class)
@WebIntegrationTest("server.port:0")
@ComponentScan(basePackages = "org.talend.dataprep")
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
        final Optional<String> adaptedName = parameters.stream().filter(p -> StringUtils.equals(p.getName(), "lookup_ds_name"))
                .map(p -> p.getDefault()).findFirst();
        assertEquals("great dataset", adaptedName.get());
        final Optional<String> adaptedId = parameters.stream().filter(p -> StringUtils.equals(p.getName(), "lookup_ds_id"))
                .map(p -> p.getDefault()).findFirst();
        assertEquals("ds#123", adaptedId.get());
        final Optional<String> adaptedUrl = parameters.stream().filter(p -> StringUtils.equals(p.getName(), "lookup_ds_url"))
                .map(p -> p.getDefault()).findFirst();
        assertEquals(dsUrl, adaptedUrl.get());
    }

    @Test
    public void shouldMerge() throws IOException {
        parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                this.getClass().getResourceAsStream("lookupAction.json"));

        parameters.put(Lookup.PARAMETERS.lookup_ds_url.name(), "http://localhost:" + port + "/lookup/test");
        final DataSetAction lookup = (DataSetAction) this.action.create(parameters);

        Map<String, String> values = new HashMap<>();
        values.put("0000", "toto");
        values.put("0001", "04/25/1999");
        values.put("0002", "tata");
        DataSetRow row = new DataSetRow(values);
        lookup.applyOnDataSet(row, new TransformationContext(), parameters);
    }
}