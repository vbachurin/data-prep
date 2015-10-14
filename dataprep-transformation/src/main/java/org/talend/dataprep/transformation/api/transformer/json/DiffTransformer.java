package org.talend.dataprep.transformation.api.transformer.json;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.TransformationErrorCodes;
import org.talend.dataprep.transformation.api.action.ActionParser;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.transformer.Transformer;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.configuration.Configuration;
import org.talend.dataprep.transformation.api.transformer.configuration.PreviewConfiguration;
import org.talend.dataprep.transformation.format.WriterRegistrationService;

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

    /** Service who knows about registered writers. */
    @Autowired
    private WriterRegistrationService writersService;

    /**
     * Starts the transformation in preview mode.
     *
     * @param input the dataset content.
     * @param configuration The {@link Configuration configuration} for this transformation.
     */
    @Override
    public void transform(DataSet input, Configuration configuration) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        final PreviewConfiguration previewConfiguration = (PreviewConfiguration) configuration;
        final TransformerWriter writer = writersService.getWriter(configuration.formatId(), configuration.output(),
                configuration.getArguments());

        //parse and extract diff configuration
        final ParsedActions referenceActions = actionParser.parse(previewConfiguration.getReferenceActions());
        InternalTransformationContext reference = new InternalTransformationContext(referenceActions,
                previewConfiguration.getReferenceContext());

        final ParsedActions previewActions = actionParser.parse(previewConfiguration.getPreviewActions());
        InternalTransformationContext preview = new InternalTransformationContext(previewActions,
                previewConfiguration.getPreviewContext());

        //extract TDP ids infos
        final List<Long> indexes = previewConfiguration.getIndexes();
        final boolean isIndexLimited = indexes != null && !indexes.isEmpty();
        final Long minIndex = isIndexLimited ? indexes.stream().mapToLong(Long::longValue).min().getAsLong() : 0L;
        final Long maxIndex = isIndexLimited ? indexes.stream().mapToLong(Long::longValue).max().getAsLong() : Long.MAX_VALUE;

        try {

            // defensive programming
            if (reference.getAction() == null) {
                throw new IllegalStateException("No old action to perform for preview.");
            }

            writer.startObject();

            // Metadata
            writer.fieldName("columns");

            RowMetadata rowMetadata = new RowMetadata(input.getColumns());
            RowMetadata referenceMetadata = rowMetadata.clone();

            rowMetadata = preview.apply(new DataSetRow(rowMetadata)).getRowMetadata();
            referenceMetadata = reference.apply(new DataSetRow(referenceMetadata)).getRowMetadata();

            rowMetadata.diff(referenceMetadata);
            writer.write(rowMetadata);

            // Records
            writer.fieldName("records");
            writer.startArray();

            input.getRecords() //
                    .filter(isWithinWantedIndexes(minIndex, maxIndex)) //
                    .map(createClone()) //
                    .map(applyTransformations(reference, preview)) //
                    .filter(shouldWriteDiff(indexes)) //
                    .forEach(writeDiff(writer));

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

    private Predicate<DataSetRow> isWithinWantedIndexes(Long minIndex, Long maxIndex) {
        return row -> row.getTdpId() >= minIndex && row.getTdpId() <= maxIndex;
    }

    private Function<DataSetRow, DataSetRow[]> createClone() {
        return row -> new DataSetRow[] { row.clone(), row };
    }

    private Function<DataSetRow[], DataSetRow[]> applyTransformations(InternalTransformationContext reference,
            InternalTransformationContext preview) {
        return rows -> {
            reference.apply(rows[0]);
            preview.apply(rows[1]);
            return rows;
        };
    }

    private Predicate<DataSetRow[]> shouldWriteDiff(List<Long> indexes) {
        return rows -> indexes == null || // no wanted index, we process all rows
                indexes.contains(rows[0].getTdpId()) || // row is a wanted diff row
                rows[0].isDeleted() && !rows[1].isDeleted(); // row is deleted but preview is not
    }

    private Consumer<DataSetRow[]> writeDiff(TransformerWriter writer) {
        return rows -> {
            rows[1].diff(rows[0]);
            try {
                if (rows[1].shouldWrite()) {
                    writer.write(rows[1]);
                }
            } catch (IOException e) {
                throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
            }
        };
    }

    private class InternalTransformationContext {

        ParsedActions parsedActions;

        private BiFunction<DataSetRow, TransformationContext, DataSetRow> action;

        private TransformationContext context;

        public InternalTransformationContext(ParsedActions parsedActions, TransformationContext context) {
            this.parsedActions = parsedActions;
            this.action = parsedActions.asUniqueRowTransformer();
            this.context = context;
        }

        public BiFunction<DataSetRow, TransformationContext, DataSetRow> getAction() {
            return action;
        }

        public TransformationContext getContext() {
            return context;
        }

        public DataSetRow apply(DataSetRow dataSetRow) {
            return action.apply(dataSetRow, context);
        }
    }

}
