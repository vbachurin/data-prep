package org.talend.dataprep.api.dataset.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class DataSetRowIterator implements Iterator<DataSetRow> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetRowIterator.class);

    private final JsonParser parser;

    private final DataSetRow row = new DataSetRow();

    public DataSetRowIterator(JsonParser parser) {
        this.parser = parser;
    }

    public DataSetRowIterator(InputStream inputStream) {
        try {
            parser = new JsonFactory().createParser(inputStream);
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }

    @Override
    public boolean hasNext() {
        try {
            return !parser.isClosed() && parser.nextToken() != JsonToken.END_ARRAY;
        } catch (IOException e) {
            LOGGER.debug("Unable to check if a next record exists.", e);
            return false;
        }
    }

    @Override
    public DataSetRow next() {
        try {
            String currentFieldName = StringUtils.EMPTY;
            JsonToken nextToken;
            while ((nextToken = parser.nextToken()) != JsonToken.END_OBJECT) {
                switch (nextToken) {
                case START_OBJECT:
                    row.clear();
                    break;
                // DataSetRow fields
                case FIELD_NAME:
                    currentFieldName = parser.getText();
                    break;
                case VALUE_STRING:
                    row.set(currentFieldName, parser.getText());
                    break;
                }
            }
            return row;
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        }
    }
}
