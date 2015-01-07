package org.talend.dataprep.dataset.service.analysis.schema;

import org.talend.dataprep.dataset.objects.ColumnMetadata;

import java.io.InputStream;
import java.util.List;

public interface SchemaParser {

    List<ColumnMetadata> parse(InputStream content);
}
