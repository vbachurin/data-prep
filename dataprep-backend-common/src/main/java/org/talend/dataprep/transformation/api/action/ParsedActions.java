package org.talend.dataprep.transformation.api.action;

import static org.talend.dataprep.transformation.api.action.Aggregate.aggregate;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * JavaBean that holds all the transformers.
 */
public class ParsedActions {

    /** The row transformers united into a single consumer. */
    private final List<DataSetRowAction> rowTransformers = new LinkedList<>();

    /** The list of metadata transformers. */
    private final List<DataSetMetadataAction> metadataTransformers = new LinkedList<>();

    /**
     * Default constructor.
     *
     * @param rowTransformer The unique row transformer united into a single consumer.
     * @param metadataTransformer The unique metadata transformer.
     */
    public ParsedActions(DataSetRowAction rowTransformer, DataSetMetadataAction metadataTransformer) {
        this.rowTransformers.add(rowTransformer);
        this.metadataTransformers.add(metadataTransformer);
    }

    /**
     * Default constructor.
     *
     * @param rowTransformers The row transformers united into a single consumer.
     * @param metadataTransformers The list of metadata transformers.
     */
    public ParsedActions(List<DataSetRowAction> rowTransformers, List<DataSetMetadataAction> metadataTransformers) {
        this.rowTransformers.addAll(rowTransformers);
        this.metadataTransformers.addAll(metadataTransformers);
    }

    public List<DataSetRowAction> getRowTransformers() {
        return rowTransformers;
    }

    public List<DataSetMetadataAction> getMetadataTransformers() {
        return metadataTransformers;
    }

    /**
     * @return The row transformers united into a single consumer.
     */
    public BiConsumer<DataSetRow, TransformationContext> asUniqueRowTransformer() {
        return aggregate(rowTransformers);
    }

    /**
     * @return The list of metadata transformers.
     */
    public BiConsumer<RowMetadata, TransformationContext> asUniqueMetadataTransformer() {
        return aggregate(metadataTransformers);
    }
}
