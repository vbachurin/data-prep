package org.talend.dataprep.transformation.api.action.metadata.datablending;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

/**
 * Unit test for the Lookup action.
 * 
 * @see Lookup
 */
public class LookupTest {

    /** The action to test. */
    private Lookup action;

    private Map<String, String> parameters;

    @Before
    public void init() throws IOException {
        action = new Lookup();
        parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                this.getClass().getResourceAsStream("lookupAction.json"));
    }

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

}