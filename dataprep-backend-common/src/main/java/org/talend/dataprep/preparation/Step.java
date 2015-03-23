package org.talend.dataprep.preparation;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.annotation.Id;

public class Step implements Object {

    private String parent = StringUtils.EMPTY;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Id
    @Override
    public String id() {
        return DigestUtils.sha1Hex(parent + content);
    }

    @Override
    public String toString() {
        return "Step {" +
                "id='" + id() + '\'' +
                ", parent='" + parent + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
