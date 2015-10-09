package org.talend.dataprep.transformation.api.transformer.suggestion.rules;

import static org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule.POSITIVE;
import static org.talend.dataprep.transformation.api.transformer.suggestion.rules.GenericRule.GenericRuleBuilder.forActions;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.talend.dataprep.transformation.api.action.metadata.date.ChangeDatePattern;
import org.talend.dataprep.transformation.api.action.metadata.date.ComputeTimeSince;
import org.talend.dataprep.transformation.api.action.metadata.date.ExtractDateTokens;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule;

@Component
public class TypeDomainRules extends BasicRules {

    /**
     * @return A {@link SuggestionEngineRule rule} that shows date actions if column is a date column.
     */
    @Bean
    public static SuggestionEngineRule dateRule() {
        return forActions(ExtractDateTokens.ACTION_NAME, ChangeDatePattern.ACTION_NAME, ComputeTimeSince.TIME_SINCE_ACTION_NAME) //
                .when(IS_DATE) //
                .then(columnMetadata -> POSITIVE) //
                .build();
    }

}
