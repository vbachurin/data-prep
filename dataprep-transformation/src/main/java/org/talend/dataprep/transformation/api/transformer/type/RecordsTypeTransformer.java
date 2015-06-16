package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

/**
 * Transforms dataset rows.
 */
@Component
public class RecordsTypeTransformer implements TypeTransformer {

    /**
     * @see TypeTransformer#process(TransformerConfiguration)
     */
    @Override
    public void process(final TransformerConfiguration configuration) {
        final TransformerWriter writer = configuration.getOutput();
        final DataSet dataSet = configuration.getInput();

        final List<BiConsumer<DataSetRow, TransformationContext>> actions = configuration.getRecordActions();
        final List<Integer> indexes = configuration.getIndexes();

        final BiConsumer<DataSetRow, TransformationContext> referenceAction = configuration.isPreview() ? actions.get(0) : null;

        final BiConsumer<DataSetRow, TransformationContext> action = configuration.isPreview() ? actions.get(1) : actions.get(0);
        TransformationContext context = configuration.isPreview() ? configuration.getTransformationContext(1) : configuration
                .getTransformationContext(0);

        final boolean isIndexLimited = indexes != null && !indexes.isEmpty();
        final Integer minIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).min().getAsInt() : 0;
        final Integer maxIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).max().getAsInt() : Integer.MAX_VALUE;

        Stream<DataSetRow> records = dataSet.getRecords();
        try {
            writer.startArray();
            AtomicInteger index = new AtomicInteger(0);
            if (!configuration.isPreview()) {
                // No preview (no 'old row' and 'new row' to compare when writing results).
                Stream<Processing> process = records.map(row -> new Processing(row, index.getAndIncrement())) //
                        .map(p -> {
                            action.accept(p.row, context);
                            return p;
                        }) //
                        .skip(minIndex) //
                        .limit(maxIndex);
                if (indexes != null) {
                    process = process.filter(p -> indexes.contains(p.index));
                }
                process.forEach(row -> writeRow(writer, row.row));
            } else {
                if (referenceAction == null) {
                    throw new IllegalStateException("No old action to perform for preview.");
                }
                TransformationContext referenceContext = configuration.isPreview() ? configuration.getTransformationContext(0) : null;
                // With preview (no 'old row' and 'new row' to compare when writing results).
                Stream<Processing[]> process = records.map(row -> new Processing(row, index.getAndIncrement())) //
                        .map(p -> new Processing[]{new Processing(p.row.clone(), p.index), p}) //
                        .map(p -> {
                            referenceAction.accept(p[0].row, referenceContext);
                            action.accept(p[1].row, context);
                            return p;
                        }) //
                        .skip(minIndex) //
                        .limit(maxIndex);
                if (indexes != null) {
                    process = process.filter(p -> indexes.contains(p[1].index) || (p[0].row.isDeleted() && !p[1].row.isDeleted()));
                }
                process.forEach(p -> {
                    p[1].row.diff(p[0].row);
                    writeRow(writer, p[1].row);
                });
            }
                writer.endArray();
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
        }
    }

    /**
     * Write the given row using the given writer.
     *
     * @param writer the writer to use.
     * @param row the row to write.
     */
    private void writeRow(TransformerWriter writer, DataSetRow row) {
        try {
            if (row.shouldWrite()) {
                writer.write(row);
            }
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TRANSFORM_DATASET, e);
        }
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
