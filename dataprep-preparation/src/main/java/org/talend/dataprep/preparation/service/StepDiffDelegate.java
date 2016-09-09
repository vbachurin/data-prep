package org.talend.dataprep.preparation.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.api.dataset.row.Flag;
import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.StepDiff;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

import static java.util.stream.Collectors.toList;

// TODO: find a good name
@Component
public class StepDiffDelegate {

    public StepDiff getActionCreatedColumns(RowMetadata metadata, List<Action> currentActions, List<Action> newActions) {
        RowMetadata workingMetadata = metadata.clone();
        compileActionOnMetadata(workingMetadata, currentActions);
        return getActionCreatedColumns(workingMetadata, newActions);
    }

    public StepDiff getActionCreatedColumns(RowMetadata metadataRef, List<Action> actions) {
        RowMetadata workingMetadata = metadataRef.clone();
        compileActionOnMetadata(workingMetadata, actions);

        workingMetadata.diff(metadataRef);

        List<String> createdColumnIds = workingMetadata.getColumns()
                .stream()
                .filter(c -> Flag.NEW.getValue().equals(c.getDiffFlagValue()))
                .map(ColumnMetadata::getId)
                .collect(toList());

        StepDiff stepDiff = new StepDiff();
        stepDiff.setCreatedColumns(createdColumnIds);
        return stepDiff;
    }

    private void compileActionOnMetadata(RowMetadata workingMetadata, List<Action> actions) {
        ActionContext contextWithMetadata = new ActionContext(null, workingMetadata);
        for (Action action : actions) {
            action.getRowAction().compile(contextWithMetadata);
        }
    }

}
