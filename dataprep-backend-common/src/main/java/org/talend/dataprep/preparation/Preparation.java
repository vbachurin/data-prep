package org.talend.dataprep.preparation;

import org.apache.commons.codec.digest.DigestUtils;

public class Preparation {

    private String id;

    private String dataSetId;

    public Preparation(String dataSetId) {
        this.dataSetId = dataSetId;
        id = DigestUtils.sha1Hex(dataSetId);
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public String getId() {
        return id;
    }

}
