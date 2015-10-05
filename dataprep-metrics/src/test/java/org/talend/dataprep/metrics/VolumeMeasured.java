package org.talend.dataprep.metrics;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class VolumeMeasured {

    @VolumeMetered
    public void run(InputStream stream) {
        try {
            IOUtils.toString(stream); // Ignore result, purpose of test is to consume stream.
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @VolumeMetered
    public void run(Part part) {
        try {
            IOUtils.toString(part.getInputStream()); // Ignore result, purpose of test is to consume stream.
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
