package org.talend.dataprep.metrics;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

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
