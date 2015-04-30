package org.talend.dataprep.exception;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

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
    public void shouldBeWrittenEntirely() {

        TDPException exception = new TDPException(
                CommonErrorCodes.UNEXPECTED_EXCEPTION,
                new NullPointerException("root cause"),
                TDPExceptionContext.build()
                    .put("key 1", "Value 1")
                    .put("key 2", 123)
                    .put("key 3", Arrays.asList(true, false, true))
                );

        String expected = "{\"code\":\"TDP_ALL_UNEXPECTED_EXCEPTION\",\"message\":\"UNEXPECTED_EXCEPTION\",\"cause\":\"root cause\",\"key 3\":\"[true, false, true]\",\"key 2\":\"123\",\"key 1\":\"Value 1\"}";

        StringWriter writer = new StringWriter();
        exception.writeTo(writer);
        Assert.assertEquals(expected, writer.toString());
    }

}
