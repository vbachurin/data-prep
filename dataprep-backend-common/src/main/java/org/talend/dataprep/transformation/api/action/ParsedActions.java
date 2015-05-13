package org.talend.dataprep.transformation.api.action;

import java.util.function.Consumer;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;

/**
 * JavaBean that holds all the transformers.
 */
public class ParsedActions {

    /** The row transformers united into a single consumer. */
    private Consumer<DataSetRow> rowTransformer;

    /** The list of metadata transformers. */
    private Consumer<RowMetadata> metadataTransformers;

    /**
     * Default constructor.
     *
     * @param rowTransformer The row transformers united into a single consumer.
     * @param metadataTransformers The list of metadata transformers.
     */
    public ParsedActions(Consumer<DataSetRow> rowTransformer, Consumer<RowMetadata> metadataTransformers) {
        this.rowTransformer = rowTransformer;
        this.metadataTransformers = metadataTransformers;
    }

    /**
     * @return The row transformers united into a single consumer.
     */
    public Consumer<DataSetRow> getRowTransformer() {
        return rowTransformer;
    }

    /**
     * @return The list of metadata transformers.
     */
    public Consumer<RowMetadata> getMetadataTransformers() {
        return metadataTransformers;
    }
}
