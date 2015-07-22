package org.talend.dataprep.transformation.api.transformer.json;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.configuration.Configuration;
import org.talend.dataprep.transformation.api.transformer.configuration.PreviewConfiguration;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

/**
 * Transformer that preview the transformation (puts additional json content so that the front can display the
 * difference between current and previous transformation).
 */
@Component
class DiffTransformer implements Transformer {

    /** The data-prep ready jackson module. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Autowired
    private ActionParser actionParser;
    
    /**
     * Starts the transformation in preview mode.
     * @param input the dataset content.
     * @param configuration The {@link Configuration configuration} for this transformation.
     */
    @Override
    public void transform(DataSet input, Configuration configuration) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        final PreviewConfiguration previewConfiguration = (PreviewConfiguration) configuration;
        final TransformerWriter writer = configuration.writer();

        //parse and extract diff configuration
        final ParsedActions referenceActions = actionParser.parse(previewConfiguration.getReferenceActions());
        final BiFunction<DataSetRow, TransformationContext, DataSetRow> referenceAction = referenceActions.asUniqueRowTransformer();
        TransformationContext referenceContext = previewConfiguration.getReferenceContext();

        final ParsedActions previewActions = actionParser.parse(previewConfiguration.getPreviewActions());
        final BiFunction<DataSetRow, TransformationContext, DataSetRow> previewAction = previewActions.asUniqueRowTransformer();
        TransformationContext previewContext = previewConfiguration.getPreviewContext();

        //extract TDP ids infos
        final List<Long> indexes = previewConfiguration.getIndexes();
        final boolean isIndexLimited = indexes != null && !indexes.isEmpty();
        final Long minIndex = isIndexLimited ? indexes.stream().mapToLong(Long::longValue).min().getAsLong() : 0L;
        final Long maxIndex = isIndexLimited ? indexes.stream().mapToLong(Long::longValue).max().getAsLong() : Long.MAX_VALUE;

        //records process lambdas
        final Predicate<DataSetRow> isWithinWantedIndexes = row -> row.getTdpId() >= minIndex && row.getTdpId() <= maxIndex;
        final Function<DataSetRow, DataSetRow[]> createClone = row -> new DataSetRow[]{row.clone(), row};
        final Function<DataSetRow[], DataSetRow[]> applyTransformations = rows -> {
            referenceAction.accept(rows[0], referenceContext);
            previewAction.accept(rows[1], previewContext);
            return rows;
        };
        final Predicate<DataSetRow[]> shouldWriteDiff = rows -> indexes == null || // no wanted index, we process all rows
                indexes.contains(rows[0].getTdpId()) || // row is a wanted diff row
                rows[0].isDeleted() && !rows[1].isDeleted(); // row is deleted but preview is not
        final Consumer<DataSetRow[]> writeDiff = rows -> {
            rows[1].diff(rows[0]);
            try {
                if (rows[1].shouldWrite()) {
                    writer.write(rows[1]);
                }
            } catch (IOException e) {
                throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
            }
        };

        try {

            // defensive programming
            if (referenceAction == null) {
                throw new IllegalStateException("No old action to perform for preview.");
            }

            writer.startObject();

            // Metadata
            writer.fieldName("columns");

            RowMetadata rowMetadata = new RowMetadata(input.getColumns());
            RowMetadata referenceMetadata = rowMetadata.clone();

            // only apply preview actions if there's any
            if (!previewActions.getAllActions().isEmpty()) {
                rowMetadata = previewAction.apply(new DataSetRow(rowMetadata), previewContext).getRowMetadata();
            }

            // only apply actions if there's any
            if (!referenceActions.getAllActions().isEmpty()) {
                referenceMetadata = referenceAction.apply(new DataSetRow(referenceMetadata), referenceContext).getRowMetadata();
            }
            
            rowMetadata.diff(referenceMetadata);
            writer.write(rowMetadata);

            // Records
            writer.fieldName("records");
            writer.startArray();
            if (referenceAction == null) {
                throw new IllegalStateException("No old action to perform for preview.");
            }

            input.getRecords() //
                    .filter(isWithinWantedIndexes) //
                    .map(createClone) //
                    .map(applyTransformations) //
                    .filter(shouldWriteDiff) //
                    .forEach(writeDiff);

            writer.endArray();
            writer.endObject();
            writer.flush();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
        }
    }

    @Override
    public boolean accept(Configuration configuration) {
        return PreviewConfiguration.class.isAssignableFrom(configuration.getClass());
    }
}
