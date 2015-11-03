package org.talend.dataprep.transformation.api.action.metadata.datablending;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.talend.dataprep.transformation.api.action.metadata.common.ImplicitParameters.COLUMN_ID;
import static org.talend.dataprep.transformation.api.action.metadata.datablending.Lookup.Parameters.LOOKUP_DS_URL;
import static org.talend.dataprep.transformation.api.action.metadata.datablending.Lookup.Parameters.LOOKUP_JOIN_ON;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;
import org.talend.dataprep.transformation.api.action.parameters.Parameter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test for the Lookup action.
 * 
 * @see Lookup
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { LookupTestApplication.class, LookupTestConfiguration.class })
@WebAppConfiguration
@IntegrationTest({ "server.port=0" })
public class LookupTest {

    @Value("${local.server.port}")
    public int port;

    /** The action to test. */
    @Autowired
    private Lookup action;

    /** DataPrep ready jackson builder. */
    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Test
    public void testAccept() throws Exception {
        assertFalse(action.acceptColumn(new ColumnMetadata()));
    }

    @Test
    public void shouldAdapt() {
        // given
        final DataSetMetadata ds = DataSetMetadata.Builder.metadata().name("great dataset").id("ds#123").build();
        String dsUrl = "http://estcequecestbientotleweekend.fr";

        // when
        action.adapt(ds, dsUrl);

        // when
        final List<Parameter> parameters = action.getParameters();
        assertEquals("great dataset", getParamValue(parameters, "lookup_ds_name"));
        assertEquals("ds#123", getParamValue(parameters, "lookup_ds_id"));
        assertEquals(dsUrl, getParamValue(parameters, "lookup_ds_url"));
    }

    @Test
    public void shouldMerge() throws IOException {
        // given
        Map<String, String> parameters = getUsStatesLookupParameters();
        DataSetRow row = ActionMetadataTestUtils.getRow("Atlanta", "GA", "Philips Arena");

        // when
        action.applyOnDataSet(row, new TransformationContext(), parameters);

        // then (check values)
        DataSetRow expected = ActionMetadataTestUtils.getRow("Atlanta", "GA", "Philips Arena", "Georgia", "Atlanta");
        Assert.assertEquals(expected, row);

        // and (check metadata)
        // TODO assertEquals(row.getRowMetadata().getById("0003").getDomain())
        // TODO assertEquals(row.getRowMetadata().getById("0003").getName())
        // TODO assertEquals(row.getRowMetadata().getById("0003").getType())
        checkMergedMetadata(row.getRowMetadata().getById("0003"), "us_states.json#1"); // (state) row#3 == us_states#1
        checkMergedMetadata(row.getRowMetadata().getById("0004"), "us_states.json#2"); // (capital) row#4 == us_states#2
    }

    @Test
    public void shouldMergeEmptyCellsOnMissingLookupData() throws IOException {
        // given
        Map<String, String> parameters = getUsStatesLookupParameters();
        DataSetRow row = ActionMetadataTestUtils.getRow("Toronto", "ON", "Air Canada Centre");

        // when
        action.applyOnDataSet(row, new TransformationContext(), parameters);

        // then (value)
        DataSetRow expected = ActionMetadataTestUtils.getRow("Toronto", "ON", "Air Canada Centre", "", "");
        Assert.assertEquals(expected, row);

        // and (metadata)
        checkMergedMetadata(row.getRowMetadata().getById("0003"), "us_states.json#1"); // (state) row#3 == us_states#1
        checkMergedMetadata(row.getRowMetadata().getById("0004"), "us_states.json#2"); // (capital) row#4 == us_states#2
    }

    @Test
    public void shouldMergeEmptyCellsOnMissingSourceData() throws IOException {
        // given
        Map<String, String> parameters = getUsStatesLookupParameters();
        DataSetRow row = ActionMetadataTestUtils.getRow("Huntington", "", "");

        // when
        action.applyOnDataSet(row, new TransformationContext(), parameters);

        // then (value)
        DataSetRow expected = ActionMetadataTestUtils.getRow("Huntington", "", "", "", "");
        Assert.assertEquals(expected, row);

        // and (metadata)
        checkMergedMetadata(row.getRowMetadata().getById("0003"), "us_states.json#1"); // (state) row#3 == us_states#1
        checkMergedMetadata(row.getRowMetadata().getById("0004"), "us_states.json#2"); // (capital) row#4 == us_states#2
    }

    /**
     * Check that the given column equals the one extracted from the source.
     *
     * @param mergedColumn the column merged by the lookup action
     * @param rawSource where to load the expected result (filename#colId)
     */
    private void checkMergedMetadata(ColumnMetadata mergedColumn, String rawSource) throws IOException {
        // split source and col id
        final int separatorIndex = rawSource.indexOf('#');
        String fileName = rawSource.substring(0, separatorIndex);
        int id = Integer.valueOf(rawSource.substring(separatorIndex + 1));

        final ColumnMetadata expectedState = getColumnMetadata(fileName, id, mergedColumn.getId());
        Assert.assertEquals(expectedState, mergedColumn);
    }

    /**
     * @return the wanted parameter value or null.
     */
    private String getParamValue(List<Parameter> parameters, String name) {
        return parameters.stream() //
                .filter(p -> StringUtils.equals(p.getName(), name)) //
                .map(Parameter::getDefault) //
                .findFirst() //
                .get();
    }

    /**
     * @return US States specific looup parameters.
     */
    private Map<String, String> getUsStatesLookupParameters() throws IOException {
        Map<String, String> parameters = ActionMetadataTestUtils
                .parseParameters(this.getClass().getResourceAsStream("lookupAction.json"));
        parameters.put(LOOKUP_DS_URL.getKey(), "http://localhost:" + port + "/test/lookup/us_states");
        parameters.put(LOOKUP_JOIN_ON.getKey(), "0000");
        parameters.put(COLUMN_ID.getKey(), "0001");
        return parameters;
    }

    /**
     * @return the wanted column with the given id
     */
    private ColumnMetadata getColumnMetadata(String inputName, int id, String idToSet) throws IOException {
        final ObjectMapper mapper = builder.build();
        final InputStream input = this.getClass().getResourceAsStream(inputName);
        try (JsonParser parser = mapper.getFactory().createParser(input)) {
            final DataSet dataSet = mapper.reader(DataSet.class).readValue(parser);
            final ColumnMetadata columnMetadata = dataSet.getColumns().get(id);
            columnMetadata.setId(idToSet); // reset column id (which is different in the merged dataset)
            return columnMetadata;
        }
    }
}