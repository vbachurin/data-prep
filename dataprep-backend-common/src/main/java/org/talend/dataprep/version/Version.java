package org.talend.dataprep.version;

import org.springframework.data.annotation.Id;
import org.talend.dataprep.api.DataSetMetadata;

public class Version {

    @Id
    String id;

    String parent;

    String actions;

    DataSetMetadata metadata;

    

}
