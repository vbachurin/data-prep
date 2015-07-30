package org.talend.dataprep.transformation.api.action.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        String actions = IOUtils.toString(input);
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = actions.trim();
        JsonNode node = mapper.readTree(content);
        return action.parseParameters(node.get("actions").get(0).get("parameters").fields());
    }
}
