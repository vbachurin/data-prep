package org.talend.dataprep.transformation.format;

import java.io.IOException;

import org.junit.Test;

/**
 * Test the CSV format.
 */
public class CSVFormatTest extends BaseFormatTest {

    @Test
    public void csv() throws IOException {
        testFormat(new CSVFormat(), "csv.json");
    }
}