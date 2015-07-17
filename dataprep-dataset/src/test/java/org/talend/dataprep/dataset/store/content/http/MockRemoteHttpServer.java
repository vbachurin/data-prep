package org.talend.dataprep.dataset.store.content.http;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mock remote http server used to check the remote http dataset import.
 */
@RestController
public class MockRemoteHttpServer {

    @RequestMapping(value = "/not/so/far/away", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String getSampleRemoteFile() throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream("file.csv"));
    }

    @RequestMapping(value = "/cannot/be/reached", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String wontGetSampleRemoteFile() {
        throw new ResourceNotFoundException();
    }

}
