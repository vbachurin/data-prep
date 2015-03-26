package org.talend.dataprep.preparation;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;

public class Preparation implements Identifiable {

    private String dataSetId;

    private String author;

    private long creationDate;

    private Step step;

    public Preparation() {
    }

    public Preparation(String dataSetId, Step step) {
        this.dataSetId = dataSetId;
        this.creationDate = System.currentTimeMillis();
        this.step = step;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    @Id
    @Override
    public String id() {
        return DigestUtils.sha1Hex(dataSetId + author);
    }

    @Override
    public String toString() {
        return "Preparation {" + "id='" + id() + '\'' + ", dataSetId='" + dataSetId + '\'' + ", author='" + author + '\''
                + ", creationDate=" + creationDate + ", step=" + step + '}';
    }
}
