package org.talend.dataprep.schema.csv;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataprep.api.type.Type;

public class CSVFastHeaderAndTypeAnalysisTest {

    @Test
    public void should_neither_detect_type_nor_first_line_header_when_sample_is_empty() {
        // given
        String firstRecord = "";
        List<String> list = Arrays.asList(firstRecord);
        Separator separator = new Separator(';');

        // when
        CSVFastHeaderAndTypeAnalysis analysis = new CSVFastHeaderAndTypeAnalysis(list, separator);

        // then
        List<Type> expectedTypes = Arrays.asList();
        Assert.assertArrayEquals(expectedTypes.toArray(), analysis.getHeaders().values().toArray());
        Assert.assertFalse(analysis.isFirstLineAHeader());
    }

    @Test
    public void should_detect_type_without_first_line_header_when_sample_has_one_line() {
        // given
        String firstRecord = "0001;Toto; Hello;1,000.02";
        List<String> list = Arrays.asList(firstRecord);
        Separator separator = new Separator(';');

        // when
        CSVFastHeaderAndTypeAnalysis analysis = new CSVFastHeaderAndTypeAnalysis(list, separator);

        // then
        List<Type> expectedTypes = Arrays.asList(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE);
        Assert.assertArrayEquals(expectedTypes.toArray(), analysis.getHeaders().values().toArray());
        Assert.assertFalse(analysis.isFirstLineAHeader());
    }

    @Test
    public void should_detect_type_without_first_line_header_when_sample_has_two_lines_without_header() {
        // given
        String firstRecord = "0001;Toto; Hello;1,000.02";
        String secondRecord = "0001;Toto; Hello;1,000.02";
        List<String> list = Arrays.asList(firstRecord, secondRecord);
        Separator separator = new Separator(';');

        // when
        CSVFastHeaderAndTypeAnalysis analysis = new CSVFastHeaderAndTypeAnalysis(list, separator);

        // then
        List<Type> expectedTypes = Arrays.asList(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE);
        Assert.assertArrayEquals(expectedTypes.toArray(), analysis.getHeaders().values().toArray());
        Assert.assertFalse(analysis.isFirstLineAHeader());
    }

    @Test
    public void should_detect_type_without_first_line_header_when_sample_has_ten_lines_without_header() {
        // given
        String firstRecord = "Toto; Hello;";
        String record2 = "Toto; Hello;";
        String record3 = "Toto; Hello;";
        String record4 = "Toto; Hello;";
        String record5 = "Toto; Hello;1;0";
        String record6 = "Toto; 2;1;2";
        String record7 = "Toto; 3;1";
        String record8 = "Toto; 4;1";
        String record9 = "Toto; 2;1";
        String record10 = "Toto;2;2; Hello;";
        List<String> list = Arrays.asList(firstRecord, record2, record3, record4, record5, record6, record7, record8, record9,
                record10);
        Separator separator = new Separator(';');

        // when
        CSVFastHeaderAndTypeAnalysis analysis = new CSVFastHeaderAndTypeAnalysis(list, separator);

        // then
        List<Type> expectedTypes = Arrays.asList(Type.STRING, Type.STRING, Type.INTEGER, Type.STRING);
        Assert.assertArrayEquals(expectedTypes.toArray(), analysis.getHeaders().values().toArray());
        Assert.assertFalse(analysis.isFirstLineAHeader());
    }

    @Test
    public void should_detect_type_without_first_line_header_when_sample_has_a_header_plus_one_line() {
        // given
        String firstRecord = "ID; Name; Welcome message; Salary";
        String secondRecord = "0001;Toto; Hello;1,000.02";
        List<String> list = Arrays.asList(firstRecord, secondRecord);
        Separator separator = new Separator(';');
        separator.incrementCount(1);

        // when
        CSVFastHeaderAndTypeAnalysis analysis = new CSVFastHeaderAndTypeAnalysis(list, separator);

        // then
        List<Type> expectedTypes = Arrays.asList(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE);
        Assert.assertArrayEquals(expectedTypes.toArray(), analysis.getHeaders().values().toArray());
        Assert.assertTrue(analysis.isFirstLineAHeader());
    }

    @Test
    public void TDP_289_first_case() {
        // given
        String firstRecord = "1; Abdelaziz; CA;";
        String secondRecord = "2; Charles; MI;";
        List<String> list = Arrays.asList(firstRecord, secondRecord);
        Separator separator = new Separator(';');
        separator.incrementCount(1);

        // when
        CSVFastHeaderAndTypeAnalysis analysis = new CSVFastHeaderAndTypeAnalysis(list, separator);

        // then
        List<String> expectedHeader = Arrays.asList("COL1", "COL2", "COL3");
        List<Type> expectedTypes = Arrays.asList(Type.INTEGER, Type.STRING, Type.STRING);
        Assert.assertArrayEquals(expectedTypes.toArray(), analysis.getHeaders().values().toArray());
        Assert.assertFalse(analysis.isFirstLineAHeader());
    }
}