package org.talend.dataprep.schema.io;

import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.exception.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.schema.CSVFormatGuess;
import org.talend.dataprep.schema.SchemaParser;
import org.talend.dataprep.schema.SchemaParserResult;

import au.com.bytecode.opencsv.CSVReader;

@Service("parser#csv")
public class CSVSchemaParser implements SchemaParser {

    private static final String META_KEY = "key";

    @Override
    public SchemaParserResult parse(InputStream content, DataSetMetadata metadata) {
        SortedMap<String, List<ColumnMetadata>> columnMetadata = new TreeMap<>();
        columnMetadata.put(META_KEY, new ArrayList<>());
        try {
            final Map<String, String> parameters = metadata.getContent().getParameters();
            final char separator = parameters.get(CSVFormatGuess.SEPARATOR_PARAMETER).charAt(0);
            CSVReader reader = new CSVReader(new InputStreamReader(content), separator);
            // First line has column names
            String[] columns = reader.readNext();
            if (columns == null) { // Empty content?
                return SchemaParserResult.Builder.parserResult() //
                        .columnMetadatas(columnMetadata).build();
            }
            // By default, consider all columns as Strings (to be refined by deeper analysis).
            for (String column : columns) {
                columnMetadata.get(META_KEY).add(column().name(column).type(Type.STRING).build());
            }

            // Best guess (and naive) on data types
            String[] line;
            while ((line = reader.readNext()) != null) {
                for (int i = 0; i < line.length; i++) {
                    String columnValue = line[i];
                    try {
                        Integer.parseInt(columnValue);
                        columnMetadata.get(META_KEY).get(i).setType(Type.INTEGER.getName());
                    } catch (NumberFormatException e) {
                        // Not an number
                    }
                    if ("true".equalsIgnoreCase(columnValue.trim()) || "false".equalsIgnoreCase(columnValue.trim())) {
                        columnMetadata.get(META_KEY).get(i).setType(Type.BOOLEAN.getName());
                    }
                }
            }

        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_READ_CONTENT, e);
        }
        return SchemaParserResult.Builder.parserResult() //
                .columnMetadatas(columnMetadata).build();
    }
}
