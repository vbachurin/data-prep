package org.talend.dataprep.dataset.service.serializer;

import static org.junit.Assert.assertThat;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.api.dataset.DataSetMetadata.Builder.metadata;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.schema.io.XlsSerializer;

public class XlsSerializerTest {

    private final XlsSerializer serializer = new XlsSerializer();

    /**
     * XlsSerializer should follow the data format as set in the Excel file. This test ensures XlsSerializer follows the
     * data format as defined and don't directly use {@link Cell#getNumericCellValue()}.
     * 
     * @throws Exception
     * @see org.talend.dataprep.schema.io.XlsUtils#getCellValueAsString(Cell)
     */
    @Test
    public void testGeneralNumberFormat() throws Exception {
        final DataSetMetadata metadata = metadata().id("1234")
                .row(column().name("id").id(0).type(Type.INTEGER), column().name("value1").id(1).type(Type.INTEGER)).build();
        final InputStream input = this.getClass().getResourceAsStream("/excel_numbers.xls");
        final String result = IOUtils.toString(serializer.serialize(input, metadata));
        final String expected = "[{\"0000\":\"1\",\"0001\":\"123\"},{\"0000\":\"2\",\"0001\":\"123,1\"},{\"0000\":\"3\",\"0001\":\"209,9\"}]";
        assertThat(result, sameJSONAs(expected));
    }
}
