package org.talend.dataprep.api.dataset.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import static org.talend.dataprep.api.dataset.DataSetRow.TDP_ID;

public class DataSetRowIterator implements Iterator<DataSetRow> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetRowIterator.class);

    private final JsonParser parser;

    private final DataSetRow row;

    private long nextRowId = 0;

    public DataSetRowIterator(JsonParser parser) {
        this.parser = parser;
        row = new DataSetRow(rowMetadata);
    }

    public DataSetRowIterator(InputStream inputStream) {
        try {
            parser = new JsonFactory().createParser(inputStream);
            row = new DataSetRow(new RowMetadata());
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    @Override
    public boolean hasNext() {
        return !parser.isClosed() && parser.getCurrentToken() != JsonToken.END_ARRAY;
    }

    @Override
    public DataSetRow next() {
        try {
            String currentFieldName = StringUtils.EMPTY;
            JsonToken nextToken;
            row.clear();
            row.setTdpId(nextRowId++);
            while ((nextToken = parser.nextToken()) != JsonToken.END_OBJECT) {
                if (nextToken == null) {
                    // End of input, return the current row.
                    LOGGER.warn("Unexpected end of input for row {}.", row.values());
                    return row;
                }
                switch (nextToken) {
                // DataSetRow fields
                case FIELD_NAME:
                    currentFieldName = parser.getText();
                    break;
                case VALUE_STRING:
                    if(TDP_ID.equals(currentFieldName)) {
                        row.setTdpId(Long.parseLong(parser.getText()));
                    }
                    else {
                        row.set(currentFieldName, parser.getText());
                    }
                    break;
                case VALUE_NULL:
                    row.set(currentFieldName, "");
                    break;
                default:
                    // let's skip this unsupported token
                    break;
                }
            }
            parser.nextToken();
            return row;
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
