package org.talend.dataprep.test;

import org.apache.commons.io.IOUtils;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import java.io.InputStream;

import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class SameJSONFile {
    /**
     * Utilities method to assert that an expected json contained in a file matches a result.
     *
     * @param stream A stream that contains the expected json.
     * @return a SameJSONAs to use like in assertThat(contentAsString, sameJSONAsFile("t-shirt_100.csv.expected.json"));
     */
    public static SameJSONAs<? super String> sameJSONAsFile(InputStream stream) throws Exception {
        return sameJSONAs(IOUtils.toString(stream)).allowingExtraUnexpectedFields().allowingAnyArrayOrdering();
    }
}
