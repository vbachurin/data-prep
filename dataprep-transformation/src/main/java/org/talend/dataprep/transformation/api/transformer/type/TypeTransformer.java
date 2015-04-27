package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.transformation.exception.TransformationMessages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

/**
 * Definition of a Dataset content Type (column, records, ...) transformer/serializer.
 */
public interface TypeTransformer<T> {

    /**
     * Serialize and write the json parser content into the output stream
     *
     * @param parser - the json parser
     * @param generator - the json generator plugged to the output stream to write into
     * @param action - the action to execute on TypeState object
     * @param preview - preview mode
     */
    void process(JsonParser parser, JsonGenerator generator, Consumer<T> action, boolean preview);

    /**
     * Write objects array to output stream
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
                throw Exceptions.Internal(TransformationMessages.UNABLE_TO_PARSE_JSON, e);
            }
        });
        generator.writeEndArray();
    }
}
