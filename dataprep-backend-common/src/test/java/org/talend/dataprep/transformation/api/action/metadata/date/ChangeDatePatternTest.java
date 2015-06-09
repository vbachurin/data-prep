package org.talend.dataprep.transformation.api.action.metadata.date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Test;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.context.TransformationContext;
import org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils;

/**
 * Unit test for the ChangeDatePattern action.
 * 
 * @see ChangeDatePattern
 */
public class ChangeDatePatternTest {

    /** The row consumer to test. */
    private BiConsumer<DataSetRow, TransformationContext> rowClosure;

    /** The metadata consumer to test. */
    private Consumer<RowMetadata> metadataClosure;

    /** The action to test. */
    private ChangeDatePattern action;

    /**
     * Constructor.
     */
    public ChangeDatePatternTest() throws IOException {
        action = new ChangeDatePattern();

        Map<String, String> parameters = ActionMetadataTestUtils.parseParameters( //
                action, //
                ChangeDatePatternTest.class.getResourceAsStream("changeDatePatternAction.json"));

        rowClosure = action.create(parameters);
        metadataClosure = action.createMetadataClosure(parameters);
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.accept(getColumn(Type.DATE)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.accept(getColumn(Type.NUMERIC)));
        assertFalse(action.accept(getColumn(Type.FLOAT)));
        assertFalse(action.accept(getColumn(Type.STRING)));
        assertFalse(action.accept(getColumn(Type.BOOLEAN)));
    }
}
