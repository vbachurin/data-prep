package org.talend.dataprep.api.preparation;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.annotation.PersistenceConstructor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Step extends Identifiable implements Serializable {

    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    /**
     * The "root step": this is the initial step for all preparation (i.e. a newly created preparation has this root
     * step as head).
     */
    public static final Step ROOT_STEP = new Step(null, PreparationActions.ROOT_CONTENT.id());

    private String parentId = StringUtils.EMPTY;

    private String contentId;

    private StepDiff diff;

    //@formatter:off
    @JsonCreator
    public Step(@JsonProperty("parentId") final String parentId, @JsonProperty("contentId") final String contentId) {
       this(parentId, contentId, null);
    }
    //@formatter:on

    @PersistenceConstructor
    public Step(final String parentId, final String contentId, final StepDiff diff) {
        setParent(parentId);
        setContent(contentId);
        setDiff(diff);
    }

    public String getContent() {
        return contentId;
    }

    public void setContent(String content) {
        this.contentId = content;
    }

    public String getParent() {
        return parentId;
    }

    public void setParent(String parent) {
        this.parentId = parent;
    }

    public StepDiff getDiff() {
        return diff;
    }

    public void setDiff(StepDiff diff) {
        this.diff = diff;
    }

    @Override
    public String id() {
        return getId();
    }

    @Override
    public String getId() {
        return DigestUtils.sha1Hex(parentId + contentId);
    }

    @Override
    public void setId(String id) {
        // No op
    }

    @Override
    public String toString() {
        return "Step {" + "id='" + id() + '\'' + ", parent='" + parentId + '\'' + ", content='" + contentId + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Step step = (Step) o;
        return Objects.equals(parentId, step.parentId) && Objects.equals(contentId, step.contentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentId, contentId);
    }

}
