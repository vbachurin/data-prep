package org.talend.dataprep.api.dataset.json;

import org.talend.dataprep.api.dataset.DataSetMetadata;

import com.fasterxml.jackson.core.JsonToken;

interface State {

    void handle(Context context, DataSetMetadata.Builder builder, JsonToken token) throws Exception;
}
