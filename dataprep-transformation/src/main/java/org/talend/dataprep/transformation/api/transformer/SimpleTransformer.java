package org.talend.dataprep.transformation.api.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.transformation.exception.Messages;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
@Scope("request")
class SimpleTransformer implements Transformer {

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    private final Consumer<DataSetRow> action;

    SimpleTransformer(Consumer<DataSetRow> action) {
        this.action = action;
    }

    @Override
    public void transform(InputStream input, OutputStream output) {
        try {
            if (input == null) {
                throw new IllegalArgumentException("Input cannot be null.");
            }
            if (output == null) {
                throw new IllegalArgumentException("Output cannot be null.");
            }
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createJsonParser(input);
            JsonToken nextToken = parser.nextToken();
            StateContext context = new StateContext();
            output.write('{');
            while (nextToken != null) { // EOF
                State state = context.getState();
                state.process(parser, nextToken, output, context);
                nextToken = parser.nextToken();
            }
            output.write('}');
            output.flush();
        } catch (IOException e) {
            throw Exceptions.User(Messages.UNABLE_TO_PARSE_JSON, e);
        }
    }

    class StateContext {

        private State current = new Selector();

        private List<ColumnMetadata> columns = Collections.emptyList();

        State getState() {
            return current;
        }

        public void setCurrent(State current) {
            this.current = current;
        }

        public void setColumns(List<ColumnMetadata> columns) {
            this.columns = columns;
        }

        public List<ColumnMetadata> getColumns() {
            return columns;
        }
    }

    interface State {

        void process(JsonParser parser, JsonToken nextToken, OutputStream output, StateContext context);
    }

    class Selector implements State {

        @Override
        public void process(JsonParser parser, JsonToken nextToken, OutputStream output, StateContext context) {
            try {
                if (nextToken == JsonToken.FIELD_NAME) {
                    // Parse records in stream
                    if ("columns".equals(parser.getText())) {
                        context.setCurrent(new Column());
                        // TODO Skip with nullState?
                    } else if ("records".equals(parser.getText())) { //$NON-NLS-1$
                        if (!context.getColumns().isEmpty()) {
                            output.write(',');
                        }
                        output.write("\"records\":[".getBytes()); //$NON-NLS-1$
                        context.setCurrent(new Records());
                        // Parsing array of records in "records" field
                        if (parser.nextToken() != JsonToken.START_ARRAY) {
                            throw new IllegalArgumentException("Incorrect record stream (expected an array).");
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to select next state. ", e);
            }
        }
    }

    class Column implements State {

        private final JsonGenerator generator;

        private final StringWriter content = new StringWriter();

        public Column() {
            try {
                JsonFactory factory = new JsonFactory();
                generator = factory.createJsonGenerator(content);
            } catch (IOException e) {
                throw new RuntimeException("Unable to create JSON Output", e);
            }
        }

        @Override
        public void process(JsonParser parser, JsonToken nextToken, OutputStream output, StateContext context) {
            try {
                // TODO nextToken == JsonToken.VALUE_EMBEDDED_OBJECT
                if (nextToken == JsonToken.END_OBJECT) {
                    generator.writeEndObject();
                } else if (nextToken == JsonToken.FIELD_NAME) {
                    generator.writeFieldName(parser.getText());
                } else if (nextToken == JsonToken.START_ARRAY) {
                    generator.writeStartArray();
                } else if (nextToken == JsonToken.START_OBJECT) {
                    generator.writeStartObject();
                } else if (nextToken == JsonToken.VALUE_FALSE) {
                    generator.writeBoolean(false);
                } else if (nextToken == JsonToken.VALUE_TRUE) {
                    generator.writeBoolean(true);
                } else if (nextToken == JsonToken.VALUE_NUMBER_FLOAT) {
                    generator.writeNumber(parser.getNumberValue().floatValue());
                } else if (nextToken == JsonToken.VALUE_NUMBER_INT) {
                    generator.writeNumber(parser.getNumberValue().intValue());
                } else if (nextToken == JsonToken.VALUE_STRING) {
                    generator.writeString(parser.getText());
                } else if (nextToken == JsonToken.END_ARRAY) {
                    generator.writeEndArray();
                    generator.flush();
                    // End of columns, parse stored information for action execution
                    ObjectReader columnReader = builder.build().reader(ColumnMetadata.class);
                    MappingIterator<ColumnMetadata> columns = columnReader.<ColumnMetadata>readValues(content.toString());
                    context.setColumns(columns.readAll());
                    // TODO Temporary: actions may transform columns, for now just print them as is
                    output.write("\"columns\":".getBytes());
                    ObjectWriter writer = builder.build().writer();
                    com.fasterxml.jackson.core.JsonFactory factory = writer.getFactory();
                    factory.disable(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
                    factory.disable(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET);
                    writer.without(SerializationFeature.CLOSE_CLOSEABLE).writeValue(output, context.getColumns());
                    // Finished columns, exit this state
                    context.setCurrent(new Selector());
                } else if (nextToken == JsonToken.END_OBJECT) {
                    generator.writeEndObject();
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to parse columns information.", e);
            }
        }
    }

    class Records implements State {

        private final DataSetRow row = new DataSetRow();

        private String currentFieldName = null;

        private boolean firstRow = true;

        public void process(JsonParser parser, JsonToken nextToken, OutputStream output, StateContext context) {
            try {
                if (nextToken == JsonToken.END_ARRAY) {
                    // End of records, end JSON output
                    output.write("]".getBytes()); //$NON-NLS-1$
                    output.flush();
                    context.setCurrent(new Selector()); // Finished records, exit this state
                } else if (nextToken == JsonToken.START_OBJECT) {
                    if (!firstRow) {
                        output.write(',');
                    } else {
                        firstRow = false;
                    }
                } else if (nextToken == JsonToken.FIELD_NAME) {
                    currentFieldName = parser.getText(); // Column name
                } else if (nextToken == JsonToken.VALUE_STRING) {
                    row.set(currentFieldName, parser.getText()); // Value
                } else if (nextToken == JsonToken.END_OBJECT) {
                    action.accept(row);
                    if (row.isDeleted()) {
                        firstRow = true;
                    } else {
                        row.writeTo(output);
                    }
                    row.clear(); // Clear values (allow to safely reuse DataSetRow instance)
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to process records.", e);
            }
        }
    }
}
