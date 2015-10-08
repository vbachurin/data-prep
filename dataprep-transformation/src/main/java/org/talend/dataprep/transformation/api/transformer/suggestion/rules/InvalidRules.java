package org.talend.dataprep.transformation.api.transformer.suggestion.rules;

import static org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule.NEGATIVE;
import static org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule.POSITIVE;
import static org.talend.dataprep.transformation.api.transformer.suggestion.rules.GenericRule.GenericRuleBuilder.forActions;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.talend.dataprep.transformation.api.action.metadata.delete.DeleteInvalid;
import org.talend.dataprep.transformation.api.action.metadata.fillinvalid.FillWithBooleanIfInvalid;
import org.talend.dataprep.transformation.api.action.metadata.fillinvalid.FillWithDateIfInvalid;
import org.talend.dataprep.transformation.api.action.metadata.fillinvalid.FillWithNumericIfInvalid;
import org.talend.dataprep.transformation.api.action.metadata.fillinvalid.FillWithStringIfInvalid;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule;

@Component
public class InvalidRules extends BasicRules {

    /**
     * @return A {@link SuggestionEngineRule rule} that hides "delete invalid" if no invalid.
     */
    @Bean
    public static SuggestionEngineRule deleteInvalidRule() {
        return forActions(DeleteInvalid.DELETE_INVALID_ACTION_NAME) //
                .then(columnMetadata -> {
                    if (columnMetadata.getStatistics().getInvalid() > 0) {
                        return POSITIVE;
                    }
                    return NEGATIVE;
                }) //
                .build();
    }

    /**
     * @return A {@link SuggestionEngineRule rule} that hides "fill invalid" if no invalid.
     */
    @Bean
    public static SuggestionEngineRule fillInvalidRule() {
        return forActions(FillWithBooleanIfInvalid.FILL_EMPTY_ACTION_NAME, //
                FillWithDateIfInvalid.FILL_INVALID_ACTION_NAME, //
                FillWithNumericIfInvalid.FILL_INVALID_ACTION_NAME, //
                FillWithStringIfInvalid.FILL_INVALID_ACTION_NAME) //
                        .then(columnMetadata -> {
                            if (columnMetadata.getStatistics().getInvalid() > 0) {
                                return POSITIVE;
                            }
                            return NEGATIVE;
                        }) //
                        .build();
    }
}
