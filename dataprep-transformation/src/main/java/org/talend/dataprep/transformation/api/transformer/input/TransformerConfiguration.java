package org.talend.dataprep.transformation.api.transformer.input;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.talend.dataprep.transformation.api.transformer.TransformerWriter;

import com.fasterxml.jackson.core.JsonParser;

public class TransformerConfiguration {

    private final JsonParser parser;

    private final TransformerWriter writer;

    private final List<Integer> indexes;

    private final boolean preview;

    private final Map<Class, List<Consumer>> actions;

    /**
     * Constructor
     * @param parser - the json parser
     * @param writer - the writer plugged to the output stream to write into
     * @param indexes - The records indexes to transform.
     * @param preview - preview mode
     * @param actions - the actions by type
     */
    public TransformerConfiguration(final JsonParser parser, final TransformerWriter writer, final List<Integer> indexes,
            final boolean preview, final Map<Class, List<Consumer>> actions) {
        this.parser = parser;
        this.writer = writer;
        this.indexes = indexes;
        this.preview = preview;
        this.actions = actions;
    }

    public JsonParser getParser() {
        return parser;
    }

    public TransformerWriter getWriter() {
        return writer;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }

    public boolean isPreview() {
        return preview;
    }

    public <T> List<Consumer<T>> getActions(final Class<T> targetClass) {
        final List<Consumer> genericActions = actions.get(targetClass);
        if(genericActions == null) {
            return null;
        }

        return genericActions.stream().map(consumer -> (Consumer<T>) consumer).collect(toList());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private JsonParser parser;

        private TransformerWriter writer;

        private List<Integer> indexes;

        private boolean preview;

        private Map<Class, List<Consumer>> actions = new HashMap<>();

        public Builder parser(final JsonParser parser) {
            this.parser = parser;
            return this;
        }

        public Builder writer(final TransformerWriter writer) {
            this.writer = writer;
            return this;
        }

        public Builder indexes(final List<Integer> indexes) {
            if (this.indexes == null) {
                this.indexes = new ArrayList<>(indexes.size());
            }
            this.indexes.addAll(indexes);
            return this;
        }

        public Builder preview(final boolean preview) {
            this.preview = preview;
            return this;
        }

        public <T> Builder actions(final Class<T> targetClass, final Consumer<T> actionsToAdd) {
            List<Consumer> existingActions = this.actions.get(targetClass);
            if (existingActions == null) {
                existingActions = new ArrayList<>(1);
                this.actions.put(targetClass, existingActions);
            }
            existingActions.add(actionsToAdd);
            return this;
        }

        public TransformerConfiguration build() {
            return new TransformerConfiguration(parser, writer, indexes, preview, actions);
        }

    }
}
