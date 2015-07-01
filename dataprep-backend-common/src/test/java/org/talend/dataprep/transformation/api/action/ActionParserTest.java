package org.talend.dataprep.transformation.api.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;

/**
 * Unit test for the ActionParser class.
 * @see ActionParser
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ActionParserTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
@EnableAutoConfiguration
public class ActionParserTest {

    /** The bean to test. */
    @Autowired
    private ActionParser actionParser;

    @Test(expected = IllegalArgumentException.class)
    public void should_not_accept_null_actions() {
        actionParser.parse(null);
    }

    @Test(expected = TDPException.class)
    public void should_not_accept_invalid_actions() {
        actionParser.parse("blah blah blah");
    }

    @Test(expected = TDPException.class)
    public void should_not_accept_unknown_actions() throws IOException {
        String json = IOUtils.toString(ActionParserTest.class.getResourceAsStream("unknown_actions.json"));
        actionParser.parse(json);
    }

    @Test
    public void empty_string_should_return_noop_actions() {
        // given
        DataSetRow actualRow = getDataSetRow();
        DataSetRow expectedRow = actualRow.clone();

        RowMetadata actualMetadata = getRowMetadata();
        RowMetadata expectedMetadata = getRowMetadata();

        ParsedActions actualActions = actionParser.parse("");

        // when
        actualActions.getRowTransformer().accept(actualRow, new TransformationContext());
        actualActions.getMetadataTransformer().accept(actualMetadata, new TransformationContext());

        // then
        Assert.assertEquals(expectedRow, actualRow);
        Assert.assertEquals(expectedMetadata, actualMetadata);
    }

    @Test
    public void should_return_expected_actions() throws IOException {
        // given
        DataSetRow actualRow = getDataSetRow();
        RowMetadata actualMetadata = getRowMetadata();

        String json = IOUtils.toString(ActionParserTest.class.getResourceAsStream("actions.json"));
        ParsedActions actualActions = actionParser.parse(json);

        // when
        actualActions.getRowTransformer().accept(actualRow, new TransformationContext());
        actualActions.getMetadataTransformer().accept(actualMetadata, new TransformationContext());

        // then
        RowMetadata expectedMetadata = getRowMetadata();
        expectedMetadata.getById("0001").setName("blah blah blah");
        Assert.assertEquals(expectedMetadata, actualMetadata);

        DataSetRow expectedRow = getDataSetRow();
        expectedRow.values().put("0001", "TOTO");
        Assert.assertEquals(expectedRow, actualRow);
    }


    /**
     * @return a default dataset row.
     */
    private DataSetRow getDataSetRow() {
        Map<String, String> values = new HashMap<>();
        values.put("0001", "toto");
        values.put("0002", "123456");
        values.put("0003", "true");
        return new DataSetRow(values);
    }

    /**
     * @return a default RowMetadata.
     */
    private RowMetadata getRowMetadata() {
        List<ColumnMetadata> columns = new ArrayList<>();
        columns.add(ColumnMetadata.Builder.column().id(0).name("name").type(Type.STRING).build());
        columns.add(ColumnMetadata.Builder.column().id(1).name("count").type(Type.INTEGER).build());
        columns.add(ColumnMetadata.Builder.column().id(2).name("default").type(Type.BOOLEAN).build());
        return new RowMetadata(columns);
    }
}
