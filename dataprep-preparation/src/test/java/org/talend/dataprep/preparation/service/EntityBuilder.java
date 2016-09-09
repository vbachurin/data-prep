package org.talend.dataprep.preparation.service;

import java.util.Arrays;

import org.talend.dataprep.api.preparation.Action;
import org.talend.dataprep.api.preparation.AppendStep;
import org.talend.dataprep.api.preparation.MixedContentMap;
import org.talend.dataprep.api.preparation.StepDiff;

public class EntityBuilder {

    public static MixedContentMap paramsColAction(String colId, String colName) {
        return params("column_id", colId, "column_name", colName, "scope", "column");
    }

    public static MixedContentMap params(String... params) {
        MixedContentMap secondActionParams = new MixedContentMap();
        if (params.length > 0) {
            for (int i = 1; i < params.length; i += 2) {
                String param = params[i - 1];
                String value = params[i];
                secondActionParams.put(param, value);
            }
        }
        return secondActionParams;
    }

    public static AppendStep step(StepDiff diff, Action... actions) {
        AppendStep appendStep = new AppendStep();
        appendStep.setDiff(diff);
        appendStep.setActions(Arrays.asList(actions));
        return appendStep;
    }

    public static StepDiff diff(String... createdColumns) {
        StepDiff stepDiff = new StepDiff();
        stepDiff.setCreatedColumns(Arrays.asList(createdColumns));
        return stepDiff;
    }

    public static Action action(String secondActionName, MixedContentMap secondActionParams) {
        Action secondAction = new Action();
        secondAction.setName(secondActionName);
        secondAction.setParameters(secondActionParams);
        return secondAction;
    }

}
