package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.DataSetRowWithDiff;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.transformer.TransformerWriter;
import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

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
        final JsonParser parser = configuration.getInput();

        final List<Consumer<DataSetRow>> actions = configuration.getActions(DataSetRow.class);
        final List<Integer> indexes = configuration.getIndexes();

        final Consumer<DataSetRow> referenceAction = configuration.isPreview() ? actions.get(0) : null;
        final Consumer<DataSetRow> action = configuration.isPreview() ? actions.get(1) : actions.get(0);

        final boolean isIndexLimited = indexes != null && indexes.size() > 0;
        final Integer minIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).min().getAsInt() : null;
        final Integer maxIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).max().getAsInt() : null;

        try {
            int currentIndex = 0;
            final DataSetRow row = new DataSetRow();
            String currentFieldName = "";

            JsonToken nextToken;
            while ((nextToken = parser.nextToken()) != null) {
                switch (nextToken) {
                // Object delimiter
                case START_OBJECT:
                    row.clear();
                    break;
                case END_OBJECT:
                    if (configuration.isPreview()) {

                        // compute the reference row
                        final DataSetRow referenceRow = row.clone();
                        referenceAction.accept(referenceRow);

                        // switch to a dataset row with diff
                        final DataSetRowWithDiff rowWithDiff = new DataSetRowWithDiff(row, referenceRow);

                        if (isIndexLimited) {
                            // we only start to process at the min index
                            if (currentIndex >= minIndex) {

                                // apply new actions
                                action.accept(rowWithDiff);

                                // we are between the min and the max index
                                // 1. the row has a wanted index : we write it no matter what
                                // 2. the row has NOT a wanted index : we write it only if it was originally deleted,
                                // but not anymore
                                if (indexes.contains(currentIndex) || (referenceRow.isDeleted() && !rowWithDiff.isDeleted())) {
                                    writeRow(writer, rowWithDiff);
                                }
                            }

                            // if the oldRow is not deleted, we move the current index
                            // the index represents the originally not deleted rows
                            currentIndex = referenceRow.isDeleted() ? currentIndex : currentIndex + 1;

                            // we stop the process after the max index
                            if (currentIndex > maxIndex) {
                                writer.endArray();
                                return;
                            }
                        } else {
                            // apply new actions
                            action.accept(row);

                            // write preview. Rules are delegated to DataSetRow
                            writeRow(writer, row);
                        }
                    } else {
                        action.accept(row);
                        writeRow(writer, row);
                    }

                    break;

                // DataSetRow fields
                case FIELD_NAME:
                    currentFieldName = parser.getText();
                    break;
                case VALUE_STRING:
                    row.set(currentFieldName, parser.getText());
                    break;

                // Array delimiter : on array end, we consider the column part ends
                case START_ARRAY:
                    writer.startArray();
                    break;
                case END_ARRAY:
                    writer.endArray();
                    return;

                }
            }
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    private void writeRow(TransformerWriter writer, DataSetRow row) throws IOException {
        if (row.shouldWrite()) {
            writer.write(row);
        }
    }
}
