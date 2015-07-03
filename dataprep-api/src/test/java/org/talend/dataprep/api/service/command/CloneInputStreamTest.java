package org.talend.dataprep.api.service.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class CloneInputStreamTest {

    @Test
    public void testEmptyStream() throws Exception {
        final File tempFile = File.createTempFile("cloneInputTest", "testEmptyStream");
        tempFile.deleteOnExit();
        CloneInputStream stream = new CloneInputStream(new ByteArrayInputStream(new byte[0]), new FileOutputStream(tempFile));
        final String originalContent = IOUtils.toString(stream);
        assertThat(IOUtils.toString(new FileInputStream(tempFile)), is(originalContent));
    }

    @Test
    public void testStringStream() throws Exception {
        final File tempFile = File.createTempFile("cloneInputTest", "testStringStream");
        tempFile.deleteOnExit();
        CloneInputStream stream = new CloneInputStream(new ByteArrayInputStream("test string".getBytes()), new FileOutputStream(tempFile));
        final String originalContent = IOUtils.toString(stream);
        assertThat(IOUtils.toString(new FileInputStream(tempFile)), is(originalContent));
    }

}
