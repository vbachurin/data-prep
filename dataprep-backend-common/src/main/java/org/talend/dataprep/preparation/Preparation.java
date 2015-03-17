package org.talend.dataprep.preparation;

import org.apache.commons.codec.digest.DigestUtils;

public class Preparation {

    private String id;

    private String dataSetId;

    private String author;

    private long creationDate;

    public Preparation() {
    }

    public Preparation(String dataSetId) {
        this.dataSetId = dataSetId;
        id = DigestUtils.sha1Hex(dataSetId);
        creationDate = System.currentTimeMillis();
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public String getId() {
        return id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getCreationDate() {
        return creationDate;
    }
}
