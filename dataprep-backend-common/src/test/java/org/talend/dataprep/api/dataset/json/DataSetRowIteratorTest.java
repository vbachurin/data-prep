package org.talend.dataprep.api.dataset.json;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;

/**
 * Unit test for the DataSetRowIterator.
 * @see DataSetRowIterator
 */
public class DataSetRowIteratorTest {


    @Test
    public void should_iterate_row() throws IOException {

        // given
        List<DataSetRow> expectedRows = new ArrayList<>();
        expectedRows.add(getDataSetRow("Sheriff Woody", "Tom Hanks", "1995–present"));
        expectedRows.add(getDataSetRow("Buzz Lightyear", "", "1995–present"));
        expectedRows.add(getDataSetRow("Mr. Potato Head", "Don Rickles", "1995–present"));

        // when
        InputStream json = DataSetRowIteratorTest.class.getResourceAsStream("datasetrow.json");
        DataSetRowIterator iterator = new DataSetRowIterator(json);

        List<DataSetRow> actual = new ArrayList<>();
        while(iterator.hasNext()) {
            actual.add(iterator.next().clone());
        }

        // then
        Assert.assertEquals(expectedRows, actual);

    }

    private DataSetRow getDataSetRow(String... data) {
        DecimalFormat format = new DecimalFormat("0000");
        Map<String, String> values = new HashMap<>();
        for (int i=0; i<data.length;i++) {
            values.put(format.format(i), data[i]);
        }
        return new DataSetRow(values);
    }
}
