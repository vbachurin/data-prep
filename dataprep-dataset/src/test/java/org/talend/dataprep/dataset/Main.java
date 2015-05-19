package org.talend.dataprep.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetRow;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream expected = DataSetServiceTests.class.getResourceAsStream("avengers_expected.json");
        final JsonParser parser = mapper.getFactory().createParser(expected);
        final DataSet dataSet = mapper.reader(DataSet.class).readValue(parser);
        System.out.println("dataSet = " + dataSet);
        final Stream<DataSetRow> stream = dataSet.getRecords();
        stream.parallel().limit(2).forEach(row -> System.out.println("row : " + row.values()));
        parser.close();
    }
}