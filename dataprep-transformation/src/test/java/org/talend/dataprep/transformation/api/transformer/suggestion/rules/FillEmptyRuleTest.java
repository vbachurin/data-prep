package org.talend.dataprep.transformation.api.transformer.suggestion.rules;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule.*;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.fillempty.FillWithBooleanIfEmpty;
import org.talend.dataprep.transformation.api.action.metadata.fillempty.FillWithDateIfEmpty;
import org.talend.dataprep.transformation.api.action.metadata.fillempty.FillWithIntegerIfEmpty;
import org.talend.dataprep.transformation.api.action.metadata.fillempty.FillWithStringIfEmpty;
import org.talend.dataprep.transformation.api.action.metadata.text.ProperCase;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule;

public class FillEmptyRuleTest {

    private SuggestionEngineRule fillEmptyRule;

    private ColumnMetadata noEmptyColumn = column().type(Type.STRING).build();

    private ColumnMetadata mostEmptyColumn = column().type(Type.STRING).build();

    @Before
    public void setUp() throws Exception {
        fillEmptyRule = EmptyRules.fillEmptyRule();
        noEmptyColumn.getStatistics().setEmpty(0);
        mostEmptyColumn.getStatistics().setEmpty(10);
    }

    @Test
    public void testOtherAction() throws Exception {
        assertThat(fillEmptyRule.apply(new ProperCase(), noEmptyColumn), is(NON_APPLICABLE));
        assertThat(fillEmptyRule.apply(new ProperCase(), mostEmptyColumn), is(NON_APPLICABLE));
    }

    @Test
    public void testNegativeMatch() throws Exception {
        assertThat(fillEmptyRule.apply(new FillWithBooleanIfEmpty(), noEmptyColumn), is(NEGATIVE));
        assertThat(fillEmptyRule.apply(new FillWithDateIfEmpty(), noEmptyColumn), is(NEGATIVE));
        assertThat(fillEmptyRule.apply(new FillWithIntegerIfEmpty(), noEmptyColumn), is(NEGATIVE));
        assertThat(fillEmptyRule.apply(new FillWithStringIfEmpty(), noEmptyColumn), is(NEGATIVE));
    }

    @Test
    public void testPositiveMatch() throws Exception {
        assertThat(fillEmptyRule.apply(new FillWithBooleanIfEmpty(), mostEmptyColumn), is(POSITIVE));
        assertThat(fillEmptyRule.apply(new FillWithDateIfEmpty(), mostEmptyColumn), is(POSITIVE));
        assertThat(fillEmptyRule.apply(new FillWithIntegerIfEmpty(), mostEmptyColumn), is(POSITIVE));
        assertThat(fillEmptyRule.apply(new FillWithStringIfEmpty(), mostEmptyColumn), is(POSITIVE));
    }

}
