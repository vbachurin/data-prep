package org.talend.dataprep.metrics;

import java.io.IOException;
import java.io.InputStream;

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
}
