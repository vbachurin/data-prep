package org.talend.dataprep.transformation.api.action.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.IOUtils;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;

/**
 * Utility class for action metadata unit tests.
 */
public class ActionMetadataTestUtils {

    /**
     *
     * @param type the wanted column type.
     * @return a new column that matches the given type.
     */
    public static ColumnMetadata getColumn(Type type) {
        return ColumnMetadata.Builder.column().id(0).name("name").type(type).build();
    }

    /**
     * Parse the given input stream into a parameter map.
     *
     * @param action the action to parse the parameters for.
     * @param input the parameters input stream.
     * @return the parsed parameters.
     * @throws IOException if an error occurs.
     */
    public static Map<String, String> parseParameters(ActionMetadata action, InputStream input) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        mapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
        JsonNode node = mapper.readTree(input);
        return action.parseParameters(node.get("actions").get(0).get("parameters").fields());
    }
}
