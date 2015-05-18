package org.talend.dataprep.schema.io;

import java.util.Collections;

import org.springframework.stereotype.Service;
import org.talend.dataprep.schema.SchemaParser;
import org.talend.dataprep.schema.SchemaParserResult;

@Service("parser#any")
public class NoOpParser implements SchemaParser {

    @Override
    public SchemaParserResult parse(Request request) {
        return SchemaParserResult.Builder.parserResult() //
                .columnMetadatas(Collections.emptySortedMap()) //
                .draft(false).build();
    }
}
