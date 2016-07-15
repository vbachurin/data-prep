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

package org.talend.dataprep.transformation.api.transformer.suggestion.rules;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.statistics.PatternFrequency;
import org.talend.dataprep.transformation.actions.math.*;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule;

import java.util.List;

import static org.talend.dataprep.transformation.actions.common.SuggestionLevel.*;
import static org.talend.dataprep.transformation.api.transformer.suggestion.rules.ColumnPredicates.isNumeric;
import static org.talend.dataprep.transformation.api.transformer.suggestion.rules.GenericRule.GenericRuleBuilder.forActions;

@Component
public class IntegerRules {

    /**
     * @return A {@link SuggestionEngineRule rule} that hides "absolute" actions if all numbers >=0.
     */
    @Bean
    public static SuggestionEngineRule absoluteRule() {
        return forActions(Absolute.ABSOLUTE_ACTION_NAME, DeleteNegativeValues.ACTION_NAME) //
                .when(isNumeric()) //
                .then(columnMetadata -> {
                    if (columnMetadata.getStatistics().getMin() >= 0) {
                        return NEGATIVE;
                    } else {
                        return MEDIUM;
                    }
                }) //
                .build();
    }

    /**
     * @return A {@link SuggestionEngineRule rule} that hides floor, ceil & round actions if all integers.
     */
    @Bean
    public static SuggestionEngineRule integerRule() {
        return forActions(RemoveFractionalPart.ACTION_NAME, RoundHalfUp.ACTION_NAME) //
                .when(isNumeric()) //
                .then(columnMetadata -> {
                    final List<PatternFrequency> patterns = columnMetadata.getStatistics().getPatternFrequencies();
                    for (PatternFrequency pattern : patterns) {
                        if (pattern.getPattern().indexOf('.') > 0) {
                            return LOW;
                        }
                    }
                    return NEGATIVE;
                }) //
                .build();
    }

    /**
     * @return A {@link SuggestionEngineRule rule} that suggests 'basic math actions on numeric columns
     */
    @Bean
    public static SuggestionEngineRule mathRule() {
        return forActions(NumericOperations.ACTION_NAME, CompareNumbers.ACTION_NAME) //
                .when(isNumeric()) //
                .then(columnMetadata -> MEDIUM) //
                .build();
    }

}
