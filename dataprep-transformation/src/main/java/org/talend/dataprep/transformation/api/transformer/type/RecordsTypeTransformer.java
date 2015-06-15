package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.List;
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
        final Integer minIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).min().getAsInt() : null;
        final Integer maxIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).max().getAsInt() : Integer.MAX_VALUE;

        final Stream<DataSetRow> records = dataSet.getRecords();
        try {
            writer.startArray();
            if (!configuration.isPreview()) {
                // No preview (no 'old row' and 'new row' to compare when writing results).
                records.map(row -> {
                    action.accept(row, context);
                    return row;
                }).forEach(row -> writeRow(writer, row));
            } else {
                if (referenceAction == null) {
                    throw new IllegalStateException("No old action to perform for preview.");
                }
                TransformationContext referenceContext = configuration.isPreview() ? configuration.getTransformationContext(0) : null;
                // With preview (no 'old row' and 'new row' to compare when writing results).
                records.map(row -> new DataSetRow[]{row, row.clone()}).map(rows -> {
                    referenceAction.accept(rows[0], referenceContext);
                    action.accept(rows[1], context);
                    return rows;
                }).forEach(rows -> {
                    rows[0].diff(rows[1]);
                    writeRow(writer, rows[0]);
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
     * @throws IOException if an error occurs.
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

}
