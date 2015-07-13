package org.talend.dataprep.transformation.api.transformer.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PreviewConfiguration extends Configuration {

    private final String previewActions;

    /** Indexes of rows (used in diff). */
    private final List<Integer> indexes;

    /** List of transformation context, one per action. */
    private final TransformationContext context = new TransformationContext();

    protected PreviewConfiguration(Configuration configuration, String previewActions, List<Integer> indexes) {
        super(configuration.output(), configuration.format(), configuration.getActions(), configuration.getArguments(), configuration.volume());
        this.previewActions = previewActions;
        this.indexes = indexes;
    }

    public static Builder preview() {
        return new Builder();
    }

    public TransformationContext getReferenceContext() {
        return super.getTransformationContext();
    }

    public TransformationContext getPreviewContext() {
        return context;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }

    public String getReferenceActions() {
        return super.getActions();
    }

    public String getPreviewActions() {
        return previewActions;
    }

    /**
     * Builder pattern used to simplify code writing.
     */
    public static class Builder {

        /** Indexes of rows. */
        private List<Integer> indexes;

        private String previewActions;

        private Configuration reference;

        private List<Integer> parseIndexes(final String indexes) {
            if (indexes == null) {
                return null;
            }
            try {
                final ObjectMapper mapper = new ObjectMapper(new JsonFactory());
                final JsonNode json = mapper.readTree(indexes);

                final List<Integer> result = new ArrayList<>(json.size());
                for (JsonNode index : json) {
                    result.add(index.intValue());
                }
                return result;
            } catch (IOException e) {
                throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_ACTIONS, e);
            }
        }

        public Builder withIndexes(String indexes) {
            this.indexes = parseIndexes(indexes);
            return this;
        }

        public Builder withActions(String previewActions) {
            this.previewActions = previewActions;
            return this;
        }

        public Builder fromReference(Configuration reference) {
            this.reference = reference;
            return this;
        }

        public PreviewConfiguration build() {
            return new PreviewConfiguration(reference, previewActions, indexes);
        }

    }
}
