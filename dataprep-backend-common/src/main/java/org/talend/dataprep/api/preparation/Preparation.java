package org.talend.dataprep.api.preparation;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;

public class Preparation implements Identifiable {

    private String dataSetId;

    private String author;

    private String name;

    private long creationDate;

    private Step step;

    public Preparation() {
    }

    public Preparation(String dataSetId, Step step) {
        this.dataSetId = dataSetId;
        this.creationDate = System.currentTimeMillis();
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Preparation merge(Preparation other) {
        dataSetId = other.dataSetId != null ? other.dataSetId : dataSetId;
        author = other.author != null ? other.author : author;
        name = other.name != null ? other.name : name;
        creationDate = other.creationDate != 0 ? other.creationDate : creationDate;
        step = other.step != null ? other.step : step;
        return this;
    }
}
