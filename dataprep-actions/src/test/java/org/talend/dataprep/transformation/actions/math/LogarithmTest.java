//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.transformation.actions.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.talend.dataprep.transformation.actions.ActionMetadataTestUtils.getRow;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataprep.api.action.ActionDefinition;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.row.DataSetRow;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.actions.AbstractMetadataBaseTest;
import org.talend.dataprep.transformation.actions.ActionMetadataTestUtils;
import org.talend.dataprep.transformation.api.action.ActionTestWorkbench;

/**
 * Unit test for the Logarithm action.
 *
 * @see Logarithm
 */
public class LogarithmTest extends AbstractMetadataBaseTest {

    /** The action to test. */
    private Logarithm action = new Logarithm();

    /** The action parameters. */
    private Map<String, String> parameters;

    @Before
    public void setUp() throws Exception {
        final InputStream parametersSource = LogarithmTest.class.getResourceAsStream("logarithmAction.json");
        parameters = ActionMetadataTestUtils.parseParameters(parametersSource);
    }

    @Test
    public void logarithm_with_positive() {
        // given
        DataSetRow row = getRow("3", "3", "Done !");

        // when
        ActionTestWorkbench.test(row, actionRegistry, factory.create(action, parameters));

        // then
        assertColumnWithResultCreated(row);
        assertEquals("0.47712125471966244", row.get("0003"));
    }

    @Test
    public void logarithm_with_negative() {
        // given
        DataSetRow row = getRow("-3", "3", "Done !");

        // when
        ActionTestWorkbench.test(row, actionRegistry, factory.create(action, parameters));

        // then
        assertColumnWithResultCreated(row);
        assertEquals(StringUtils.EMPTY, row.get("0003"));
    }

    @Test
    public void logarithm_with_NaN() {
        // given
        DataSetRow row = getRow("beer", "3", "Done !");

        // when
        ActionTestWorkbench.test(row, actionRegistry, factory.create(action, parameters));

        // then
        assertColumnWithResultCreated(row);
        assertEquals(StringUtils.EMPTY, row.get("0003"));
    }

    @Test
    public void should_have_expected_behavior() {
        assertEquals(1, action.getBehavior().size());
        assertTrue(action.getBehavior().contains(ActionDefinition.Behavior.METADATA_CREATE_COLUMNS));
    }

    private void assertColumnWithResultCreated(DataSetRow row) {
        ColumnMetadata expected = ColumnMetadata.Builder.column().id(3).name("0000_logarithm").type(Type.STRING).build();
        ColumnMetadata actual = row.getRowMetadata().getById("0003");
        assertEquals(expected, actual);
    }

}
