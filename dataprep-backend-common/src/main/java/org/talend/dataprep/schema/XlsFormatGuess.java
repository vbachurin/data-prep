package org.talend.dataprep.schema;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class XlsFormatGuess implements FormatGuess {

    private HSSFWorkbook hssfWorkbook;

    public XlsFormatGuess(HSSFWorkbook hssfWorkbook) {
        this.hssfWorkbook = hssfWorkbook;
    }

    @Override
    public String getMediaType() {
        return "application/vnd.ms-excel";
    }

    @Override
    public float getConfidence() {
        return 1;
    }

    @Override
    public SchemaParser getSchemaParser() {

        return null;
    }

    @Override
    public Serializer getSerializer() {
        return null;
    }
}
