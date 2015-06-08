package org.talend.dataprep.transformation.api.action.metadata;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.type.Type;

/**
 * Unit test for the Cut action.
 * 
 * @see Cut
 */
public class CutTest {

    /** The action to test. */
    private Cut cutAction;

    /** The consumer out of the action. */
    private Consumer<DataSetRow> consumer;

    /**
     * Constructor.
     */
    public CutTest() throws IOException {
        cutAction = new Cut();

        String actions = IOUtils.toString(CutTest.class.getResourceAsStream("cutAction.json"));
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String content = actions.trim();
        JsonNode node = mapper.readTree(content);
        Map<String, String> parameters = cutAction.parseParameters(node.get("actions").get(0).get("parameters").getFields());//$NON-NLS-1$//$NON-NLS-2$
        consumer = cutAction.create(parameters);
    }

    @Test
    public void should_accept_column() {
        assertTrue(cutAction.accept(getColumn(Type.STRING)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(cutAction.accept(getColumn(Type.NUMERIC)));
        assertFalse(cutAction.accept(getColumn(Type.DOUBLE)));
        assertFalse(cutAction.accept(getColumn(Type.FLOAT)));
        assertFalse(cutAction.accept(getColumn(Type.INTEGER)));
        assertFalse(cutAction.accept(getColumn(Type.DATE)));
        assertFalse(cutAction.accept(getColumn(Type.BOOLEAN)));
    }
}
