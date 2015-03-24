package org.talend.dataprep.api.preparation;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;

public abstract class Blob implements Object {

    private final String content;

    public Blob(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Id
    @Override
    public String id() {
        return DigestUtils.sha1Hex(content);
    }

    @Override
    public String toString() {
        return "Blob {" + "id='" + id() + '\'' + '}';
    }
}
