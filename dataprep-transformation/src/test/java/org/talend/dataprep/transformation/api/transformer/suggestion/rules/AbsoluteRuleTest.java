package org.talend.dataprep.transformation.api.transformer.suggestion.rules;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.talend.dataprep.api.dataset.ColumnMetadata.Builder.column;
import static org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule.*;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.math.AbsoluteFloat;
import org.talend.dataprep.transformation.api.action.metadata.math.AbsoluteInt;
import org.talend.dataprep.transformation.api.action.metadata.math.Ceil;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule;

public class AbsoluteRuleTest {

    private SuggestionEngineRule absoluteRule;

    private ColumnMetadata positiveIntColumn = column().type(Type.INTEGER).build();

    private ColumnMetadata negativeIntColumn = column().type(Type.INTEGER).build();

    private ColumnMetadata positiveFloatColumn = column().type(Type.FLOAT).build();

    private ColumnMetadata negativeFloatColumn = column().type(Type.FLOAT).build();

    private ColumnMetadata stringColumn = column().type(Type.STRING).build();

    @Before
    public void setUp() throws Exception {
        absoluteRule = BasicRules.absoluteRule();
        negativeIntColumn.getStatistics().setMin(-1);
        negativeFloatColumn.getStatistics().setMin(-1);
    }

    @Test
    public void testOtherAction() throws Exception {
        assertThat(absoluteRule.apply(new Ceil(), positiveIntColumn), is(NON_APPLICABLE));
        assertThat(absoluteRule.apply(new Ceil(), negativeIntColumn), is(NON_APPLICABLE));
    }

    @Test
    public void testNegativeMatch() throws Exception {
        assertThat(absoluteRule.apply(new AbsoluteFloat(), positiveIntColumn), is(NEGATIVE));
        assertThat(absoluteRule.apply(new AbsoluteFloat(), positiveFloatColumn), is(NEGATIVE));
        assertThat(absoluteRule.apply(new AbsoluteInt(), positiveFloatColumn), is(NEGATIVE));
        assertThat(absoluteRule.apply(new AbsoluteInt(), positiveIntColumn), is(NEGATIVE));
    }

    @Test
    public void testPositiveMatch() throws Exception {
        assertThat(absoluteRule.apply(new AbsoluteFloat(), negativeIntColumn), is(POSITIVE));
        assertThat(absoluteRule.apply(new AbsoluteFloat(), negativeFloatColumn), is(POSITIVE));
        assertThat(absoluteRule.apply(new AbsoluteInt(), negativeIntColumn), is(POSITIVE));
        assertThat(absoluteRule.apply(new AbsoluteInt(), negativeFloatColumn), is(POSITIVE));
    }

    @Test
    public void testIgnoreRule() throws Exception {
        assertThat(absoluteRule.apply(new AbsoluteInt(), stringColumn), is(NON_APPLICABLE));
        assertThat(absoluteRule.apply(new AbsoluteFloat(), stringColumn), is(NON_APPLICABLE));
    }
}