package org.talend.dataprep.transformation.api.transformer.suggestion.rules;

import static org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule.NEGATIVE;
import static org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule.POSITIVE;
import static org.talend.dataprep.transformation.api.transformer.suggestion.rules.GenericRule.GenericRuleBuilder.forActions;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.statistics.PatternFrequency;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.transformation.api.action.metadata.math.*;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule;

@Component
public class BasicRules {

    private static final Predicate<ColumnMetadata> IS_NUMERIC = columnMetadata -> {
        final Type type = Type.get(columnMetadata.getType());
        return Type.NUMERIC.isAssignableFrom(type);
    };

    @Bean
    public static SuggestionEngineRule absoluteRule() {
        return forActions(AbsoluteFloat.ABSOLUTE_FLOAT_ACTION_NAME, AbsoluteInt.ABSOLUTE_INT_ACTION_NAME) //
                .when(IS_NUMERIC) //
                .then(columnMetadata -> {
                    if (columnMetadata.getStatistics().getMin() >= 0) {
                        return NEGATIVE;
                    } else {
                        return POSITIVE;
                    }
                }) //
                .build();
    }

    @Bean
    public static SuggestionEngineRule integerRule() {
        return forActions(Floor.FLOOR_ACTION_NAME, Round.ROUND_ACTION_NAME, Ceil.CELL_ACTION_NAME) //
                .when(IS_NUMERIC) //
                .then(columnMetadata -> {
                    final List<PatternFrequency> patterns = columnMetadata.getStatistics().getPatternFrequencies();
                    if (patterns.size() == 1 && patterns.get(0).getPattern().indexOf('.') > 0) {
                        return POSITIVE;
                    } else {
                        return NEGATIVE;
                    }
                }) //
                .build();
    }


}
