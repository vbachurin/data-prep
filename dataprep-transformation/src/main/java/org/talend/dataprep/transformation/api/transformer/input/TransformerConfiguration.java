package org.talend.dataprep.transformation.api.transformer.input;

import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.function.Consumer;

import org.talend.dataprep.transformation.api.transformer.TransformerWriter;

import com.fasterxml.jackson.core.JsonParser;

/**
 * Full configuration for a transformation.
 */
public class TransformerConfiguration {

    /** The dataset input to transform. */
    private final JsonParser input;

    /** Where to write the transformed content. */
    private final TransformerWriter output;

    /** Indexes of rows (used in diff). */
    private final List<Integer> indexes;

    /** True if in preview mode. */
    private final boolean preview;

    /** The list of actions to perform ordered by type. */
    private final Map<Class, List<Consumer>> actions;

    /**
     * Constructor for the transformer configuration.
     * 
     * @param input the json parser
     * @param output the writer plugged to the output stream to write into
     * @param indexes The records indexes to transform.
     * @param preview preview mode
     * @param actions the actions by type
     */
    private TransformerConfiguration(final JsonParser input, final TransformerWriter output, final List<Integer> indexes,
            final boolean preview, final Map<Class, List<Consumer>> actions) {
        this.input = input;
        this.output = output;
        this.indexes = indexes;
        this.preview = preview;
        this.actions = actions;
    }

    /**
     * @return the dataset to transform as json parser.
     */
    public JsonParser getInput() {
        return input;
    }

    /**
     * @return the writer where to write the transform dataset.
     */
    public TransformerWriter getOutput() {
        return output;
    }

    /**
     * @return the row indexes.
     */
    public List<Integer> getIndexes() {
        return indexes;
    }

    /**
     * @return true if in preview mode.
     */
    public boolean isPreview() {
        return preview;
    }

    /**
     * Return the actions to perform for the given class.
     *
     * @param targetClass the class to look the actions for.
     * @param <T> the class to look the actions for.
     * @return the actions to perform for the given class.
     */
    public <T> List<Consumer<T>> getActions(final Class<T> targetClass) {
        final List<Consumer> genericActions = actions.get(targetClass);
        if (genericActions == null) {
            return Collections.emptyList();
        }

        return genericActions.stream().map(consumer -> (Consumer<T>) consumer).collect(toList());
    }

    /**
     * @return a TransformerConfiguration builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder pattern used to simplify code writing.
     */
    public static class Builder {

        /** The dataset input to transform. */
        private JsonParser input;

        /** Where to write the transformed content. */
        private TransformerWriter output;

        /** Indexes of rows (used in diff). */
        private List<Integer> indexes;

        /** True if in preview mode. */
        private boolean preview;

        /** The list of actions to perform ordered by type. */
        private Map<Class, List<Consumer>> actions = new HashMap<>(2);

        /**
         * @param input the dataset input to set.
         * @return the builder to chain calls.
         */
        public Builder input(final JsonParser input) {
            this.input = input;
            return this;
        }

        /**
         * @param output where to write the transformed dataset.
         * @return the builder to chain calls.
         */
        public Builder output(final TransformerWriter output) {
            this.output = output;
            return this;
        }

        /**
         * @param indexes the indexes to set.
         * @return the builder to chain calls.
         */
        public Builder indexes(final List<Integer> indexes) {
            if (this.indexes == null) {
                this.indexes = new ArrayList<>(indexes.size());
            }
            this.indexes.addAll(indexes);
            return this;
        }

        /**
         * @param preview the preview flag to set.
         * @return the builder to chain calls.
         */
        public Builder preview(final boolean preview) {
            this.preview = preview;
            return this;
        }

        /**
         * Set the actions to perform for a given class.
         *
         * @param targetClass the class for the given actions.
         * @param actionsToAdd the actions to perform for the given class.
         * @param <T> the class for the given actions.
         * @return the builder to chain calls.
         */
        public <T> Builder actions(final Class<T> targetClass, final Consumer<T> actionsToAdd) {
            List<Consumer> existingActions = this.actions.get(targetClass);
            if (existingActions == null) {
                existingActions = new ArrayList<>(2);
                this.actions.put(targetClass, existingActions);
            }
            existingActions.add(actionsToAdd);
            return this;
        }

        /**
         * @return a new TransformerConfiguration from the builder setup.
         */
        public TransformerConfiguration build() {
            return new TransformerConfiguration(input, output, indexes, preview, actions);
        }

    }
}
