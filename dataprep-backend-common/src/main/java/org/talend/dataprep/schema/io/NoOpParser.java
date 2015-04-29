package org.talend.dataprep.schema.io;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.schema.SchemaParser;

@Service("parser#any")
class NoOpParser implements SchemaParser {

    @Override
    public List<ColumnMetadata> parse(InputStream content, DataSetMetadata metadata) {
        return Collections.emptyList();
    }
}
