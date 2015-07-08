package org.talend.dataprep.transformation.api.transformer.configuration;

import java.util.List;

import org.talend.dataprep.transformation.api.action.context.TransformationContext;

public class PreviewConfiguration extends Configuration {

    /** Indexes of rows (used in diff). */
    private final List<Integer> indexes;

    /** List of transformation context, one per action. */
    private TransformationContext[] contexts = new TransformationContext[2];

    private String referenceActions;

    public PreviewConfiguration(Configuration configuration, List<Integer> indexes) {
        super(configuration.input(), configuration.output(), configuration.format(), configuration.getActions(), configuration
                .getArguments());
        this.indexes = indexes;
    }

    public TransformationContext[] getContexts() {
        return contexts;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }

    public String getReferenceActions() {
        return referenceActions;
    }

    /**
     * Builder pattern used to simplify code writing.
     */
    public static class Builder {

        /** Indexes of rows. */
        private List<Integer> indexes;

        public Builder indexes(List<Integer> indexes) {
            this.indexes = indexes;
            return this;
        }

    }
}
