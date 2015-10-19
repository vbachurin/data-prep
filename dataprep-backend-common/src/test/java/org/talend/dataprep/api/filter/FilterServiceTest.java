package org.talend.dataprep.api.filter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;

public class FilterServiceTest {

    FilterService service;

    DataSetRow row;

    @Before
    public void setUp() throws Exception {
        service = new SimpleFilterService();
        Map<String, String> values = new HashMap<String, String>() {
            {
                put("0000", "value");
                put("0001", "value with spaces");
                put("0002", "2");
            }
        };
        row = new DataSetRow(values);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFilter() throws Exception {
        service.build(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMalformedFilter() throws Exception {
        service.build("{}");
    }

    @Test
    public void testEmptyFilter() throws Exception {
        final Predicate<DataSetRow> predicate = service.build("");
        assertThat(predicate.test(row), is(true));
    }

    @Test
    public void testEquals() throws Exception {
        // Test match on "0000 = value"
        final Predicate<DataSetRow> matchPredicate = service.build("{\"eq\": {\"field\": \"0000\",\"value\": \"value\"}}");
        assertThat(matchPredicate.test(row), is(true));
        // Test non match on "0000 = value1"
        final Predicate<DataSetRow> nonMatchPredicate = service.build("{\"eq\": {\"field\": \"0000\",\"value\": \"value1\"}}");
        assertThat(nonMatchPredicate.test(row), is(false));
    }

    @Test
    public void testGreaterThan() throws Exception {
        // Test match on "0002 > 1"
        final Predicate<DataSetRow> matchPredicate = service.build("{\"gt\": {\"field\": \"0002\",\"value\": \"1\"}}");
        assertThat(matchPredicate.test(row), is(true));
        // Test non match on "0002 > 3"
        final Predicate<DataSetRow> nonMatchPredicate = service.build("{\"gt\": {\"field\": \"0002\",\"value\": \"3\"}}");
        assertThat(nonMatchPredicate.test(row), is(false));
    }

    @Test
    public void testGreaterThanOnString() throws Exception {
        // Test match on "0001 > 3"
        final Predicate<DataSetRow> matchPredicate = service.build("{\"gt\": {\"field\": \"0001\",\"value\": \"3\"}}");
        assertThat(matchPredicate.test(row), is(false));
    }

    @Test
    public void testGreaterThanEquals() throws Exception {
        // Test match on "0002 >= 2"
        final Predicate<DataSetRow> matchPredicate = service.build("{\"gte\": {\"field\": \"0002\",\"value\": \"2\"}}");
        assertThat(matchPredicate.test(row), is(true));
        // Test non match on "0002 >= 3"
        final Predicate<DataSetRow> nonMatchPredicate = service.build("{\"gte\": {\"field\": \"0002\",\"value\": \"3\"}}");
        assertThat(nonMatchPredicate.test(row), is(false));
    }

    @Test
    public void testGreaterThanEqualsOnString() throws Exception {
        // Test match on "0001 >= 2"
        final Predicate<DataSetRow> matchPredicate = service.build("{\"gte\": {\"field\": \"0001\",\"value\": \"2\"}}");
        assertThat(matchPredicate.test(row), is(false));
    }

    @Test
    public void testLessThan() throws Exception {
        // Test match on "0002 < 3"
        final Predicate<DataSetRow> matchPredicate = service.build("{\"lt\": {\"field\": \"0002\",\"value\": \"3\"}}");
        assertThat(matchPredicate.test(row), is(true));
        // Test non match on "0002 < 1"
        final Predicate<DataSetRow> nonMatchPredicate = service.build("{\"lt\": {\"field\": \"0002\",\"value\": \"1\"}}");
        assertThat(nonMatchPredicate.test(row), is(false));
    }

    @Test
    public void testLessThanOnString() throws Exception {
        // Test non match on "0002 < 1"
        final Predicate<DataSetRow> nonMatchPredicate = service.build("{\"lt\": {\"field\": \"0001\",\"value\": \"1\"}}");
        assertThat(nonMatchPredicate.test(row), is(false));
    }

    @Test
    public void testLessThanEquals() throws Exception {
        // Test match on "0002 <= 2"
        final Predicate<DataSetRow> matchPredicate = service.build("{\"lte\": {\"field\": \"0002\",\"value\": \"2\"}}");
        assertThat(matchPredicate.test(row), is(true));
        // Test non match on "0002 <= 1"
        final Predicate<DataSetRow> nonMatchPredicate = service.build("{\"lte\": {\"field\": \"0002\",\"value\": \"1\"}}");
        assertThat(nonMatchPredicate.test(row), is(false));
    }

    @Test
    public void testLessThanEqualsOnString() throws Exception {
        // Test non match on "0001 <= 2"
        final Predicate<DataSetRow> nonMatchPredicate = service.build("{\"lte\": {\"field\": \"0001\",\"value\": \"2\"}}");
        assertThat(nonMatchPredicate.test(row), is(false));
    }

}