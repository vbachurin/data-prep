package org.talend.dataprep.schema.io;

import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
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
    public SchemaParserResult parse(Request request) {
        List<SchemaParserResult.SheetContent> sheetContents = new ArrayList<>();
        sheetContents.add(new SchemaParserResult.SheetContent(META_KEY, new ArrayList<>()));
        try {
            final Map<String, String> parameters = request.getMetadata().getContent().getParameters();
            final char separator = parameters.get(CSVFormatGuess.SEPARATOR_PARAMETER).charAt(0);
            CSVReader reader = new CSVReader(new InputStreamReader(request.getContent()), separator);
            // First line has column names
            String[] columns = reader.readNext();
            if (columns == null) { // Empty content?
                return SchemaParserResult.Builder.parserResult() //
                        .sheetContents( sheetContents ).build();
            }
            // By default, consider all columns as Strings (to be refined by deeper analysis).
            for (String column : columns) {
                sheetContents.stream().filter( sheetContent -> { return META_KEY.equals( sheetContent.getName()); } )
                    .findFirst() //
                    .get().getColumnMetadatas() //
                    .add( column().name( column ).type( Type.STRING ).build() );
            }

            // Best guess (and naive) on data types
            String[] line;
            while ((line = reader.readNext()) != null) {
                for (int i = 0; i < line.length; i++) {
                    String columnValue = line[i];
                    try {
                        Integer.parseInt(columnValue);
                        sheetContents.stream().filter( sheetContent -> { return META_KEY.equals( sheetContent.getName()); } )
                            .findFirst() //
                            .get().getColumnMetadatas().get(i).setType(Type.INTEGER.getName());
                    } catch (NumberFormatException e) {
                        // Not an number
                    }
                    if ("true".equalsIgnoreCase(columnValue.trim()) || "false".equalsIgnoreCase(columnValue.trim())) {
                        sheetContents.stream().filter( sheetContent -> { return META_KEY.equals( sheetContent.getName()); } )
                            .findFirst() //
                            .get().getColumnMetadatas().get(i).setType(Type.BOOLEAN.getName());
                    }
                }
            }

        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_READ_CONTENT, e);
        }
        return SchemaParserResult.Builder.parserResult() //
                .sheetContents(sheetContents) //
                .draft(false).build();
    }
}
