package org.talend.dataprep.api.preparation;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONBlob extends Blob {

    public JSONBlob(String content) {
        super(normalize(content));
    }

    private static String normalize(String content) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tree = mapper.reader().readTree(content);
            return mapper.writer().writeValueAsString(tree);
        } catch (IOException e) {
            throw new RuntimeException("Unable to normalize.", e);
        }
    }
}
