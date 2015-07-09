package org.talend.dataprep.transformation.api.transformer.json;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
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

        final ParsedActions referenceActions = actionParser.parse(previewConfiguration.getReferenceActions());
        final ParsedActions previewActions = actionParser.parse(previewConfiguration.getPreviewActions());
        final BiConsumer<DataSetRow, TransformationContext> referenceAction = referenceActions.asUniqueRowTransformer();
        final BiConsumer<RowMetadata, TransformationContext> referenceMetadataAction = referenceActions.asUniqueMetadataTransformer();
        final BiConsumer<RowMetadata, TransformationContext> previewMetadataAction = previewActions.asUniqueMetadataTransformer();
        final BiConsumer<DataSetRow, TransformationContext> previewAction = previewActions.asUniqueRowTransformer();

        TransformationContext referenceContext = previewConfiguration.getReferenceContext();
        TransformationContext previewContext = previewConfiguration.getPreviewContext();

        final List<Integer> indexes = previewConfiguration.getIndexes();
        final boolean isIndexLimited = indexes != null && !indexes.isEmpty();
        final Integer minIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).min().getAsInt() : 0;
        final Integer maxIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).max().getAsInt() : Integer.MAX_VALUE;

        Stream<DataSetRow> records = input.getRecords();
        try {
            writer.startObject();
            // Metadata
            writer.fieldName("columns");
            RowMetadata referenceMetadata = new RowMetadata(input.getColumns());
            RowMetadata rowMetadata = new RowMetadata(input.getColumns());
            referenceMetadataAction.accept(referenceMetadata, referenceContext);
            previewMetadataAction.accept(rowMetadata, previewContext);
            rowMetadata.diff(referenceMetadata);
            writer.write(rowMetadata);
            // Records
            writer.fieldName("records");
            writer.startArray();
            AtomicInteger index = new AtomicInteger(0);
            if (referenceAction == null) {
                throw new IllegalStateException("No old action to perform for preview.");
            }
            final AtomicInteger resultIndexShift = new AtomicInteger();
            // With preview (no 'old row' and 'new row' to compare when writing results).
            Stream<Processing[]> process = records
                    .map(row -> new Processing(row, index.getAndIncrement() - resultIndexShift.get())) //
                    .map(p -> new Processing[]{new Processing(p.row.clone(), p.index), p}) //
                    .map(p -> {
                        referenceAction.accept(p[0].row, referenceContext);
                        if (p[0].row.isDeleted()) {
                            resultIndexShift.incrementAndGet();
                        }
                        previewAction.accept(p[1].row, previewContext);
                        return p;
                    }); //
            if (indexes != null) {
                process = process.filter(p -> {
                    final boolean inRange = p[1].index >= minIndex && p[1].index <= maxIndex;
                    final boolean include = indexes.contains(p[1].index) || (p[0].row.isDeleted() && !p[1].row.isDeleted());
                    return inRange && include;
                });
            }
            process.forEach(p -> {
                p[1].row.diff(p[0].row);
                try {
                    if (p[1].row.shouldWrite()) {
                        writer.write(p[1].row);
                    }
                } catch (IOException e) {
                    throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
                }
            });
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

    public static class Processing {
        DataSetRow row;
        int index;

        public Processing(DataSetRow row, int index) {
            this.row = row;
            this.index = index;
        }
    }
}
