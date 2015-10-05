package org.talend.dataprep.transformation.api.transformer.suggestion;

import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;
import org.talend.dataprep.transformation.api.transformer.suggestion.model.Suggestion;

@Component
public class DefaultSuggestionEngine implements SuggestionEngine {

    @Autowired
    private List<DefaultSuggestionEngineRule> rules;

    @PostConstruct
    public void init() {
    }

    @Override
    public List<Suggestion> score(List<ActionMetadata> actions, ColumnMetadata column) {
        return null;
    }

    interface DefaultSuggestionEngineRule extends BiFunction<ActionMetadata, ColumnMetadata, Integer> {
    }
}
