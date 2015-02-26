package org.talend.dataprep.api.json;

import com.fasterxml.jackson.core.JsonToken;
import org.talend.dataprep.api.DataSetMetadata;

interface State {

    void handle(Context context, DataSetMetadata.Builder builder, JsonToken token) throws Exception;
}
