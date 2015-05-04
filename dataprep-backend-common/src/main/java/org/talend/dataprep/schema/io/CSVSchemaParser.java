package org.talend.dataprep.schema.io;

import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.exception.CommonMessages;
import org.talend.dataprep.exception.Exceptions;
import org.talend.dataprep.schema.CSVFormatGuess;
import org.talend.dataprep.schema.SchemaParser;

import au.com.bytecode.opencsv.CSVReader;
import org.talend.dataprep.schema.SchemaParserResult;

@Service("parser#csv")
public class CSVSchemaParser implements SchemaParser {

    @Override
    public SchemaParserResult parse(InputStream content, DataSetMetadata metadata) {
        List<ColumnMetadata> columnMetadata = new LinkedList<>();
        try {
            final Map<String, String> parameters = metadata.getContent().getParameters();
            final char separator = parameters.get(CSVFormatGuess.SEPARATOR_PARAMETER).charAt(0);
            CSVReader reader = new CSVReader(new InputStreamReader(content), separator);
            // First line has column names
            String[] columns = reader.readNext();
            if (columns == null) { // Empty content?
                return SchemaParserResult.Builder.parserResult() //
                    .columnMetadatas( columnMetadata).build();
            }
            // By default, consider all columns as Strings (to be refined by deeper analysis).
            for (String column : columns) {
                columnMetadata.add(column().name(column).type(Type.STRING).build());
            }
            // Best guess (and naive) on data types
            String[] line;
            while ((line = reader.readNext()) != null) {
                for (int i = 0; i < line.length; i++) {
                    String columnValue = line[i];
                    try {
                        Integer.parseInt(columnValue);
                        columnMetadata.get(i).setType(Type.INTEGER.getName());
                    } catch (NumberFormatException e) {
                        // Not an number
                    }
                    if ("true".equalsIgnoreCase(columnValue.trim()) || "false".equalsIgnoreCase(columnValue.trim())) {
                        columnMetadata.get(i).setType(Type.BOOLEAN.getName());
                    }
                }
            }
        } catch (IOException e) {
            throw Exceptions.User(CommonMessages.UNABLE_TO_READ_CONTENT, e);
        }
        return SchemaParserResult.Builder.parserResult() //
            .columnMetadatas( columnMetadata).build();
    }
}
