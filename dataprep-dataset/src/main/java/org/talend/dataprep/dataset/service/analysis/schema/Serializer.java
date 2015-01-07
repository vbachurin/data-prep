package org.talend.dataprep.dataset.service.analysis.schema;

import org.talend.dataprep.dataset.objects.DataSetMetadata;

import java.io.InputStream;

public interface Serializer {
    InputStream serialize(InputStream rawContent, DataSetMetadata metadata);
}
