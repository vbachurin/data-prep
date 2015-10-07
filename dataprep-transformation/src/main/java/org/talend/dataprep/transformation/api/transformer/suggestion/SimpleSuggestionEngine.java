package org.talend.dataprep.transformation.api.transformer.suggestion;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.transformation.api.action.metadata.common.ActionMetadata;

@Component
public class SimpleSuggestionEngine implements SuggestionEngine {

    @Autowired(required = false)
    private List<SuggestionEngineRule> rules;

    @Override
    public List<Suggestion> score(Collection<ActionMetadata> actions, ColumnMetadata column) {
        return actions.stream() //
                .map(actionMetadata -> new Suggestion(actionMetadata, 0)) //
                .collect(Collectors.toList());
    }

    @Override
    public List<ActionMetadata> suggest(DataSet dataSet) {
        return Collections.emptyList();
    }

}
