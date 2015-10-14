package org.talend.dataprep.transformation.format;

import java.io.IOException;

import org.junit.Test;

/**
 * Test the CSV format.
 */
public class XlsFormatTest extends BaseFormatTest {

    @Test
    public void xls() throws IOException {
        testFormat(new XlsFormat(), "xls.json");
    }
}