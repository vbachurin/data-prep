package org.talend.dataprep.exception;

import java.io.*;
import java.util.Arrays;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * Unit test for the TDPException
 * 
 * @see TDPException
 */
public class TDPExceptionTest {

    /**
     * @see TDPException#writeTo(Writer)
     */
    @Test
    public void shouldBeWrittenEntirely() throws Exception {

        TDPException exception = new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, new NullPointerException("root cause"),
                TDPExceptionContext.build().put("key 1", "Value 1").put("key 2", 123)
                        .put("key 3", Arrays.asList(true, false, true)));

        String expected = read(TDPExceptionTest.class.getResourceAsStream("expected-exception.json"));

        StringWriter writer = new StringWriter();
        exception.writeTo(writer);
        JSONAssert.assertEquals(expected, writer.toString(), false);
    }

    /**
     * Return the given inputstream as a String.
     * 
     * @param input the input stream to read.
     * @return the given inputstream content.
     * @throws IOException if an error occurred.
     */
    private String read(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        return content.toString();
    }
}
