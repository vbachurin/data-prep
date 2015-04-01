package org.talend.dataprep.api.preparation;

import static java.lang.Math.max;
import static java.lang.Math.min;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.annotation.AccessType;

public class Preparation implements Identifiable {

    @AccessType(AccessType.Type.PROPERTY)
    private String id;

    private String dataSetId;

    private String author;

    private String name;

    private long creationDate;

    private long lastModificationDate;

    private Step step;

    public Preparation() {
        this.creationDate = System.currentTimeMillis();
        this.lastModificationDate = this.creationDate;
    }

    public Preparation(String dataSetId, Step step) {
        this();
        this.dataSetId = dataSetId;
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

    public long getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    @Override
    public String id() {
        return getId();
    }

    public String getId() {
        if (StringUtils.isEmpty(name)) {
            return DigestUtils.sha1Hex(dataSetId + author);
        }
        return DigestUtils.sha1Hex(dataSetId + author + name);
    }

    public void setId(String id) {
        // No op
    }

    @Override
    public String toString() {
        return "Preparation {" + "id='" + id() + '\'' + ", dataSetId='" + dataSetId + '\'' + ", author='" + author + '\''
                + ", creationDate=" + creationDate + ", lastModificationDate=" + lastModificationDate + ", step=" + step + '}';
    }

    public void updateLastModificationDate() {
        this.lastModificationDate = System.currentTimeMillis();
    }

    public Preparation merge(Preparation other) {
        dataSetId = other.dataSetId != null ? other.dataSetId : dataSetId;
        author = other.author != null ? other.author : author;
        name = other.name != null ? other.name : name;
        creationDate = min(other.creationDate, creationDate);
        lastModificationDate = max(other.lastModificationDate, lastModificationDate);
        step = other.step != null ? other.step : step;
        return this;
    }
}
