package org.talend.dataprep.transformation.api.action.metadata.blending;

import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;

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
                this.getClass().getResourceAsStream("lookup.json"));
    }

    @Test
    public void testAccept() throws Exception {
        Assert.fail("test to write");
    }

}