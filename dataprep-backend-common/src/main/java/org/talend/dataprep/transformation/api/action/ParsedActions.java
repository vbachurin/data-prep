package org.talend.dataprep.transformation.api.action;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;

/**
 * JavaBean that holds all the transformers.
 */
public class ParsedActions {

    /** The row transformers united into a single consumer. */
    private Consumer<DataSetRow> rowTransformer;

    /** The list of metadata transformers. */
    private List<Function<List<ColumnMetadata>, List<ColumnMetadata>>> metadataTransformers;

    /**
     * Default constructor.
     *
     * @param rowTransformer The row transformers united into a single consumer.
     * @param metadataTransformers The list of metadata transformers.
     */
    public ParsedActions(Consumer<DataSetRow> rowTransformer,
            List<Function<List<ColumnMetadata>, List<ColumnMetadata>>> metadataTransformers) {
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
    public List<Function<List<ColumnMetadata>, List<ColumnMetadata>>> getMetadataTransformers() {
        return metadataTransformers;
    }
}
