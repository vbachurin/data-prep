package org.talend.dataprep.transformation.api.transformer.suggestion.rules;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.transformer.suggestion.SuggestionEngineRule;

@Component
public class DummyRule implements SuggestionEngineRule {

    @Override
    public Integer apply(ActionMetadata actionMetadata, ColumnMetadata columnMetadata) {
        return 0;
    }
}
