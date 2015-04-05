package org.talend.dataprep.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataprep.api.dataset.ColumnMetadata;

public class XlsFormatGuess implements FormatGuess {

    public static final String MEDIA_TYPE = "application/vnd.ms-excel";

    private final Logger       logger     = LoggerFactory.getLogger(getClass());

    public XlsFormatGuess() {
        // no op
    }

    @Override
    public String getMediaType() {
        return MEDIA_TYPE;
    }

    @Override
    public float getConfidence() {
        return 1;
    }

    @Override
    public SchemaParser getSchemaParser() {

        return content -> {

            try {
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(content);

                List<ColumnMetadata> columnMetadatas = new ArrayList<>();



                return columnMetadatas;
            } catch (IOException e) {
                logger.debug("IOEXception during parsing xls content :" + e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        };
    }

    @Override
    public Serializer getSerializer() {
        return null;
    }
}
