package org.talend.dataprep.api.preparation;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.annotation.AccessType;

public class Step implements Identifiable {

    public static final Step ROOT_STEP = new Step(null, PreparationActions.ROOT_CONTENT.id());

    private String parentId = StringUtils.EMPTY;

    private String contentId;

    @AccessType(AccessType.Type.PROPERTY)
    private String id;

    public Step(final String parentId, final String contentId) {
        setParent(parentId);
        setContent(contentId);
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

    @Override
    public String id() {
        return getId();
    }

    public String getId() {
        return DigestUtils.sha1Hex(parentId + contentId);
    }

    public void setId(String id) {
        // No op
    }

    @Override
    public String toString() {
        return "Step {" + "id='" + id() + '\'' + ", parent='" + parentId + '\'' + ", content='" + contentId + '\'' + '}';
    }
}
