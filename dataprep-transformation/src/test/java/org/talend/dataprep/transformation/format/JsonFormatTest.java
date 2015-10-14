package org.talend.dataprep.transformation.format;

import java.io.IOException;

import org.junit.Test;

/**
 * Test the CSV format.
 */
public class JsonFormatTest extends BaseFormatTest {

    @Test
    public void json() throws IOException {
        testFormat(new JsonFormat(), "json.json");
    }
}