package org.talend.dataprep.dataset.store.content.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSetContent;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.location.HttpLocation;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.store.content.DataSetContentStore;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.schema.FormatGuess;
import org.talend.dataprep.schema.Serializer;

/**
 * Remote http dataset content store implementation.
 */
@Component("ContentStore#http")
public class HttpContentStore implements DataSetContentStore {

    @Autowired
    FormatGuess.Factory factory;

    /**
     * @see DataSetContentStore#get(DataSetMetadata)
     */
    @Override
    public InputStream get(DataSetMetadata dataSetMetadata) {
        DataSetContent content = dataSetMetadata.getContent();
        Serializer serializer = factory.getFormatGuess(content.getFormatGuessId()).getSerializer();
        return serializer.serialize(getAsRaw(dataSetMetadata), dataSetMetadata);
    }

    /**
     * @see DataSetContentStore#getAsRaw(DataSetMetadata)
     */
    @Override
    public InputStream getAsRaw(DataSetMetadata dataSetMetadata) {
        HttpLocation location = (HttpLocation) dataSetMetadata.getLocation();
        HttpGet get = new HttpGet(location.getUrl());
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            response = client.execute(get);
            return response.getEntity().getContent();
        } catch (IOException e) {
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_READ_DATASET_CONTENT, e);
        }
    }

    /**
     * @see DataSetContentStore#storeAsRaw(DataSetMetadata, InputStream)
     */
    @Override
    public void storeAsRaw(DataSetMetadata dataSetMetadata, InputStream dataSetContent) {
        // nothing to do here since the dataset is already stored
    }

    /**
     * @see DataSetContentStore#delete(DataSetMetadata)
     */
    @Override
    public void delete(DataSetMetadata dataSetMetadata) {
        // nothing to do here
    }

    /**
     * @see DataSetContentStore#clear()
     */
    @Override
    public void clear() {
        // nothing to do here...
    }
}
