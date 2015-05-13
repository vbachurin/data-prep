package org.talend.dataprep.api.dataset.json;

import com.fasterxml.jackson.core.JsonToken;

interface State {

    void handle(Context context, JsonToken token) throws Exception;
}
