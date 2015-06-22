package org.talend.dataprep.transformation.api.action.metadata;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.talend.dataprep.transformation.api.action.metadata.ActionMetadataTestUtils.getColumn;

import java.io.IOException;

import org.junit.Test;
import org.talend.dataprep.api.type.Type;

/**
 * Unit test for the Cut action.
 * 
 * @see Cut
 */
public class CutTest {

    /** The action to test. */
    private Cut action;

    /**
     * Constructor.
     */
    public CutTest() throws IOException {
        action = new Cut();
    }

    @Test
    public void should_accept_column() {
        assertTrue(action.accept(getColumn(Type.STRING)));
    }

    @Test
    public void should_not_accept_column() {
        assertFalse(action.accept(getColumn(Type.NUMERIC)));
        assertFalse(action.accept(getColumn(Type.DOUBLE)));
        assertFalse(action.accept(getColumn(Type.FLOAT)));
        assertFalse(action.accept(getColumn(Type.INTEGER)));
        assertFalse(action.accept(getColumn(Type.DATE)));
        assertFalse(action.accept(getColumn(Type.BOOLEAN)));
    }
}
