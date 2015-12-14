package org.talend.dataprep.api.preparation;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

public class Preparation extends Identifiable implements Serializable {

    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    /** The dataset id. */
    private String dataSetId;

    /** The author name. */
    private String author;

    /** The preparation name. */
    private String name;

    /** The creation date. */
    private long creationDate;

    /** The last modification date. */
    private long lastModificationDate;

    /** The head id. */
    private String headId;

    /** List of the steps id for this preparation. */
    private List<String> steps;

    /**
     * Default empty constructor.
     */
    public Preparation() {
        this.creationDate = System.currentTimeMillis();
        this.lastModificationDate = this.creationDate;
    }

    /**
     * Create a preparation out of the given parameters.
     *
     * @param dataSetId the dataset id.
     * @param headId the head step id.
     */
    public Preparation(String dataSetId, String headId) {
        this();
        this.dataSetId = dataSetId;
        this.headId = headId;
    }

    /**
     * Creates a default preparation (no author) for <code>dataSetId</code> at {@link Step#ROOT_STEP}.
     * @param dataSetId A data set id.
     * @return A {@link Preparation preparation} where head is set to {@link Step#ROOT_STEP root step}.
     */
    public static Preparation defaultPreparation(String dataSetId) {
        return new Preparation(dataSetId, Step.ROOT_STEP.id());
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
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

    /**
     * @return the HeadId
     */
    public String getHeadId() {
        return headId;
    }

    /**
     * @param headId the headId to set.
     */
    public void setHeadId(String headId) {
        this.headId = headId;
    }

    @Override
    public String id() {
        return getId();
    }

    @Override
    public String getId() {
        if (StringUtils.isEmpty(name)) {
            return DigestUtils.sha1Hex(dataSetId + author);
        }
        return DigestUtils.sha1Hex(dataSetId + author + name);
    }

    @Override
    public void setId(String id) {
        // No op
    }

    @Override
    public String toString() {
        return "Preparation {" + //
                "id='" + id() + '\'' + //
                ", dataSetId='" + dataSetId + '\'' + //
                ", author='" + author + '\'' + //
                ", creationDate=" + creationDate + //
                ", lastModificationDate=" + lastModificationDate + //
                ", headId='" + headId +"'}";
    }

    public void updateLastModificationDate() {
        this.lastModificationDate = System.currentTimeMillis();
    }

    public Preparation merge(Preparation other) {
        Preparation merge = new Preparation();
        merge.dataSetId = other.dataSetId != null ? other.dataSetId : dataSetId;
        merge.author = other.author != null ? other.author : author;
        merge.name = other.name != null ? other.name : name;
        merge.creationDate = min(other.creationDate, creationDate);
        merge.lastModificationDate = max(other.lastModificationDate, lastModificationDate);
        merge.headId = other.headId != null ? other.headId : headId;
        return merge;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Preparation that = (Preparation) o;
        return Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(lastModificationDate, that.lastModificationDate) &&
                Objects.equals(dataSetId, that.dataSetId) &&
                Objects.equals(author, that.author) &&
                Objects.equals(name, that.name) &&
                Objects.equals(headId, that.headId);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(dataSetId, author, name, creationDate, lastModificationDate, headId);
    }
}
