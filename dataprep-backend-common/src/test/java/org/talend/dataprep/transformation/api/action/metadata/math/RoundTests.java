package org.talend.dataprep.transformation.api.action.metadata.math;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.transformation.api.action.ActionTestWorkbench;
import org.talend.dataprep.transformation.api.action.metadata.AbstractMetadataBaseTest;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;

public abstract class RoundTests extends AbstractMetadataBaseTest {

    protected Map<String, String> parameters;

    protected void testCommon(String input, String expected) {
        // given
        final Map<String, String> values = new HashMap<>();
        values.put("0000", input);
        final DataSetRow row = new DataSetRow(values);

        // when
        ActionTestWorkbench.test(row, factory.create(getAction(), parameters));

        // then
        assertEquals(expected, row.get("0000"));
    }

    protected abstract ActionMetadata getAction();

}
