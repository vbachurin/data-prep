package org.talend.dataprep.transformation.api.transformer.type;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.ParsedActions;
import org.talend.dataprep.transformation.exception.TransformationErrorCodes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * Records array transformer.
 */
@Component
public class RecordsTypeTransformer implements TypeTransformer {

    /**
     * @see TypeTransformer#process(JsonParser, JsonGenerator, List, boolean, ParsedActions...)
     */
    public void process(final JsonParser input, final JsonGenerator output, final List<Integer> indexes, final boolean preview,
            final ParsedActions... actions) {
        final Consumer<DataSetRow> oldAction = preview ? actions[0].getRowTransformer() : null;
        final Consumer<DataSetRow> action = preview ? actions[1].getRowTransformer() : actions[0].getRowTransformer();

        final boolean isIndexLimited = indexes != null && indexes.size() > 0;
        final Integer minIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).min().getAsInt() : null;
        final Integer maxIndex = isIndexLimited ? indexes.stream().mapToInt(Integer::intValue).max().getAsInt() : null;

        try {
            int currentIndex = 0;
            final DataSetRow row = new DataSetRow();
            String currentFieldName = "";

            JsonToken nextToken;
            while ((nextToken = input.nextToken()) != null) {
                switch (nextToken) {
                // Object delimiter
                case START_OBJECT:
                    row.clear();
                    break;
                case END_OBJECT:
                    if (preview) {
                        // apply old actions
                        final DataSetRow oldRow = row.clone();
                        oldAction.accept(oldRow);

                        if (isIndexLimited) {
                            // we only start to process at the min index
                            if (currentIndex >= minIndex) {

                                // apply new actions
                                action.accept(row);

                                // we are between the min and the max index
                                // 1. the row has a wanted index : we write it no matter what
                                // 2. the row has NOT a wanted index : we write it only if it was originally deleted,
                                // but not anymore
                                if (indexes.contains(currentIndex) || (oldRow.isDeleted() && !row.isDeleted())) {
                                    row.writePreviewTo(output, oldRow);
                                }
                            }

                            // if the oldRow is not deleted, we move the current index
                            // the index represents the originally not deleted rows
                            currentIndex = oldRow.isDeleted() ? currentIndex : currentIndex + 1;

                            // we stop the process after the max index
                            if (currentIndex > maxIndex) {
                                output.writeEndArray();
                                output.flush();
                                return;
                            }
                        } else {
                            // apply new actions
                            action.accept(row);

                            // write preview. Rules are delegated to DataSetRow
                            row.writePreviewTo(output, oldRow);
                        }
                    } else {
                        action.accept(row);
                        row.writeTo(output);
                    }

                    break;

                // DataSetRow fields
                case FIELD_NAME:
                    currentFieldName = input.getText();
                    break;
                case VALUE_STRING:
                    row.set(currentFieldName, input.getText());
                    break;

                // Array delimiter : on array end, we consider the column part ends
                case START_ARRAY:
                    output.writeStartArray();
                    break;
                case END_ARRAY:
                    output.writeEndArray();
                    output.flush();
                    return;

                }
            }
        } catch (IOException e) {
            throw new TDPException(TransformationErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
