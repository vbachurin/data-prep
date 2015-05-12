package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.List;

import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

/**
 * Definition of a Dataset transformer/serializer.
 */
public interface TypeTransformer {

    /**
     * Serialize and write the json parser content into the output stream
     *
     * @param input the dataset in json format ready to be read by a json parser.
     * @param output where to write the transformed dataset in json format.
     * @param indexes The records indexes to transform.
     * @param actions the actions to perform to achieve the transformation.
     * @param preview true if in preview mode.
     */
    void process(JsonParser input, JsonGenerator output, List<Integer> indexes, boolean preview, ParsedActions... actions);

    /**
     * Write objects array to output stream
     * 
     * @param generator - the output stream
     * @param objects - the objects to write
     * @throws java.io.IOException
     */
    default void write(final JsonGenerator generator, final List<?> objects) throws IOException {
        generator.writeStartArray();
        objects.stream().forEach((obj) -> {
            try {
                generator.writeObject(obj);
            } catch (IOException e) {
                throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
            }
        });
        generator.writeEndArray();
    }
}
