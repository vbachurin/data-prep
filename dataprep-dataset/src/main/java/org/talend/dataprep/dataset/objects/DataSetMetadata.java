package org.talend.dataprep.dataset.objects;

import org.springframework.data.annotation.Id;

public class DataSetMetadata {

    @Id
    private final String id;

    public DataSetMetadata(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
